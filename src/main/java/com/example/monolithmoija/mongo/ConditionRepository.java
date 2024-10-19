package com.example.monolithmoija.mongo;

import com.example.monolithmoija.mongo_entity.Condition;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConditionRepository extends MongoRepository<Condition,Long> {
    @Override
    <S extends Condition> List<S> saveAll(Iterable<S> entities);
    List<Condition> findByRecruitId(Long recruitId);
}
