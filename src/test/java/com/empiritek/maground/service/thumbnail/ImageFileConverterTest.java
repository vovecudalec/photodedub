package com.empiritek.maground.service.thumbnail;

import com.empiritek.maground.service.AppConstants;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.im4java.core.Info;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ImageFileConverterTest {

    @Autowired
    private ImageFileConverter imageFileConverter;

    String lagreFileName;
    String middleFileName;
    String thumbFileName;

    @Test
    public void getLowQualityCopy() throws Exception {

        String in = new File(this.getClass().getResource("in.jpg").toURI()).toString();
        String outputPath = new File(this.getClass().getResource(".").toURI()).toString();

        imageFileConverter.getLowQualityCopy(in);

        String originalNameNoExtension = FilenameUtils.getBaseName(in);
        lagreFileName = imageFileConverter.constructFileName(outputPath, originalNameNoExtension, AppConstants.ImageSuffix.LARGE);
        middleFileName = imageFileConverter.constructFileName(outputPath, originalNameNoExtension, AppConstants.ImageSuffix.MIDDLE);
        thumbFileName = imageFileConverter.constructFileName(outputPath, originalNameNoExtension, AppConstants.ImageSuffix.THUMB);

        Info largeFileInfo = new Info(lagreFileName);
        Info middleFileInfo = new Info(middleFileName);
        Info thumbFileInfo = new Info(thumbFileName);

        Assert.assertTrue(largeFileInfo.getImageWidth() == 800);
        Assert.assertTrue(middleFileInfo.getImageWidth() == 400);
        Assert.assertTrue(thumbFileInfo.getImageWidth() == 250);

    }

    @After
    public void clear(){
        try {
            FileUtils.forceDelete(new File(lagreFileName));
            FileUtils.forceDelete(new File(middleFileName));
            FileUtils.forceDelete(new File(thumbFileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}