package com.example.monolithmoija.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

@Service@RequiredArgsConstructor@Slf4j
public class MongoService {
    @Autowired
    private MongoTemplate mongoTemplate;
    public <T> void storeMessage(T collObject, String collectionName) {
        mongoTemplate.save(collObject,collectionName);
    }

    public <T> T findOne(Query readQuery, Class<T> collectionClass, String collectionName) {
        return mongoTemplate.findOne(readQuery,collectionClass,collectionName);
    }

    public <T> Integer count(Query talkCountQuery, Class<T> collectionClass) {
        return (int) mongoTemplate.count(talkCountQuery, collectionClass);
    }

    public <T> List<T> find(Query query, Class<T> collectionClass, String collectionName) {
        return mongoTemplate.find(query,collectionClass,collectionName);
    }
}
