package edu.prokhorovmax.bigdata.repository;

import edu.prokhorovmax.bigdata.model.File;

import java.util.List;
import java.util.Optional;

public interface FileRepository {
    Optional<File> findById(long id);

    Optional<File> findByName(String fileName);

    void create(File file);

    void clear();

    List<File> getFileList();

    void delete(long id);
}
