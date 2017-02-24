package com.empiritek.maground.service;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.empiritek.maground.repository.FileMetadataRepository;
import com.empiritek.maground.repository.FolderMetadataRepository;
import com.empiritek.maground.service.extradata.ExtraDataLoaderService;
import com.empiritek.maground.service.thumbnail.FileConversionService;
import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.*;


@Service
@SuppressWarnings({"SpringJavaAutowiringInspection", "SpringAutowiredFieldsWarningInspection"})
public class FileLoaderImpl implements FileLoader {

    private static final String FOLDER_STATUS_MESSAGE = "%s ----- %s";

    public static final List<String> HARDCODED_FOLDERS_TO_LOAD =
            Collections.unmodifiableList(Arrays.asList("360V", "HDR", "TR"));
    public static final String REQUIRED_TRACK_SUFFIX = "A_";

    private final Logger logger = LoggerFactory.getLogger(FileLoader.class);

    @Autowired
    FolderMetadataRepository folderMetadataRepository;
    @Autowired
    FileMetadataRepository fileMetadataRepository;
    @Autowired
    ExtraDataLoaderService extraDataLoaderService;
    @Autowired
    FileConversionService conversionService;
    @Autowired
    FolderService folderService;
    @Autowired
    FileService fileService;

    @Value("${files.rootFolder}")
    String ROOT_FOLDER;
    @Value("${files.storePath}")
    String STORE_PATH;


    // хэш загруженных файлов
    HashSet<String> imagesMDs = new HashSet<String>();

    @Override
    public void loadFoldersHierarchy(String path) throws IOException {


        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(STORE_PATH + File.separator + "loaded.hash"))) {
            imagesMDs = (HashSet<String>) inputStream.readObject();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            logger.warn("Файл не найден", e.getLocalizedMessage());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            logger.warn("Ошибка чтения файла хешей", e.getLocalizedMessage());
        }

        logLoadingMessage(path, String.format("\n\n--- %1$tF %1$tT START loading files from %2$s ---\n", new Date(), path));
        Files.walkFileTree(Paths.get(path),
                new SimpleFileVisitor<Path>() {

                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                            throws IOException {


                        try {
                            ImageMetadataReader.readMetadata(file.toFile());
                            String md5 = getMd5(file);
                            if (!imagesMDs.contains(md5)) {
                                copyFile(file);
                                imagesMDs.add(md5);
                            } else {
                                logger.warn("Обраружен дубль для файла {} ", file.toString());
                            }
                        } catch (ImageProcessingException | IOException e) {
                            logger.error("Error with file " + file.toString(), e);
                        } catch (Exception e){
                            logger.error("Error with file " + file.toString(), e);
                        }

                        int imagesCount = imagesMDs.size();
                        if (imagesCount % 100 == 0){
                            logger.info("Обработано {} файлов.", imagesCount);
                        }

                        return FileVisitResult.CONTINUE;
                    }


                    private void copyFile(Path file) throws ImageProcessingException, IOException {

                        Metadata metadata = ImageMetadataReader.readMetadata(file.toFile());


                        ExifSubIFDDirectory exifSubIFDDirectory = metadata.getDirectory(ExifSubIFDDirectory.class);
                        ExifIFD0Directory exifIFD0Directory = metadata.getDirectory(ExifIFD0Directory.class);
                        String dir = STORE_PATH;

                        if (exifSubIFDDirectory == null){
                            dir = dir + File.separator + "noExifOrigDate";
                            if (exifIFD0Directory == null) {
                                dir = dir + File.separator + "noexif";
                            } else {
                                Date date = exifIFD0Directory.getDate(ExifIFD0Directory.TAG_DATETIME);

                                if (date == null) {
                                    dir = dir + File.separator + "nodated";
                                }else {
                                    DateTime dateTime = new DateTime(date);
                                    dir = dir + File.separator
                                            + dateTime.getYear() + File.separator
                                            + getMonth(dateTime);
                                }
                            }
                        } else {

                            Date date = exifSubIFDDirectory.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);

                            if (date == null) {
                                dir = dir + File.separator + "nodated";
                            } else {

                                DateTime dateTime = new DateTime(date);

                                dir = dir + File.separator
                                        + dateTime.getYear() + File.separator
                                        + getMonth(dateTime);
                            }
                        }
                        FileUtils.copyFileToDirectory(file.toFile(), new File(dir));

                    }

                    private String getMonth(DateTime date) {

                        Month month = Month.of(date.getMonthOfYear());
                        Locale loc = Locale.forLanguageTag("ru");

                        String monthNum = String.format("%02d", month.getValue());

                        return monthNum + "_" + month.getDisplayName(TextStyle.FULL_STANDALONE, loc);
                    }

                }
        );

        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(STORE_PATH + File.separator + "loaded.hash"));
        out.writeObject(imagesMDs);
        out.close();
    }


    private String getMd5(Path path) throws IOException {
        return DigestUtils.md5DigestAsHex(Files.newInputStream(path));
    }

    private void logLoadingMessage(String path, String message) throws IOException {
        Path loadingStatusFile = Paths.get(Paths.get(path).getParent() + File.separator + "loadingStatus.txt");
        Files.write(loadingStatusFile,
                Collections.singletonList(message), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }

}
