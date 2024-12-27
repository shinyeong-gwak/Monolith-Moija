package com.example.monolithmoija.mongo;

import com.example.monolithmoija.entity.PushNoti;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PushNotiRepository extends MongoRepository<PushNoti,String> {
}
