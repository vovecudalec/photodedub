package com.empiritek.maground.service.thumbnail;

import com.empiritek.maground.service.AppConstants;
import com.empiritek.maground.service.FileLoader;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.job.FFmpegJob;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class ImageFileConverter implements FileConverter {

    private final Logger log = LoggerFactory.getLogger(FileLoader.class);

    @Value("${ffmpeg.probe.exe:}")
    private String ffProbePath;

    @Value("${ffmpeg.ffmpeg.exe:}")
    private String ffMpegPath;

    @Override
    public void getLowQualityCopy(String originalFilePath) throws IOException {

        if(!(new File(originalFilePath).exists())) {
            log.error("File with name [{}] does not exists.", originalFilePath);
            return;
        }

        String originalNameNoExtension = FilenameUtils.getBaseName(originalFilePath);
        String outputPath = FilenameUtils.getFullPath(originalFilePath);

        String largeFileName = constructFileName(outputPath, originalNameNoExtension, AppConstants.ImageSuffix.LARGE);
        String middleFileName = constructFileName(outputPath, originalNameNoExtension, AppConstants.ImageSuffix.MIDDLE);
        String thumbFileName = constructFileName(outputPath, originalNameNoExtension, AppConstants.ImageSuffix.THUMB);

        compressImage(originalFilePath, largeFileName, 800);
        compressImage(originalFilePath, middleFileName, 400);
        compressImage(originalFilePath, thumbFileName, 250);
    }

    @Override
    public List<String> getSupportedFileExtensions() {
        return Arrays.asList("jpg", "jpeg");
    }

    protected void compressImage(String in, String out, int w) throws IOException {

        FFprobe ffprobe = new FFprobe(ffProbePath);
        FFmpeg ffmpeg = new FFmpeg(ffMpegPath);

        FFmpegBuilder builder;
        builder = new FFmpegBuilder()
                .setInput(in)
                .overrideOutputFiles(true)
                .addOutput(out)
                .addExtraArgs("-vf", "scale=" + w + ":-2")
                .done();

        FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);
        FFmpegJob job = executor.createJob(builder);
        job.run();
    }
}
