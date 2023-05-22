package com.analytics.twitter.controllers;

import com.analytics.twitter.services.WorkflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private WorkflowService workflowService;

    @GetMapping
    public ResponseEntity<String> test() {
        workflowService.startWorkflow("input.csv");
        return new ResponseEntity<>("Started the workflow!", HttpStatus.OK);
    }


}
