package com.analytics.twitter.batch.steps.handlers;

import static com.analytics.twitter.util.Converstion.getAs;

import java.sql.Date;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.analytics.twitter.model.HomeMonthStats;
import com.analytics.twitter.repository.HomeMonthStatRepository;
import com.analytics.twitter.repository.TweetRepository;

@Service
public class HomeScreenDataHandler {

    @Autowired
    private HomeMonthStatRepository targetRepo;
    @Autowired
    private TweetRepository tweetRepository;

    public void handle(int month) {
        List<List<Object>> row = tweetRepository.homeWidgetData(month);
        HomeMonthStats data = HomeMonthStats.buildFromList(row);
        data.setMonth(month);
        List<List<Object>> result = tweetRepository.getPerDayGraphsData(month);
        fillAllPerDayMetricsFields(result, data, month);
    }

    private void fillAllPerDayMetricsFields(List<List<Object>> results, HomeMonthStats data, int month) {
        Map<Long, Long> tweetsPerDay = new HashMap<>();
        Map<Long, Long> activeUsesPerDay = new HashMap<>();
        Map<Long, Long> storageUsagePerDay = new HashMap<>();
        for (List<Object> row : results) {
            long tweetCount = getAs(row.get(0), Long.class, 0l);
            long activeUserCount = getAs(row.get(1), Long.class, 0l);
            long storageUsage = getAs(row.get(2), Long.class, 0l);
            long date = ((Date) row.get(3)).getTime();
            tweetsPerDay.put(date, tweetCount);
            activeUsesPerDay.put(date, activeUserCount);
            storageUsagePerDay.put(date, storageUsage);
        }
        YearMonth yearMonthObject = YearMonth.of(2023, month);
        int daysInMonth = yearMonthObject.lengthOfMonth();

        for (int i = 1; i <= daysInMonth; i++) {
            long date = LocalDate.of(2023, month, i).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
            tweetsPerDay.computeIfAbsent(date, d -> 0l);
            activeUsesPerDay.computeIfAbsent(date, d -> 0l);
            storageUsagePerDay.computeIfAbsent(date, d -> 0l);
        }

        System.out.println(data);
        data.setActiveUsersPerDay(activeUsesPerDay);
        data.setTweetsPerDay(tweetsPerDay);
        data.setStorageUsagePerDay(storageUsagePerDay);

        var prev = targetRepo.findByMonth(month);
        if (!prev.isEmpty()) {
            targetRepo.deleteAll(prev);
        }

        targetRepo.save(data);

    }

}
