package edu.prokhorovmax.bigdata.service;

import edu.prokhorovmax.bigdata.model.File;
import edu.prokhorovmax.bigdata.model.FileHash;
import edu.prokhorovmax.bigdata.model.Hash;

import java.util.List;

public interface FileHashService {
    FileHash create(File file, Hash hash, int number);

    List<FileHash> findByFileName(String fileName);

    List<FileHash> findByFileId(long fileId);

    void clear();

    void delete(File file);
}
