package edu.prokhorovmax.bigdata.repository;

import edu.prokhorovmax.bigdata.model.File;
import edu.prokhorovmax.bigdata.model.FileHash;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

@Repository
public class FileHashRepositoryImpl implements FileHashRepository {

    private final EntityManager entityManager;

    public FileHashRepositoryImpl(@Qualifier("coreEntityManager") EntityManager entityManager) {
        this.entityManager = entityManager;
    }
    @Override
    public List<FileHash> findByFile(File file) {
        String query = "SELECT fh FROM FileHash fh WHERE fh.file = :file ORDER by fh.number";
        return new ArrayList<>(entityManager.createQuery(query, FileHash.class)
                .setParameter("file", file)
                .getResultList());
    }

    @Override
    public void create(FileHash entity) {
        entityManager.persist(entity);
    }

    @Override
    public void clear() {
        String query = "DELETE FROM FileHash";
        entityManager.createQuery(query).executeUpdate();
    }

    @Override
    public void delete(File file) {
        String query = "DELETE FROM FileHash fh WHERE fh.file = :file";
        entityManager.createQuery(query).setParameter("file", file).executeUpdate();
    }
}
