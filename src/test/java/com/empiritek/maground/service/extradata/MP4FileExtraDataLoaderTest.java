package com.empiritek.maground.service.extradata;

import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.Serializable;
import java.util.Map;

import static org.junit.Assert.*;


@RunWith(SpringRunner.class)
@SpringBootTest
public class MP4FileExtraDataLoaderTest {

    @Autowired
    private MP4FileExtraDataLoader mp4FileMetadataExtractor;

    @Value("${ffmpeg.probe.exe:}")
    private String ffProbePath;

    @Before
    public void setFFProbePath() throws Exception {
        mp4FileMetadataExtractor.setFFProbePath(ffProbePath);
    }

    @Before
    public void checkOS() throws Exception {
        Assume.assumeTrue(isMacOS() || isLinux());
    }

    private boolean isMacOS() {
        return System.getProperty("os.name").toLowerCase().startsWith("mac os");
    }

    private boolean isLinux() {
        return System.getProperty("os.name").toLowerCase().startsWith("linux");
    }

    @Test
    public void testGetExtraData() throws Exception {
        String filePath = new File(this.getClass().getResource("test_video.mp4").toURI()).toString();
        Map<String, Serializable> metadata = mp4FileMetadataExtractor.getExtraData(filePath);
        assertNotNull(metadata);
        assertTrue(metadata.containsKey("duration"));
        Serializable duration = metadata.get("duration");
        assertTrue(duration instanceof Long);
        assertEquals(duration, 7L);
    }

}