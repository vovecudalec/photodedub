package com.empiritek.maground.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@SuppressWarnings("SpringAutowiredFieldsWarningInspection")
@Component
public class ScheduledTasks {

    private final Logger logger = LoggerFactory.getLogger(ScheduledTasks.class);

    @Autowired
    private FileLoader fileLoader;

    @Value("${files.inboxPath}")
    String INBOX_PATH;

//    @Scheduled(fixedRateString = "${files.loader.schedule.fixedRate:10000}")
    public void scanFolder() {
        logger.info("Scanning folder");
        try {
            fileLoader.loadFoldersHierarchy(INBOX_PATH);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}