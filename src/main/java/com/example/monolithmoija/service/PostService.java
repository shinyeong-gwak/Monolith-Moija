package com.example.monolithmoija.service;

import com.example.monolithmoija.dto.PostReq;
import com.example.monolithmoija.dto.PostRes;
import com.example.monolithmoija.dto.ROLE_IN_POST;
import com.example.monolithmoija.entities.Recruit;
import com.example.monolithmoija.extractor.Genarator;
import com.example.monolithmoija.global.BaseException;
import com.example.monolithmoija.mongo_entity.Image;
import com.example.monolithmoija.repository.RecruitRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.*;

import static com.example.monolithmoija.dto.ROLE_IN_POST.*;
import static com.example.monolithmoija.extractor.Sorter.state;
import static com.example.monolithmoija.extractor.Sorter.viewType;
import static com.example.monolithmoija.global.BaseResponseStatus.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostService {
    private static final int PAGE_SIZE = 5;
    @Autowired
    private final RecruitRepository recruitRepository;
    private final ConditionService conditionService;
    private final LikeService likeService;
    private final ClipService clipService;
    private final ImageService imageService;
    private final WaitingService waitingService;
    private final SporeService sporeService;
    private final GCSService gcsService;
    private final MemberService memberService;

    public void writePost(PostReq.PostWriteReq postWriteReq, List<MultipartFile> images, Long postId,String userId) throws BaseException, IOException, NoSuchAlgorithmException, InvalidKeyException {
        Recruit recruit;
        //수정일때는 어떤 것을 할지
        if(!postWriteReq.isChanged()) {
            //해당 id의 포스트에 접근 권한이 없을경우?? - 토큰 구현 이후
            //초기에만 가능한 것들.
            recruit = Recruit.builder()
                    .leaderId(userId)
                    .category(postWriteReq.getCategory())
                    .timeFirstWrite(new Timestamp(System.currentTimeMillis()))
                    .likes(0L)
                    .views(0L)
                    .reliabilityRecruit(0)
                    //여기부터 초기 기본값 정하기
                    .latestWrite(new Timestamp(System.currentTimeMillis()))
                    .latestWrite(new Timestamp(System.currentTimeMillis()))
                    .stateRecruit(true)
                    .isAvailable(true)
                    .build();
            recruit = setDupColumn(recruit,postWriteReq);
        } else {
            //수정이 아닌데 체크되어있음?
            if(postId == 0L) {
                throw new BaseException(BAD_ACCESS);
            }
            //존재하는지 검증
            if(recruitRepository.findByRecruitIdAndIsAvailableTrue(postId).isPresent()){
                recruit = recruitRepository.findByRecruitIdAndIsAvailableTrue(postId).get();
            } else {
                throw new BaseException(BAD_ACCESS);
            }
            recruit = setDupColumn(recruit,postWriteReq);
            recruit.setRecruitId(null);
            //이미지 수정 발생 경우!!
            if(images.size() !=0 | postWriteReq.getChangedPictures()!=null) {
                for(int index:postWriteReq.getChangedPictures())
                    imageService.deleteByRecruitIdAndNumber(postId,index);
                //-----------------------여기에 gcs버킷에 있는 이미지 삭제 코드도 추가해야함-----------------------------------
            }
            recruit.setTimeLastWrite(new Timestamp(System.currentTimeMillis()));
            //이게 '최신순'의 기준이 되고, 따로 이용자에게 보여지는 시간은 아님
            recruit.setLatestWrite(new Timestamp(System.currentTimeMillis()));
            recruitRepository.notAvailable(postId);
        }
        //최종 저장 -> sql 모집에 하나, nosql 조건에 하나 저장
        recruitRepository.saveAndFlush(recruit);

        //이미지 저장하기!!
        if (!images.isEmpty()) {
            imageService.updateRecruitId(postId,recruit.getRecruitId());
            for (int i = 0; i < images.size(); i++) {
                if(imageService.existsByRecruitIdAndNumber(recruit.getRecruitId(),i)){
                    continue;
                }
                //  [recruitid](1)-uuid.jpg 이런 형식!
                String type;
                if(images.get(i).getContentType().equals("image/png")) {
                    type = "png";
                }else if(images.get(i).getContentType().equals("image/jpeg") | images.get(i).getContentType().equals("image/pjpeg")) {
                    type = "jpg";
                } else {
                    throw new BaseException(FILE_FORMAT_ERROR);
                }
                String fileName = String.format("[%s](%s)-%s.%s", recruit.getRecruitId(), i, UUID.randomUUID().toString(),type);
                String url = gcsService.writeRecruitImage(fileName, images.get(i));
                imageService.saveImage(url, recruit.getRecruitId(), i);
            }
        }

        //모임이 생겼으니 팀원이 들어갈 member id도 연결해 줘야함.
        memberService.save(recruit.getRecruitId(),recruit.getLeaderId());

        //recruit의 id를 가져와서 같이 서비스로 넘김
        conditionService.writeQuestions(
                postWriteReq.getConditions(),
                recruit.getRecruitId()
        );
        if(postWriteReq.getNumCondition() != postWriteReq.getConditions().size()) {
            throw new BaseException(NEED_MORE_WRITE);
        }
    }

    public void remove(String userId, Long postId) throws BaseException {
        Optional<Recruit> recruit = recruitRepository.findByRecruitIdAndIsAvailableTrue(postId);
        if(recruit.isEmpty()) {
            throw new BaseException(NOT_EXISTS);
        }
        if(!recruit.get().getLeaderId().equals(userId)) {
            throw new BaseException(NOT_PRIVILEGE);
        }
        //마지막으로 수정한 날짜를 변경 (이 날로 부터 3개월 뒤에 자동 삭제)
        recruit.get().setTimeLastWrite(new Timestamp(System.currentTimeMillis()));
        recruitRepository.saveAndFlush(recruit.get());

        recruitRepository.notAvailable(postId);
    }
    private Recruit setDupColumn(Recruit recruit,PostReq.PostWriteReq postWriteReq) {
        recruit.setTitle(postWriteReq.getTitle());
        recruit.setContents(postWriteReq.getContents());
        recruit.setPenalty(postWriteReq.getPenalty());
        recruit.setNumCondition(postWriteReq.getNumCondition());
        return recruit;
    }
    public Page<PostRes.ListPostRes> pagePage(Optional<String> category, String view_type, Optional<String> keyword, Optional<String> searchType, Optional<String> userId,int pageNo)
            throws BaseException {
        switch (view_type) {
            case "most_view" -> view_type = "views";
            case "most_like" -> view_type = "likes";
            default -> view_type = "latestWrite";
        }
        Sort sort = Sort.by(Sort.Direction.DESC, view_type);
        Pageable pageable = PageRequest.of(pageNo,PAGE_SIZE, sort);
        List<PostRes.ListPostRes> recruitList = list(category,view_type,keyword,searchType,userId,pageable);
        return new PageImpl<>(recruitList,pageable,recruitList.size());
    }
    //sy-gwak today
    public List<PostRes.ListPostRes> pageList(Optional<String> category, String view_type, Optional<String> keyword, Optional<String> searchType, Optional<String> userId,int pageNo)
            throws BaseException {
        switch (view_type) {
            case "most_view" -> view_type = "views";
            case "most_like" -> view_type = "likes";
            default -> view_type = "latestWrite";
        }
        Sort sort = Sort.by(Sort.Direction.DESC, view_type);
        Pageable pageable = PageRequest.of(pageNo,PAGE_SIZE, sort);
        List<PostRes.ListPostRes> recruitList = list(category,view_type,keyword,searchType,userId,pageable);
        return recruitList;
    }

    public List<PostRes.ListPostRes> list(Optional<String> category, String view_type, Optional<String> keyword, Optional<String> searchType, Optional<String> userId,Pageable pageable) throws BaseException {
        List<Recruit> recruitList = new ArrayList<>();
        Page<Recruit> recruitPage;
        userId.ifPresent(s -> {
            recruitList.addAll(recruitRepository.findAllByLeaderIdAndIsAvailableTrueAndStateRecruitTrue(s,pageable).getContent());
            recruitList.addAll(recruitRepository.findAllByLeaderIdAndIsAvailableTrueAndStateRecruitFalse(s,pageable).getContent());
        });
        category.ifPresent(s -> {
            recruitList.addAll(recruitRepository.findAllByCategoryContainingAndIsAvailableTrueAndStateRecruitTrue(s.equals("all")?"":s));//,pageable).getContent());
            recruitList.addAll(recruitRepository.findAllByCategoryContainingAndIsAvailableTrueAndStateRecruitFalse(s.equals("all")?"":s));//,pageable).getContent());

        });
        //아직 키워드 정렬 안됨.!!!!!!!!!!!1
        keyword.ifPresent(s -> {
            switch(searchType.orElse("all")) {
                case "tilte":
                    recruitList.addAll(recruitRepository.findAllByTitleContainingAndIsAvailableTrue(keyword.get(),pageable).getContent());
                case "contents":
                    recruitList.addAll(recruitRepository.findAllByContentsContainingAndIsAvailableTrue(keyword.get(),pageable).getContent());
                case "leader":
                    recruitList.addAll(recruitRepository.findAllByLeaderIdContainingAndIsAvailableTrue(keyword.get(),pageable).getContent());
                default:
                    recruitList.addAll(recruitRepository.findAllByTitleContainingAndIsAvailableTrue(keyword.get(),pageable).getContent());
                    recruitList.addAll(recruitRepository.findAllByContentsContainingAndIsAvailableTrue(keyword.get(),pageable).getContent());
                    recruitList.addAll(recruitRepository.findAllByLeaderIdContainingAndIsAvailableTrue(keyword.get(),pageable).getContent());
            }
        });

//        if(recruitList.size() > 10) {
//            return makeList( new PageImpl<>(recruitList, pageable, recruitList.size()).getContent() );
//
//        }


        if(recruitList.isEmpty()) {
            throw new BaseException(NOT_EXISTS);
        }

        return makeList(recruitList);
    }

    public PostRes.ReadPostRes view(Long postId,Optional<String> userId) throws BaseException, IOException {
        Optional<Recruit> selected = recruitRepository.findByRecruitIdAndIsAvailableTrue(postId);
        if(selected.isPresent()) {
            Recruit recruit = selected.get();
            updateView(recruit.getRecruitId());
            return PostRes.ReadPostRes.builder()
                    .title(recruit.getTitle())
                    .contents(recruit.getContents())
                    .stateRecruit(recruit.isStateRecruit())
                    .leaderNickname(recruit.getLeader().getNickname())
                    .isChanged(recruit.isChanged())
                    .penalty(recruit.getPenalty())
                    .reliabilityRecruit(recruit.getReliabilityRecruit())
                    .latestWrite(recruit.getLatestWrite())
                    .likes(recruit.getLikes())
                    .views(recruit.getViews())
                    .category(recruit.getCategory())
                    .lastWrite(recruit.getTimeLastWrite())
                    .firstWrite(recruit.getTimeFirstWrite())
                    .numCondition(recruit.getNumCondition())
                    .pictures(imageService.loadImageUrl(recruit.getRecruitId()).stream().
                            map(Image::getUrl).toList())
                    //커스텀 유저에 따른 권한
                    .myliked(likeService.existsByRecruitIdAndUserId(recruit.getRecruitId(), userId.orElse("없는없는없는유저유저유저유저")))
                    .mycliped(clipService.existsByRecruitIdAndUserId(recruit.getRecruitId(), userId.orElse("없는없는없는유저유저유저유저")))
                    .mygranted(sporeService.findByPostIdAndUserId(recruit.getRecruitId(), userId.orElse("없는없는없는유저유저유저유저")))
                    .roleInPost(userId.isEmpty()? Viewer:getRoleInPost(recruit,userId.get()))
                    //여기부터 유저
                    .leaderNickname(recruit.getLeaderId())
                    .gender(recruit.getLeader().isGender()?"여":"남")
                    .reliabilityUser(recruit.getLeader().getReliabilityUser())
                    .bornIn(Genarator.changeToBornIn(recruit.getLeader().getBirth()))
                    .profilePhoto(recruit.getLeader().getProfile())
                    .build();
        } else {
            //보려는 포스트가 없을 경우
            throw new BaseException(NOT_EXISTS);
        }
    }

    private ROLE_IN_POST getRoleInPost(Recruit recruit, String userId) {
        if(userId.equals(recruit.getLeaderId())) {
            return Leader;
        } else if (memberService.existTeamUser(recruit.getRecruitId(),userId)) {
            return Member;
        } else if (waitingService.existTeamUser(recruit.getRecruitId(), userId)) {
            return TempMember;
        } else {
            return Viewer;
        }
    }

    @Transactional
    public int updateView(Long id) {
        return recruitRepository.updateView(id);
    }

    //답변을 등록하고, 대기를 걸어놓고 모임 초대를 기다리는 것 , waiting에 접근하고, waiting은 answer에 접근해서 등록

    public void inWaitingQueue(PostReq.PostWaitingReq postWaitingReq, Long postId, String userId) throws BaseException {
        //포스트 아이디가 존재하지 않는다면
        if(!recruitRepository.existsByRecruitIdAndIsAvailableTrue(postId)) {
            throw new BaseException(NOT_EXISTS);
        }
        //이미 있는거면 안되게 해야지
        if(memberService.existTeamUser(postId,userId))
            throw new BaseException(TEAM_ALREADY_JOINED);
        if(waitingService.existTeamUser(postId,userId))
            throw new BaseException(WAITING_ALREADY_EXISTS);
        //question개수와 answer개수가 다르다면 답변을 덜 쓴거니 다 채우라고 해야지 -> 이거 프론트에서 처리
        waitingService.saveWaiting(postWaitingReq,postId,userId);
    }

    public void renew(Long postId, String userId) throws BaseException{
        Optional<Recruit> recruitOptional =  recruitRepository.findByRecruitIdAndIsAvailableTrue(postId);
        //시간 차이 처리
        long current = System.currentTimeMillis();
        long latest;
        if(recruitOptional.isPresent()) {
            if(!Objects.equals(recruitOptional.get().getLeaderId(), userId)) {
                throw new BaseException(NOT_PRIVILEGE);
            }
            latest = recruitOptional.get().getLatestWrite().getTime();
        } else {
            throw new BaseException(NOT_EXISTS);
        }
        short gap = (short) ((current - latest) / (60 * 60 * 1000));
        //조건에 만족 한다면 서비스를 부름
        if(gap >= 30) {
            recruitRepository.updateTimeLatest(new Timestamp(current),postId);
        } else {
            throw new BaseException(CURRENT_UNAVAILABLE);
        }


    }

    public boolean existPost(Long recruitId) throws BaseException{
        return recruitRepository.existsByRecruitIdAndIsAvailableTrue(recruitId);
    }

    public static List<PostRes.ListPostRes> makeList(List<Recruit> recruits) {
        return recruits.stream().map(r ->
                PostRes.ListPostRes.builder()
                        .post_id(r.getRecruitId())
                        .title(r.getTitle())
                        .state_recruit(r.isStateRecruit())
                        .contents(r.getContents())
                        .leader_nickname(r.getLeader().getNickname())
                        .latest_write(r.getLatestWrite())
                        .likes(r.getLikes())
                        .views(r.getViews())
                        .build()
        ).toList();
    }

    //true이면 재개 false이면 종료
    public void stateRecruit(Long postId, boolean stateRecruit, String userId) throws BaseException {
        if(!recruitRepository.existsByRecruitIdAndIsAvailableTrue(postId))
            throw new BaseException(NOT_EXISTS);
        if(!recruitRepository.findLeaderIdByRecruitId(postId).get().equals(userId))
            throw new BaseException(NOT_PRIVILEGE);
        if(stateRecruit == recruitRepository.isRecruiting(postId)) {
            throw new BaseException(ALREADY_RECRUIT);
        }
        recruitRepository.updateStateRecruit(postId,stateRecruit);
    }

    public void grantPost(Long postId, String userId, float score) throws BaseException {
        Optional<Recruit> postOp = recruitRepository.findByRecruitIdAndIsAvailableTrue(postId);
        if(postOp.isPresent() && !sporeService.existsByPostIdAndUserId(postId,userId)){
            //병합 과정
            int prevNumSpore = sporeService.countByRecruitId(postId);
            float prevSpore = postOp.get().getReliabilityRecruit();
            score = ( prevSpore * prevNumSpore + score ) / ( prevNumSpore+1 );
            sporeService.saveGrant(postId,userId,score);
            recruitRepository.updateReliabilityRecruit(postId,score);
        } else {
            throw new BaseException(ALREADY_SCORED);
        }
    }

    public Map titlePost(Long postId) throws BaseException {
        Optional<String> recruitOp = recruitRepository.findTitleByRecruitIdAndIsAvailableTrue(postId);
        if(recruitOp.isEmpty()) {
            throw new BaseException(NOT_EXISTS);
        }
        return Map.of("title",recruitOp.get());
    }
}
