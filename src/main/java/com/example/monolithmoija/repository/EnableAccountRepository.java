package com.example.monolithmoija.repository;

import com.example.monolithmoija.entity.EnableAccount;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EnableAccountRepository extends CrudRepository<EnableAccount,String> {
    Optional<EnableAccount> findByUuid(String uuid);
}
