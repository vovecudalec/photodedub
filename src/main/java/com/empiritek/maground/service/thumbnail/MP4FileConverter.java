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
import java.util.Collections;
import java.util.List;

@Component
public class MP4FileConverter implements FileConverter {

    private final Logger log = LoggerFactory.getLogger(FileLoader.class);

    @Value("${ffmpeg.probe.exe:}")
    private String ffProbePath;

    @Value("${ffmpeg.ffmpeg.exe:}")
    private String ffMpegPath;

    @Override
    public void getLowQualityCopy(String originalFilePath) throws IOException {

        if (!(new File(originalFilePath).exists())) {
            log.error("File with name [{}] does not exists.", originalFilePath);
            return;
        }
        String originalNameNoSuffixExtension = getOriginalNameNoSuffixExtension(originalFilePath);
        String outputPath = FilenameUtils.getFullPath(originalFilePath);

//        String _2kFileName = constructFileName(outputPath, originalNameNoSuffixExtension, AppConstants.VideoSuffix._2K);
        String lagreFileName = constructFileName(outputPath, originalNameNoSuffixExtension, AppConstants.VideoSuffix.LARGE);
        String middleFileName = constructFileName(outputPath, originalNameNoSuffixExtension, AppConstants.VideoSuffix.MIDDLE);
        String thumbFileName = constructFileName(outputPath, originalNameNoSuffixExtension, AppConstants.VideoSuffix.THUMB);

//        compressVideo(originalFilePath, _2kFileName, 1920, 1080, 236898000L));
        compressVideo(originalFilePath, lagreFileName, 800, 996000L);
        compressVideo(lagreFileName, middleFileName, 400, 498000L);
        compressVideo(middleFileName, thumbFileName, 250, 139000L);

        renameSrcFile(originalFilePath, constructFileName(outputPath, originalNameNoSuffixExtension, AppConstants.VideoSuffix._4K));
    }

    public String compressVideo(String originalFilePath, String lowQFilePath, int w, long bitrate) throws IOException {
        if (new File(lowQFilePath).exists()) {
            log.warn("File named [{}] already exists", lowQFilePath);
            return lowQFilePath;
        }
        FFmpeg ffmpeg = new FFmpeg(ffMpegPath);
        FFprobe ffprobe = new FFprobe(ffProbePath);

        FFmpegBuilder builder;
        builder = new FFmpegBuilder()
                .setInput(originalFilePath)
                .overrideOutputFiles(true)
                .addOutput(lowQFilePath)
                .setVideoCodec("libx264")
                .setVideoBitRate(bitrate)
                .addExtraArgs("-vf", "scale=" + w + ":-2")
                .addExtraArgs("-pix_fmt", "yuv420p")
                .done();

        FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);
        FFmpegJob job = executor.createJob(builder);
        job.run();
        if (job.getState() == FFmpegJob.State.FAILED) {
            log.error("FFmpegJob start Failed! Output file [{}] broken.", lowQFilePath);
        }
        return lowQFilePath;
    }

    private void renameSrcFile(String srcFilePath, String targetFileName) {
        if (srcFilePath.endsWith(AppConstants.VideoSuffix._4K) || srcFilePath.endsWith(AppConstants.VideoSuffix._4k)) {
            return;
        }
        File file = new File(srcFilePath);
        File file2 = new File(targetFileName);
        if (file2.exists()) {
            log.error("Cannot rename src file. Target fileName just exists. [{}]", targetFileName);
        }

        boolean success = file.renameTo(file2);

        if (success) {
            log.info("Source file was successfully renamed");
        } else {
            log.error("Cannot rename src file! [{}]", srcFilePath);
        }
    }


    @Override
    public List<String> getSupportedFileExtensions() {
        return Collections.singletonList("mov");
    }
}
