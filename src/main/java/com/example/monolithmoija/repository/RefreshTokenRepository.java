package com.example.monolithmoija.repository;

import com.example.monolithmoija.entity.RefreshToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends CrudRepository<RefreshToken,String> {
    Optional<RefreshToken> findByToken(String token);

    Optional<RefreshToken> findByAuthId(String authId);

    void updateByAuthId(String userId);
    void deleteByToken(String token);
    boolean existsByToken(String token);

    boolean existsByAuthId(String uuid);
}
