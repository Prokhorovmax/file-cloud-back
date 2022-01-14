package edu.prokhorovmax.bigdata.service;

import edu.prokhorovmax.bigdata.model.Hash;
import edu.prokhorovmax.bigdata.repository.HashRepository;
import edu.prokhorovmax.bigdata.repository.JpaHashRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

@Service
@Transactional(rollbackFor = Exception.class)
public class HashServiceImpl implements HashService {

    private static final Logger logger = LoggerFactory.getLogger(HashServiceImpl.class.getName());

    private final HashRepository hashRepository;
    private final JpaHashRepository jpaHashRepository;


    public HashServiceImpl(@Lazy HashRepository hashRepository,
                           @Lazy JpaHashRepository jpaHashRepository) {
        this.hashRepository = hashRepository;
        this.jpaHashRepository = jpaHashRepository;
    }

    @Override
    public void clear() {
        hashRepository.clear();
    }

    @Override
    public Hash findByHash(byte[] hash) {
        return jpaHashRepository.findByHash(hash).orElse(null);
    }

    @Override
    public byte[] createHash(byte[] data) {
        try {
            return MessageDigest.getInstance("SHA-512").digest(data);
        } catch (NoSuchAlgorithmException ex) {
            logger.error("Error during create hash", ex);
            return null;
        }
    }

    @Override
    public Hash createOrUpdate(Hash hash) {
        Optional<Hash> entityOpt = hashRepository.findByHash(hash.getHash());
        if (entityOpt.isPresent()) {
            Hash entity = entityOpt.get();
            entity.increaseCount();
            hashRepository.update(entity);
            return entity;
        } else {
            hash.setCount(1);
            hashRepository.create(hash);
            return hash;
        }
    }

    @Override
    public void deleteHashEntry(Hash hash) {
        if (hash.getCount() > 1) {
            hash.decreaseCount();
            hashRepository.update(hash);
        } else {
            hashRepository.delete(hash.getHash());
        }
    }
}
