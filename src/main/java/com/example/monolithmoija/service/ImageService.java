package com.example.monolithmoija.service;

import com.example.monolithmoija.mongo.ImageRepository;
import com.example.monolithmoija.mongo_entity.Image;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ImageService {
    @Autowired
    ImageRepository imageRepository;
    public void saveImage(String url, Long recruitId, int num) {
        Image image = Image.builder()
                .number(num)
                .url(url)
                .recruitId(recruitId)
                .build();
        imageRepository.save(image);
    }

    public List<Image> loadImageUrl(Long recruitId) {
        return imageRepository.findAllByUrlContainsIgnoreCase(String.format("[%s](",Long.toString(recruitId)));
    }

    public void deleteByRecruitIdAndNumber(Long postId, int index) {
        imageRepository.deleteByRecruitIdAndNumber(postId,index);
    }

    public void updateRecruitId(Long prev, Long next) {
        imageRepository.updateRecruitId(prev,next);
    }

    public boolean existsByRecruitIdAndNumber(Long recruitId, int number) {
        return imageRepository.existsByRecruitIdAndNumber(recruitId,number);
    }
}
