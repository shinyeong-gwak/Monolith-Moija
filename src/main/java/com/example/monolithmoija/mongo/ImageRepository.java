package com.example.monolithmoija.mongo;

import com.example.monolithmoija.mongo_entity.Image;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageRepository extends MongoRepository<Image,Long> {
    List<Image> findAllByUrlContainsIgnoreCase(String recruitId);

    void deleteByRecruitIdAndNumber(Long postId, int index);

    boolean existsByRecruitIdAndNumber(Long recruitId, int number);

    @Query("{'recruitId' : ?0}")
    @Update("{'$set': {'recruitId': ?1}}")
    void updateRecruitId(Long prev, Long next);
}
