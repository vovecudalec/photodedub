package com.empiritek.maground.service.extradata;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.Serializable;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PdfExtractorTest {

    @Autowired
    private PdfExtraDataLoader pdfExtraDataLoader;

    @Test
    public void testExtractPdf() throws Exception {
        String filePath = new File(this.getClass().getResource("hook.pdf").toURI()).toString();
        Map<String, Serializable> extraData = pdfExtraDataLoader.getExtraData(filePath);
        Assert.assertEquals(extraData.size(),20);
        Assert.assertEquals(extraData.get("area"), "Downtown");
    }

}
