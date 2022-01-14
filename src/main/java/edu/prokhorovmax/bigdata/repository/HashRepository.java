package edu.prokhorovmax.bigdata.repository;

import edu.prokhorovmax.bigdata.model.Hash;

import java.util.Optional;

public interface HashRepository {
    Optional<Hash> findByHash(byte[] hash);

    void create(Hash hash);

    void update(Hash hash);

    void delete(byte[] hash);

    void clear();
}
