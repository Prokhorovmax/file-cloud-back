package edu.prokhorovmax.bigdata.service;

import edu.prokhorovmax.bigdata.model.File;
import edu.prokhorovmax.bigdata.model.FileHash;
import edu.prokhorovmax.bigdata.model.Hash;
import edu.prokhorovmax.bigdata.repository.FileRepository;
import liquibase.util.file.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(rollbackFor = Exception.class)
public class FileServiceImpl implements FileService {

    private static final Logger logger = LoggerFactory.getLogger(FileServiceImpl.class.getName());

    @Value("${segment.size:4}")
    private int baseSegmentSize;

    @Value("${storage.file.size:512}")
    private int baseMaxStorageFileSize;

    @Value("${base.upload.path}")
    private String baseUploadPath;

    @Value("${base.download.path}")
    private String baseDownloadPath;

    @Value("${base.storage.directory}")
    private String baseStorageDir;

    private long lastUsedFile = -1;
    private int lastUsedLine = -1;

    private final FileRepository fileRepository;
    private final HashService hashService;
    private final FileHashService fileHashService;

    public FileServiceImpl(@Lazy FileRepository fileRepository,
                           @Lazy HashService hashService,
                           @Lazy FileHashService fileHashService) {
        this.fileRepository = fileRepository;
        this.hashService = hashService;
        this.fileHashService = fileHashService;
    }

    @PostConstruct
    private void updatePointers() {
        String storagePath = Paths.get(".").toAbsolutePath().getParent() + "\\" + baseStorageDir + "\\";
        java.io.File dir = new java.io.File(storagePath);
        java.io.File[] files = dir.listFiles();
        if (files == null || files.length == 0) {
            lastUsedFile = -1;
        } else {
            java.io.File lastModifiedFile = files[0];
            for (int i = 1; i < files.length; i++) {
                if (lastModifiedFile.lastModified() < files[i].lastModified()) {
                    lastModifiedFile = files[i];
                }
            }
            lastUsedFile = Long.parseLong(FilenameUtils.removeExtension(lastModifiedFile.getName())) + 1;
        }
        lastUsedLine = -1;
    }

    /**
     * Загрузить файл в хранилище
     *
     * @param fileName           имя файла
     * @param currentSegmentSize размер сагмента
     */
    @Override
    public void uploadFile(String fileName, int currentSegmentSize, int currentMaxStorageFileSize, String currentStoragePath) {
        byte[] bytes = null;
        try {
            bytes = Files.readAllBytes(Paths.get(fileName));
        } catch (OutOfMemoryError | IOException | SecurityException ex) {
            logger.error("Error during reading file", ex);
        }

        if (bytes != null) {
            File fileEntity = new File();
            fileEntity.setFileName(Paths.get(fileName).getFileName().toString());
            fileEntity.setDate(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
            fileRepository.create(fileEntity);

            // размер сегмента
            int segmentSize = (currentSegmentSize <= 0) ? baseSegmentSize : currentSegmentSize;
            // путь для сохранения сегментов
            String storagePath = (currentStoragePath.equals("")) ? baseUploadPath : currentStoragePath;
            // максимальный размер файлов хранилища сегментов
            int maxStorageFileSize = baseMaxStorageFileSize;
            if (currentMaxStorageFileSize > 0) {
                if (currentMaxStorageFileSize > maxStorageFileSize || currentMaxStorageFileSize > segmentSize) {
                    maxStorageFileSize = currentMaxStorageFileSize;
                }
            }
            if (maxStorageFileSize <= segmentSize) {
                maxStorageFileSize = segmentSize + 1;
            }

            // порядок сегмента в файле
            int number = 0;

            for (int i = 0; i < bytes.length; i += segmentSize) {
                //logger.info("Array copy");
                byte[] segment = Arrays.copyOfRange(bytes, i, i + segmentSize);
                //logger.info("Create hash");
                byte[] hash = hashService.createHash(segment);
                //logger.info("Find by hash");
                Hash hashEntity = hashService.findByHash(hash);
                if (hashEntity != null) {
                    //logger.info("Update");
                    hashEntity = hashService.createOrUpdate(hashEntity);
                } else {
                    hashEntity = new Hash();
                    hashEntity.setHash(hash);
                    //logger.info("Save segment");
                    saveSegment(hashEntity, segment, maxStorageFileSize, storagePath);
                    hashEntity = hashService.createOrUpdate(hashEntity);
                }
                fileHashService.create(fileEntity, hashEntity, number);
                number++;
                //logger.info("Next segment");
            }
        }
    }

    @Override
    public void clear() {
        fileRepository.clear();
    }

    @Override
    public List<File> getFileList() {
        logger.info("Get files request");
        return fileRepository.getFileList();
    }

    @Override
    public void testReset() {
        lastUsedFile = -1;
        lastUsedLine = -1;
    }

    @Override
    public void uploadFile(String fileName) {
        uploadFile(fileName, baseSegmentSize, baseMaxStorageFileSize, baseUploadPath);
    }


    /**
     * Загрузить файл из хранилища в указанное место (конечный файл)
     *
     * @param fileName           имя файла из таблицы файлов
     * @param destination        файл, в который будет загружены данные
     * @param currentSegmentSize размер сегмента
     */
    @Override
    public void downloadFile(String fileName, String destination, int currentSegmentSize) {
        List<FileHash> data = fileHashService.findByFileName(fileName);
        int segmentSize = (currentSegmentSize <= 0) ? baseSegmentSize : currentSegmentSize;
        try (FileOutputStream fos = new FileOutputStream(destination)) {
            for (FileHash fh : data) {
                Hash hash = fh.getHash();
                String source = hash.getFileName();
                byte[] buffer = Files.readAllBytes(Paths.get(source));
                int from = hash.getLineNumber() * (segmentSize + 1);
                byte[] bytes = Arrays.copyOfRange(buffer, from, from + segmentSize);
                // Удаляем нулевые символы в конце файла
                if (bytes[bytes.length - 1] == 0) {
                    for (byte b : bytes) {
                        if (b != 0) {
                            fos.write(b);
                        }
                    }
                } else {
                    fos.write(bytes);
                }
            }
        } catch (IOException ex) {
            logger.error("Error during file download", ex);
        }
    }

    @Override
    public void uploadFile(MultipartFile file) {
        byte[] bytes = null;
        try {
            bytes = file.getBytes();
        } catch (OutOfMemoryError | IOException | SecurityException ex) {
            logger.error("Error during reading file", ex);
        }

        if (bytes != null) {
            File fileEntity = new File();
            fileEntity.setFileName(file.getOriginalFilename());
            fileEntity.setDate(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
            fileRepository.create(fileEntity);

            // размер сегмента
            int segmentSize = baseSegmentSize;
            // путь для сохранения сегментов
            String storagePath = Paths.get(".").toAbsolutePath().getParent() + "\\" + baseStorageDir + "\\";
            // максимальный размер файлов хранилища сегментов
            int maxStorageFileSize = baseMaxStorageFileSize;

            // порядок сегмента в файле
            int number = 0;

            for (int i = 0; i < bytes.length; i += segmentSize) {
                byte[] segment = Arrays.copyOfRange(bytes, i, i + segmentSize);
                byte[] hash = hashService.createHash(segment);
                Hash hashEntity = hashService.findByHash(hash);
                if (hashEntity != null) {
                    hashEntity = hashService.createOrUpdate(hashEntity);
                } else {
                    hashEntity = new Hash();
                    hashEntity.setHash(hash);
                    saveSegment(hashEntity, segment, maxStorageFileSize, storagePath);
                    hashEntity = hashService.createOrUpdate(hashEntity);
                }
                fileHashService.create(fileEntity, hashEntity, number);
                number++;
            }
        }
    }

    @Override
    public void uploadFiles(List<MultipartFile> files) {
        for (MultipartFile file : files) {
            uploadFile(file);
        }
    }

    @Override
    public Resource downloadFile(long fileId) {
        List<FileHash> data = fileHashService.findByFileId(fileId);
        int segmentSize = baseSegmentSize;
        byte[] fileBytes = new byte[baseSegmentSize * data.size()];
        try {
            for (int i = 0; i < data.size(); i++) {
                Hash hash = data.get(i).getHash();
                String source = hash.getFileName();
                byte[] buffer = Files.readAllBytes(Paths.get(source));
                int from = hash.getLineNumber() * (segmentSize + 1);
                byte[] bytes = Arrays.copyOfRange(buffer, from, from + segmentSize);
                // Удаляем нулевые символы в конце файла
                if (bytes[bytes.length - 1] == 0) {
                    for (int j = 0; j < bytes.length; j++) {
                        if (bytes[j] != 0) {
                            fileBytes[baseSegmentSize * i + j] = bytes[j];
                        }
                    }
                } else {
                    System.arraycopy(bytes, 0, fileBytes, baseSegmentSize * i, bytes.length);
                }
            }
        } catch (IOException ex) {
            logger.error("Error during file download", ex);
        }
        return new ByteArrayResource(fileBytes);
    }

    @Override
    public String getFileName(long id) {
        Optional<File> fileOpt = fileRepository.findById(id);
        if (fileOpt.isPresent()) {
            return fileOpt.get().getFileName();
        } else return "";
    }

    @Override
    public void deleteFile(long id) {
        logger.info("Delete request");
        File file = fileRepository.findById(id).orElse(null);
        if (file != null) {
            List<FileHash> fileHashList = fileHashService.findByFileId(id);
            fileHashService.delete(file);
            for (FileHash fh : fileHashList) {
                hashService.deleteHashEntry(fh.getHash());
            }
            fileRepository.delete(id);
        }
        logger.info(file + " deleted");
    }

    @Override
    public void downloadFile(String fileName, String destination) {
        downloadFile(fileName, destination, baseSegmentSize);
    }

    /**
     * Сохранить сегмент в файловое хранилище
     *
     * @param hash    хэш-значение сегмента
     * @param segment сегмент в байтах
     */
    private void saveSegment(Hash hash, byte[] segment, int maxStorageFileSize, String storagePath) {
        try {
            if (lastUsedFile == -1) {
                lastUsedFile++;
            } else if (lastUsedLine != -1 && (Files.size(Paths.get(storagePath + lastUsedFile + ".txt")) >
                    maxStorageFileSize - segment.length) ||
                    lastUsedLine == Integer.MAX_VALUE - 1) {
                lastUsedFile++;
                lastUsedLine = -1;
            }
            try (FileOutputStream fos = new FileOutputStream(storagePath + lastUsedFile + ".txt", true)) {
                fos.write(segment);
                fos.write(new byte[]{'\n'});
                lastUsedLine++;
                hash.setFileName(storagePath + lastUsedFile + ".txt");
                hash.setLineNumber(lastUsedLine);
            }
        } catch (IOException ex) {
            logger.error("Error during segment save", ex);
        }
    }
}
