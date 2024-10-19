package com.example.monolithmoija.mongo;


import com.example.monolithmoija.mongo_entity.Nickname;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NicknameRepository extends MongoRepository<Nickname,String> {
}
