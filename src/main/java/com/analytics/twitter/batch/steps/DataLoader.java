package com.analytics.twitter.batch.steps;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Random;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.separator.DefaultRecordSeparatorPolicy;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.batch.item.file.transform.IncorrectTokenCountException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.validation.BindException;

import com.analytics.twitter.model.Tweet;
import com.analytics.twitter.repository.TweetRepository;

@Service
public class DataLoader implements StepExecutionListener {
    private static final Random RANDOM = new Random();

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss",
            Locale.getDefault());
    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    private TweetRepository tweetRepository;
    @Autowired
    private JobRepository jobRepository;

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        return ExitStatus.COMPLETED;
    }

    public Step getStep(final String path) {
        return new StepBuilder("Persisting tweets", jobRepository)
                .<Tweet, Tweet>chunk(1000, transactionManager)
                .faultTolerant()
                .skip(FlatFileParseException.class)
                .noRetry(FlatFileParseException.class)
                .noRollback(FlatFileParseException.class)
                .skipLimit(60)
                .skip(IncorrectTokenCountException.class)
                .noRetry(IncorrectTokenCountException.class)
                .noRollback(IncorrectTokenCountException.class)
                .skipLimit(60)
                .reader(reader(path))
                .processor(item -> item)
                .writer(writer())
                .listener(this)
                // .taskExecutor(taskExecutor) Don't do this as file is not proper with one line
                // ending.
                .build();
    }

    private ItemWriter<Tweet> writer() {
        return new ItemWriter<Tweet>() {
            @Override
            public void write(Chunk<? extends Tweet> chunk) throws Exception {
                tweetRepository.saveAll(chunk.getItems());
            }
        };
    }

    private FlatFileItemReader<Tweet> reader(final String path) {
        return new FlatFileItemReaderBuilder<Tweet>()
                .name("Tweet-Item-Reader")
                .resource(new ClassPathResource(path))
                .delimited()
                // date,id,content,username,like_count,retweet_count
                .names(new String[] { "date", "id", "content", "username", "like_count", "retweet_count" })
                .strict(false)
                .recordSeparatorPolicy(new DefaultRecordSeparatorPolicy())
                .fieldSetMapper(new FieldSetMapper<Tweet>() {
                    @Override
                    public Tweet mapFieldSet(FieldSet fieldSet) throws BindException {
                        var date = fieldSet.readString("date");
                        date = date.substring(0, 19);
                        var localDate = LocalDate.parse(date, DATE_TIME_FORMATTER);
                        // ---------Randomizing------------------
                        int randomMonth = RANDOM.nextInt(12) + 1;
                        int month = localDate.getDayOfMonth() > 28 && randomMonth == 2 ? 3 : randomMonth;
                        localDate = LocalDate.of(localDate.getYear(), month, RANDOM.nextInt(28) + 1);

                        // ---------------------------------------
                        String content = fieldSet.readRawString("content");
                        int likes = fieldSet.readInt("like_count");
                        int retweets = fieldSet.readInt("retweet_count");
                        Tweet tweet = Tweet.builder()
                                .id(fieldSet.readLong("id"))
                                .date(localDate)
                                .content(content)
                                .userName(fieldSet.readString("username"))
                                .likeCount(likes)
                                .retweetCount(retweets)
                                .storage(content.length())
                                .engagementCount(1 + retweets + likes)
                                .build();
                        return tweet;
                    }
                })
                .linesToSkip(1)
                .build();
    }

}
