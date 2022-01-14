package edu.prokhorovmax.bigdata.repository;

import edu.prokhorovmax.bigdata.model.Hash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaHashRepository extends JpaRepository<Hash, byte[]> {

    Optional<Hash> findByHash(byte[] hash);
}
