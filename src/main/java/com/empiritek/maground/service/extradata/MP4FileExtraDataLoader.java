package com.empiritek.maground.service.extradata;

import com.empiritek.maground.service.FileLoader;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import net.bramp.ffmpeg.probe.FFmpegStream;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;

@Component
class MP4FileExtraDataLoader implements FileExtraDataLoader {

    private final Logger logger = LoggerFactory.getLogger(FileLoader.class);

    @Value("${ffmpeg.probe.exe:}")
    private String ffProbePath;

    @Override
    public Map<String, Serializable> getExtraData(String filePath) throws IOException {
        HashMap<String, Serializable> metadata = new HashMap<>();

        if (StringUtils.isNotEmpty(ffProbePath)) {
            FFprobe fFprobe = new FFprobe(ffProbePath);
            FFmpegProbeResult probeResult = fFprobe.probe(filePath);
            Optional<FFmpegStream> videoStream = probeResult.getStreams().stream().filter(fFmpegStream -> fFmpegStream.codec_type.equals(FFmpegStream.CodecType.VIDEO)).findFirst();

            if (videoStream.isPresent()) {
                metadata.put("duration", (long) videoStream.get().duration);
                metadata.put("width", (long) videoStream.get().width);
                metadata.put("height", (long) videoStream.get().height);
                metadata.put("ratio", videoStream.get().display_aspect_ratio);
            }
        } else {
            logger.warn("ffmpeg.probe.exe is null. Extract metadata for file {} was skipped.", filePath);
        }

        return metadata;
    }

    @Override
    public List<String> getSupportedFileExtensions() {
        return Collections.singletonList("mp4");
    }


    void setFFProbePath(String ffProbePath) {
        this.ffProbePath = ffProbePath;
    }
}
