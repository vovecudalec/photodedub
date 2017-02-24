package com.empiritek.maground.service.extradata;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.Serializable;
import java.util.Map;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ExtraDataLoaderServiceTest {

    @Autowired
    private ExtraDataLoaderService extraDataLoaderService;
    @Autowired
    private MockFileExtraDataLoader mockFileExtraDataLoader;
    @Autowired
    private MP4FileExtraDataLoader mp4FileExtraDataLoader;
    @Autowired
    private PdfExtraDataLoader pdfExtraDataLoader;

    @Test
    public void testInitExtractors() throws Exception {
        assertEquals(extraDataLoaderService.extractorsMap.get("mock1"), mockFileExtraDataLoader);
        assertEquals(extraDataLoaderService.extractorsMap.get("mock2"), mockFileExtraDataLoader);
        assertEquals(extraDataLoaderService.extractorsMap.get("mock3"), mockFileExtraDataLoader);
        assertEquals(extraDataLoaderService.extractorsMap.get("mp4"), mp4FileExtraDataLoader);
        assertEquals(extraDataLoaderService.extractorsMap.get("pdf"), pdfExtraDataLoader);
    }

    @Test
    public void testExtractMetadataFromMockFile() throws Exception {
        String filePath = "/path_to_file/testFile.mock1";
        Map<String, Serializable> extraData = extraDataLoaderService.getExtraData(filePath);
        assertNotNull(extraData);
        assertTrue(extraData.containsKey("filePath"));
        assertEquals(extraData.get("filePath"), filePath);
    }

    @Test
    public void testExtractMetadataFromUnknownFile() throws Exception {
        String filePath = "/path_to_file/testFile.12345";
        Map<String, Serializable> extraData = extraDataLoaderService.getExtraData(filePath);
        assertNotNull(extraData);
        assertTrue(extraData.isEmpty());
    }
}
