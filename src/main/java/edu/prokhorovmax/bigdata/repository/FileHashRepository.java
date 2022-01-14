package edu.prokhorovmax.bigdata.repository;

import edu.prokhorovmax.bigdata.model.File;
import edu.prokhorovmax.bigdata.model.FileHash;

import java.util.List;

public interface FileHashRepository {
    List<FileHash> findByFile(File file);

    void create(FileHash entity);

    void clear();

    void delete(File file);
}
