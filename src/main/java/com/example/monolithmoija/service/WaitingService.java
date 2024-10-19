package com.example.monolithmoija.service;

import com.example.monolithmoija.dto.MypageRes;
import com.example.monolithmoija.dto.PostReq;
import com.example.monolithmoija.dto.PostRes;
import com.example.monolithmoija.dto.QnADTO;
import com.example.monolithmoija.mongo_entity.Answer;
import com.example.monolithmoija.entities.Waiting;
import com.example.monolithmoija.extractor.Genarator;
import com.example.monolithmoija.global.BaseException;
import com.example.monolithmoija.repository.WaitingRepository;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.example.monolithmoija.global.BaseResponseStatus.BAD_ACCESS;
import static com.example.monolithmoija.global.BaseResponseStatus.NOT_EXISTS;

@Service
@NoArgsConstructor
@Slf4j
public class WaitingService {
    @Autowired
    WaitingRepository waitingRepository;
    @Autowired
    AnswerService answerService;
    @Autowired
    ConditionService conditionService;
    @Autowired
    MemberService memberService;
    public void saveWaiting(PostReq.PostWaitingReq postWaitingReq, Long postId, String userId) throws BaseException {
        Waiting waiting = Waiting.builder()
                .isAsk(postWaitingReq.isAsk())
                .numAnswer(postWaitingReq.getNumAnswer())
                .recruitId(postId)
                .userId(userId)
                //기본값
                .isPermitted(false)
                .build();
        waitingRepository.saveAndFlush(waiting);
        answerService.saveAll(postWaitingReq.getAnswers(), waiting.getWaitingId(), userId);
    }

    public boolean existTeamUser(Long teamId,String userId) {
        return waitingRepository.existsByRecruitIdAndUserId(teamId,userId);
    }

    public List<MypageRes.WaitingListRes> loadWaitingList(List<PostRes.ListPostRes> myposts) throws BaseException {
        ArrayList<MypageRes.WaitingListRes> response = new ArrayList<>();
        //리크루트 이름으로부터 웨이팅 정보를 다 가져옴
        for (PostRes.ListPostRes post : myposts) {
            List<Waiting> waitings = waitingRepository.findAllByRecruitId(post.getPost_id());
            List<MypageRes.MemDto> members = waitings.stream().map(w ->
                    MypageRes.MemDto.builder()
                            .waitingId(w.getWaitingId())
                            .is_ask(w.isAsk())
                            .nickname(w.getUser().getNickname())
                            .build()).toList();

            //post를 받아온 객체를 이 상황에 맞게 매핑
            response.add(MypageRes.WaitingListRes.builder()
                            .title(post.getTitle())
                            .postId(post.getPost_id())
                            .latestWrite(post.getLatest_write())
                            .users(members)
                    .build());
        }

        return response;
    }
    public List<MypageRes.AskListRes> loadMyRequest(String userId) {
        List<Waiting> myWaitings =  waitingRepository.findAllByUserId(userId);
        return myWaitings.stream().map(MypageRes.AskListRes::from).toList();
    }

    public MypageRes.WaitingRes viewWaiting(Long waitingId, String leaderId) throws BaseException {
        Optional<Waiting> waiting = waitingRepository.findByWaitingId(waitingId);

        if(waiting.isPresent()) {
            MypageRes.WaitingRes waitingRes = MypageRes.WaitingRes.builder()
                    .is_ask(waiting.get().isAsk())
                    .userId(waiting.get().getUserId())
                    .profileUrl(waiting.get().getUser().getProfile())
                    .reliabilityUser(waiting.get().getUser().getReliabilityUser())
                    .nickname(waiting.get().getUser().getNickname())
                    .gender(waiting.get().getUser().isGender() ? "여":"남")
                    .genaration(Genarator.changeToBornIn(waiting.get().getUser().getBirth()))
                    .build();
            List<QnADTO> qnaList = conditionService.viewCondition(waiting.get().getRecruitId());
            List<Answer> answers = answerService.findAllByWaitingId(waitingId);
            //혹시나 답변개수랑 질문개수랑 다르면 잘못된거.
            if(qnaList.size() != answers.size()) {throw new BaseException(BAD_ACCESS);}
            //일단 qnaList에 덮어쓰기하는 방식인데 만약 단답형말고 질문 고르기로 바꾼다면 객체를 두개 줄듯
            for(int i = 0 ; i < qnaList.size(); i++) {
                qnaList.get(i).setAnswer(answers.get(i).getAnswer());
            }
            waitingRes.setQnas(qnaList);
            return waitingRes;
        }else {
            throw new BaseException(NOT_EXISTS);
        }

    }

    public void acceptOrDeny(Long waitingId, boolean isAccept, String leaderId) throws BaseException {
        if(!waitingRepository.existsById(waitingId))
            throw new BaseException(BAD_ACCESS);
        Waiting waitingInfo = waitingRepository.findByWaitingId(waitingId).get();
        if(isAccept){
            memberService.save(waitingInfo.getRecruitId(),waitingInfo.getUserId());
        }
        else {
        }
        //일정기간 보관하면서 보이는게...지금까지 보관한거 많으니까 그냥 없앨거야. 만약 안 없앤다면 isPermitted조작으로 수정
        if(waitingInfo.getNumAnswer()!=0) {
            answerService.deleteByWaitingId(waitingId);
        }
        waitingRepository.deleteById(waitingId);

    }


}
