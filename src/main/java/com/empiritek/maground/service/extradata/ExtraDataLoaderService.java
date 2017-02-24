package com.empiritek.maground.service.extradata;

import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

@Service
public class ExtraDataLoaderService {

    protected Map<String, FileExtraDataLoader> extractorsMap = new HashMap<>();

    public ExtraDataLoaderService(List<FileExtraDataLoader> fileExtraDataLoaders) {
        fileExtraDataLoaders.forEach(extractor ->
                this.extractorsMap.putAll(extractor.getSupportedFileExtensions().stream().collect(toMap(ex -> ex, ex -> extractor)))
        );
    }

    public Map<String, Serializable> getExtraData(String filePath) throws IOException {
        String extension = FilenameUtils.getExtension(filePath);
        FileExtraDataLoader metadataExtractor = extractorsMap.get(extension);
        return metadataExtractor != null ? metadataExtractor.getExtraData(filePath) : new HashMap<>();
    }
}
