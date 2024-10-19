package com.example.monolithmoija.service;

import com.example.monolithmoija.dto.UserRes;
import com.example.monolithmoija.entity.User;
import com.example.monolithmoija.extractor.Genarator;
import com.example.monolithmoija.global.BaseException;
import com.example.monolithmoija.mongo_entity.Nickname;
import com.example.monolithmoija.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static com.example.monolithmoija.global.BaseResponseStatus.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    GCSService gcsService;
    @Autowired
    NicknameRepository nicknameRepository;

    public UserRes.ProfileRes loadProfile(String userId) throws BaseException, IOException {
        Optional<User> userOptional = userRepository.findById(userId);
        if(userOptional.isPresent()) {
            return UserRes.ProfileRes.builder()
                    .nickname(userOptional.get().getNickname())
                    .bornIn(Genarator.changeToBornIn(userOptional.get().getBirth()))
                    .profilePhotoUrl(userOptional.get().getProfile())
                    .gender(userOptional.get().isGender()?"여":"남")
                    .userId(userId)
                    .reliabilityUser(userOptional.get().getReliabilityUser())
                    .build();
        }else {
            throw new BaseException(BAD_ACCESS);
        }
    }

    @Transactional
    public void saveProfile(String fileName, MultipartFile image, String userId) throws BaseException, IOException, NoSuchAlgorithmException, InvalidKeyException {
        Optional<User> userOptional = userRepository.findById(userId);
        if(userOptional.isPresent()) {
            //원래의 파일 이름은 무시함.
            fileName = String.format("%s_profile.jpg",userOptional.get().getUsername()); //sy-gwak

            String gcsUrl = gcsService.writeProfile(fileName,image);
            userRepository.updateProfile(gcsUrl,userId);
        } else {
            throw new BaseException(LOGIN_FIRST);
        }
    }

    public void editNickname(String newNickname, String userId) throws BaseException {
        Optional<User> userOptional = userRepository.findById(userId);
        //중복 닉이 있다면
        checkNicknameDup(newNickname);
        if(userOptional.isPresent()) {
            if(userOptional.get().getNickname().equals(newNickname)) {
                throw new BaseException(NOT_EDIT);
            }else {
                userRepository.updateNickname(newNickname,userId);
                //에딧은 밑에꺼가 필요하고, 이닛은 필요없다.
                //닉네임 변경시간이 너무 잦다면
                if(nicknameRepository.findById(userId).isPresent()) {
                    Nickname nickname = nicknameRepository.findById(userId).get();
                    //90일 이상 되었다면
                    // 현재 날짜와 시간
                    LocalDateTime currentDate = LocalDateTime.now();
                    // 90일 이전의 날짜와 시간 계산
                    LocalDateTime ninetyDaysAgo = currentDate.minus(90, ChronoUnit.DAYS);

                    // 현재로부터 90일 이전인지 검증
                    if (ninetyDaysAgo.isBefore(nickname.getLastModifiedDate())) {
                        nicknameRepository.save(Nickname.builder()
                                .userId(userId)
                                .lastModifiedDate( currentDate )
                                .build());
                    }
                    throw new BaseException(NICKNAME_CHANGE_AVAILABLE);
                }
            }
        } else {
            throw new BaseException(LOGIN_FIRST);
        }
    }

    private void checkNicknameDup(String newNickname) throws BaseException{
        if(userRepository.existsByNickname(newNickname) ) {
            throw new BaseException(DUPLICATE_NICKNAME);
        }
    }

}
