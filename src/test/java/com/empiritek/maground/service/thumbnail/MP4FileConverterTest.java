package com.empiritek.maground.service.thumbnail;

import com.empiritek.maground.service.AppConstants;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import net.bramp.ffmpeg.probe.FFmpegStream;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MP4FileConverterTest {

    @Value("${ffmpeg.probe.exe:}")
    private String ffProbePath;

    @Autowired
    private MP4FileConverter mp4FileConverter;

    String largeFileName;
    String middleFileName;
    String thumbFileName;
    String _2kFileName;
    String _4kFileName;

    @After
    public void clear() {
        try {
            FileUtils.forceDelete(new File(largeFileName));
            FileUtils.forceDelete(new File(middleFileName));
            FileUtils.forceDelete(new File(thumbFileName));
//            FileUtils.forceDelete(new File(_2kFileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getLowQualityCopy() throws Exception {
        String in = new File(this.getClass().getResource("in-4K.mov").toURI()).toString();
        String outputPath = new File(this.getClass().getResource(".").toURI()).toString();
        String oldDigest = getDigest(in);

        mp4FileConverter.getLowQualityCopy(in);

        String originalNameNoExtension = mp4FileConverter.getOriginalNameNoSuffixExtension(in);
        largeFileName = mp4FileConverter.constructFileName(outputPath, originalNameNoExtension, AppConstants.VideoSuffix.LARGE);
        middleFileName = mp4FileConverter.constructFileName(outputPath, originalNameNoExtension, AppConstants.VideoSuffix.MIDDLE);
        thumbFileName = mp4FileConverter.constructFileName(outputPath, originalNameNoExtension, AppConstants.VideoSuffix.THUMB);
        _2kFileName = mp4FileConverter.constructFileName(outputPath, originalNameNoExtension, AppConstants.VideoSuffix._2K);
        _4kFileName = mp4FileConverter.constructFileName(outputPath, originalNameNoExtension, AppConstants.VideoSuffix._4K);

        FFmpegStream stream;// = getMediaStream(_2kFileName);
//        Assert.assertTrue("Assert video resolution for 2K ", stream.width == 1920);
        stream = getMediaStream(largeFileName);
        Assert.assertTrue("Assert video resolution for large ",stream.width == 800);
        stream = getMediaStream(middleFileName);
        Assert.assertTrue("Assert video resolution for middle ",stream.width == 400);
        stream = getMediaStream(thumbFileName);
        Assert.assertTrue("Assert video resolution for thumb ",stream.width == 250);

        String newDigest = getDigest(_4kFileName);
        Assert.assertTrue("Assert file rename", StringUtils.equals(oldDigest, newDigest));
    }

    private FFmpegStream getMediaStream(String file) {
        FFprobe ffprobe = new FFprobe(ffProbePath);
        FFmpegProbeResult probeResult = null;
        try {
            probeResult = ffprobe.probe(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        FFmpegStream stream = probeResult.getStreams().get(0);

        return stream;
    }

    private String getDigest(String filePath) {
        String md5 = null;
        try (FileInputStream fis = new FileInputStream(new File(filePath))) {
            md5 = org.apache.commons.codec.digest.DigestUtils.md5Hex(fis);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return md5;
    }
}