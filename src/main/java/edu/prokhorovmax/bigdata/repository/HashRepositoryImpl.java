package edu.prokhorovmax.bigdata.repository;

import edu.prokhorovmax.bigdata.model.Hash;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.Optional;

@Repository
public class HashRepositoryImpl implements HashRepository {

    private final EntityManager entityManager;

    public HashRepositoryImpl(@Qualifier("coreEntityManager") EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Optional<Hash> findByHash(byte[] hash) {
        String query = "SELECT h FROM Hash h WHERE h.hash = :hash";
        return entityManager.createQuery(query, Hash.class)
                .setParameter("hash", hash)
                .getResultList().stream().findFirst();
    }

    @Override
    public void create(Hash hash) {
        entityManager.persist(hash);
    }

    @Override
    public void update(Hash hash) {
        entityManager.merge(hash);
    }

    @Override
    public void delete(byte[] hash) {
        String query = "DELETE FROM Hash h WHERE h.hash = :hash";
        entityManager.createQuery(query).setParameter("hash", hash).executeUpdate();
    }

    @Override
    public void clear() {
        String query = "DELETE FROM Hash";
        entityManager.createQuery(query).executeUpdate();
    }
}
