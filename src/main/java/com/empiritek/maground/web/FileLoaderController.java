package com.empiritek.maground.web;


import com.empiritek.maground.service.FileLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;

@Controller
public class FileLoaderController {

    private final Logger logger = LoggerFactory.getLogger(FileLoaderController.class);

    @Autowired
    private FileLoader fileLoader;
    @Value("${files.inboxPath}")
    private String inboxPath;
    private boolean fileLoadingRunning = false;

    @RequestMapping("/file-loading/start")
    @ResponseBody
    @CrossOrigin
    public String startFileLoading() {
        String resultMessage;
        if (!fileLoadingRunning) {
            fileLoadingRunning = true;
            new Thread(() -> {
                try {
                    fileLoader.loadFoldersHierarchy(inboxPath);
                } catch (IOException e) {
                    logger.error(e.getLocalizedMessage(), e);
                } finally {
                    fileLoadingRunning = false;
                }
            }, "file-loading-by-request").start();
            resultMessage = "file loading is running\n";
        } else {
            resultMessage = "file loading already has ran\n";
        }
        return resultMessage;
    }

    @RequestMapping("/file-loading/status")
    @ResponseBody
    @CrossOrigin
    public String fileLoadingStatus() {
        return fileLoadingRunning ? "Running..." : "Not Running.";
    }
}
