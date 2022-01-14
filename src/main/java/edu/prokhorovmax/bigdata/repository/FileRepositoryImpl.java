package edu.prokhorovmax.bigdata.repository;

import edu.prokhorovmax.bigdata.model.File;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class FileRepositoryImpl implements FileRepository {

    private final EntityManager entityManager;

    public FileRepositoryImpl(@Qualifier("coreEntityManager") EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Optional<File> findById(long id) {
        String query = "SELECT f FROM File f WHERE f.id = :id";
        return entityManager.createQuery(query, File.class)
                .setParameter("id", id)
                .getResultList().stream().findFirst();
    }

    @Override
    public Optional<File> findByName(String fileName) {
        String query = "SELECT f FROM File f WHERE f.fileName = :name";
        return entityManager.createQuery(query, File.class)
                .setParameter("name", fileName)
                .getResultList().stream().findFirst();
    }

    @Override
    public void create(File file) {
        entityManager.persist(file);
    }

    @Override
    public void clear() {
        String query = "DELETE FROM File";
        entityManager.createQuery(query).executeUpdate();
    }

    @Override
    public List<File> getFileList() {
        String query = "SELECT f FROM File f ORDER BY f.date DESC";
        return new ArrayList<>(entityManager.createQuery(query, File.class)
                .getResultList());
    }

    @Override
    public void delete(long id) {
        String query = "DELETE FROM File f WHERE f.id = :id";
        entityManager.createQuery(query).setParameter("id", id).executeUpdate();
    }
}
