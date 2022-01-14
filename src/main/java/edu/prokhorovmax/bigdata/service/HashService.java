package edu.prokhorovmax.bigdata.service;

import edu.prokhorovmax.bigdata.model.Hash;

public interface HashService {
    void clear();

    Hash findByHash(byte[] hash);

    byte[] createHash(byte[] data);

    Hash createOrUpdate(Hash hash);

    void deleteHashEntry(Hash hash);
}
