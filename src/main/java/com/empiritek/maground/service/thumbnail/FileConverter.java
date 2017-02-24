package com.empiritek.maground.service.thumbnail;

import com.empiritek.maground.service.AppConstants;
import org.apache.commons.io.FilenameUtils;

import java.io.IOException;
import java.util.List;

public interface FileConverter {
    void getLowQualityCopy(String originalFilePath) throws IOException;

    default String constructFileName(String path, String baseName, String suffix) {
        return FilenameUtils.concat(path, baseName + suffix);
    }

    default String getOriginalNameNoSuffixExtension(String originalFilePath) {
        String originalNameNoSuffixExtension;
        if (originalFilePath.endsWith(AppConstants.VideoSuffix._4K)) {
            originalNameNoSuffixExtension = FilenameUtils.getName(originalFilePath).replace(AppConstants.VideoSuffix._4K, "");
        } else if (originalFilePath.endsWith(AppConstants.VideoSuffix._4k)) {
            originalNameNoSuffixExtension = FilenameUtils.getName(originalFilePath).replace(AppConstants.VideoSuffix._4k, "");
        } else {
            originalNameNoSuffixExtension = FilenameUtils.getName(originalFilePath).replace(AppConstants.VideoSuffix.SRC, "");
        }
        return originalNameNoSuffixExtension;
    }

    List<String> getSupportedFileExtensions();
}
