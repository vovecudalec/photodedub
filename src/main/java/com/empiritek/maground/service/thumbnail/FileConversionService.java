package com.empiritek.maground.service.thumbnail;

import com.empiritek.maground.service.AppConstants;
import com.empiritek.maground.service.FileLoader;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;

@Service
public class FileConversionService {
    protected Map<String, FileConverter> convertersMap = new HashMap<>();

    private final Logger logger = LoggerFactory.getLogger(FileLoader.class);

    public FileConversionService(List<FileConverter> fileExtraDataLoaders) {
        fileExtraDataLoaders.forEach(converter ->
                this.convertersMap.putAll(converter.getSupportedFileExtensions().stream().collect(toMap(ex -> ex, ex -> converter)))
        );
    }

    public void convert(String filePath) {
        String extension = FilenameUtils.getExtension(filePath);
        FileConverter converter = convertersMap.get(extension);
        try {
            converter.getLowQualityCopy(filePath);
        } catch (IOException e) {
            logger.error("File convertation error {} \n", filePath, e);
        }
    }

    public void convertFolder(Path path) {
        String folderName = path.getName(path.getNameCount() - 1).toString();
        List<File> children = Arrays.asList(path.toFile().listFiles());

        Map<String, File> fileNamesAndPaths = children.stream().collect(Collectors.toMap(File::getName, Function.identity()));

        String videoSrc = folderName + AppConstants.VideoSuffix.SRC;
        String video4K = folderName + AppConstants.VideoSuffix._4K;
        if(fileNamesAndPaths.containsKey(videoSrc)){
            convert(fileNamesAndPaths.get(videoSrc).getPath());
        } else if (fileNamesAndPaths.containsKey(video4K)){
            convert(fileNamesAndPaths.get(video4K).getPath());
        } else {
            logger.error("There are no files [{} or {}] in directory [{}]. Video convertation fail.", videoSrc, video4K, path.toString());
        }

        String imageSrc = folderName + AppConstants.ImageSuffix.SRC;
        if (fileNamesAndPaths.containsKey(imageSrc)){
            convert(fileNamesAndPaths.get(imageSrc).getPath());
        } else {
            logger.error("There is no file [{}] in directory [{}]. Image convertation fail.", imageSrc, path.toString());
        }

    }
}
