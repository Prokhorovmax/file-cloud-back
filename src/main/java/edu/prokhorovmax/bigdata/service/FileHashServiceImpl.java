package edu.prokhorovmax.bigdata.service;

import edu.prokhorovmax.bigdata.model.File;
import edu.prokhorovmax.bigdata.model.FileHash;
import edu.prokhorovmax.bigdata.model.Hash;
import edu.prokhorovmax.bigdata.repository.FileHashRepository;
import edu.prokhorovmax.bigdata.repository.FileRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


@Service
@Transactional(rollbackFor = Exception.class)
public class FileHashServiceImpl implements FileHashService {

    private final FileHashRepository fileHashRepository;
    private final FileRepository fileRepository;

    public FileHashServiceImpl(@Lazy FileHashRepository fileHashRepository,
                               @Lazy FileRepository fileRepository) {
        this.fileHashRepository = fileHashRepository;
        this.fileRepository = fileRepository;
    }

    @Override
    public FileHash create(File file, Hash hash, int number) {
        FileHash entity = new FileHash();
        entity.setFile(file);
        entity.setHash(hash);
        entity.setNumber(number);
        fileHashRepository.create(entity);
        return entity;
    }

    @Override
    public List<FileHash> findByFileName(String fileName) {
        File file = fileRepository.findByName(fileName).orElse(null);
        return (file == null) ? new ArrayList<>() : fileHashRepository.findByFile(file);
    }

    @Override
    public List<FileHash> findByFileId(long fileId) {
        File file = fileRepository.findById(fileId).orElse(null);
        return (file == null) ? new ArrayList<>() : fileHashRepository.findByFile(file);
    }

    @Override
    public void clear() {
        fileHashRepository.clear();
    }

    @Override
    public void delete(File file) {
        fileHashRepository.delete(file);
    }
}
