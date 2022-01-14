package edu.prokhorovmax.bigdata.service;


import edu.prokhorovmax.bigdata.model.File;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileService {

    void clear();

    List<File> getFileList();

    void testReset();

    void uploadFile(String fileName);

    void uploadFile(String fileName, int currentSegmentSize, int maxStorageFileSize, String currentStoragePath);

    void downloadFile(String fileName, String destination);

    void downloadFile(String fileName, String destination, int currentSegmentSize);

    void uploadFile(MultipartFile file);

    void uploadFiles(List<MultipartFile> files);

    Resource downloadFile(long id);

    String getFileName(long id);

    void deleteFile(long id);
}
