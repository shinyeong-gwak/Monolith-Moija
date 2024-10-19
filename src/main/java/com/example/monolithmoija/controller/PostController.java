package com.example.monolithmoija.controller;

import com.example.monolithmoija.dto.PostReq;
import com.example.monolithmoija.dto.PostRes;
import com.example.monolithmoija.dto.QnADTO;
import com.example.monolithmoija.global.BaseException;
import com.example.monolithmoija.global.BaseResponse;
import com.example.monolithmoija.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.example.monolithmoija.global.BaseResponseStatus.*;

//scheduled remover 필요!! 현재 지워진 글은 비트만 바꿔서 안보이게 하고 있으므로, 1달에 한번씩 데이터베이스 삭제, 3달 이상 지난 사용 불가능 포스트 삭제!!!


@RestController
@RequiredArgsConstructor
@RequestMapping("/post")
public class PostController {
    @Autowired
    private final PostService postService;
    @Autowired
    private final ConditionService conditionService;
    @Autowired
    private final LikeService likeService;
    @Autowired
    private final ClipService clipService;

    @PostMapping(value="/write",consumes = {MediaType.MULTIPART_FORM_DATA_VALUE,MediaType.APPLICATION_JSON_VALUE,MediaType.APPLICATION_JSON_VALUE})
    public BaseResponse<Void> writePost(
            @RequestPart(value = "image",required = false) List<MultipartFile> images,
            @RequestPart(value = "write") PostReq.PostWriteReq postWriteReq,
            @RequestPart(value = "userId") String userId
            //,@AuthenticationPrincipal String userId
    ) throws BaseException, IOException, NoSuchAlgorithmException, InvalidKeyException {
        if(userId == null)
            throw new BaseException(LOGIN_FIRST);
        if(images == null) {
            images = new ArrayList<>();
        }else if(images.size() > 5) {
            throw new BaseException(NUM_FILE_OVER);
        }
        postService.writePost(postWriteReq,images,0L,userId);
        return new BaseResponse<Void>(SUCCESS);
    }
    /**
     * 원래 /write/{postId}의 PathVariable이였으나, Feign Client의 put메서드 지원 문제로 인해 put으로 받음
     * */
    @PutMapping(value="/write/{postId}",consumes = {MediaType.APPLICATION_JSON_VALUE,MediaType.MULTIPART_FORM_DATA_VALUE})
    public BaseResponse<Void> editPost(
            @PathVariable(name="postId") Long postId,
            @RequestPart(value = "image",required = false) List<MultipartFile> images,
            @RequestPart(value = "write") PostReq.PostWriteReq postWriteReq,
            @RequestPart(value = "userId") String userId
    ) throws BaseException, IOException, NoSuchAlgorithmException, InvalidKeyException {
        if(userId == null)
            throw new BaseException(LOGIN_FIRST);
        if(images == null) {
            images = new ArrayList<>();
        }else if(images.size() > 5) {
            throw new BaseException(NUM_FILE_OVER);
        }
        postService.writePost(postWriteReq, images, postId,userId);
        return new BaseResponse<Void>(SUCCESS);
    }

    @DeleteMapping("/delete/{postId}")
    public BaseResponse<Void> deletePost(
            @PathVariable(name="postId") Long postId,
            @RequestPart(value = "userId") String userId
    ) throws BaseException, IOException {
        if(userId == null)
            throw new BaseException(LOGIN_FIRST);
        postService.remove(userId, postId);
        return  new BaseResponse<Void>(SUCCESS);
    }

    @GetMapping("/list")
    public BaseResponse loadPostList(
            @RequestParam(value="category",required = false,defaultValue = "all") String category,
            @RequestParam(value = "view_type",required = false, defaultValue = "latest") String viewType,
            @RequestParam(value = "keyword",required = false) String keyword,
            @RequestParam(value = "search_type",required = false,defaultValue = "title") String searchType,
            @RequestParam(required = false, defaultValue = "0", value = "page") int pageNo
    ) throws BaseException, IOException {
        Page<PostRes.ListPostRes> response = postService.pageList(
                Optional.of(category),
                viewType,
                Optional.ofNullable(keyword),
                Optional.of(searchType),
                Optional.empty(),
                pageNo);
        return new BaseResponse(response.getContent());
    }

    @GetMapping("/page")
    public BaseResponse<PostRes.ReadPostRes> viewPost(
            @RequestParam(value = "post_id") Long postId
    ) throws BaseException, IOException {
        PostRes.ReadPostRes response = postService.view(postId,Optional.empty());
        return new BaseResponse<>(response);
    }
    @PostMapping("/page")
    public BaseResponse<PostRes.ReadPostRes> viewPostAuth(
            @RequestParam(value = "post_id") Long postId,
            @RequestParam(value = "user_id") String userId
    ) throws BaseException, IOException {
        //System.out.println(userId);
        PostRes.ReadPostRes response = postService.view(postId,Optional.of(userId));
        return new BaseResponse<>(response);
    }



    @PostMapping("/like")
    public BaseResponse<Void> likePost(
            @RequestPart(name = "req") PostReq.PostLikeReq postLikeReq,
            @RequestPart(name = "userId") String userId
    ) throws BaseException, IOException {
        if(userId == null)
            throw new BaseException(LOGIN_FIRST);
        if(!postService.existPost(postLikeReq.getRecruitId())) {
            throw new BaseException(NOT_EXISTS);
        }
        likeService.userPostLike(postLikeReq, userId);
        return new BaseResponse<>(SUCCESS);
    }

    @PostMapping("/clip")
    public BaseResponse<Void> clipPost(
            @RequestPart(name = "req") PostReq.PostClipReq postClipReq,
            @RequestPart(name = "userId") String userId
    ) throws BaseException, IOException {
        if(userId == null)
            throw new BaseException(LOGIN_FIRST);
        clipService.userPostClip(postClipReq,userId);
        return new BaseResponse<>(SUCCESS);
    }

    @PostMapping("/question/{postId}")
    public BaseResponse<List> viewQuestion(
            @PathVariable(name="postId") Long postId,
            @RequestBody String userId
    ) throws BaseException, IOException {
        if(userId == null)
            throw new BaseException(LOGIN_FIRST);
        if(!postService.existPost(postId))
            throw new BaseException(NOT_EXISTS);
        List<QnADTO> conditions = conditionService.viewCondition(postId);
        return new BaseResponse<List>(conditions);

    }

    //waiting 스키마 / answer 컬렉션에 접근해야함.
    @PostMapping("/waiting/{postId}")
    public BaseResponse<Void> writeAnswer(
            @PathVariable(name = "postId") Long postId,
            @RequestPart(value = "req") PostReq.PostWaitingReq postWaitingReq,
            @RequestPart(value = "userId") String userId
    ) throws BaseException, IOException {
        if(userId == null)
            throw new BaseException(LOGIN_FIRST);
        postService.inWaitingQueue(postWaitingReq,postId,userId);
        return new BaseResponse<>(SUCCESS);
    }

    @PostMapping("/renew/{postId}")
    public BaseResponse<Void> renew(
            @PathVariable(name = "postId") Long postId,
            @RequestBody String userId
    ) throws BaseException, IOException {
        if(userId == null)
            throw new BaseException(LOGIN_FIRST);
        postService.renew(postId,userId);
        return new BaseResponse<Void>(SUCCESS);
    }
    @PostMapping("/stop/{postId}")
    public BaseResponse<Void> stopRecruit(
            @PathVariable(value = "postId") Long postId,
            @RequestBody String userId
    ) throws BaseException {
        postService.stateRecruit(postId,false,userId);
        return new BaseResponse<>(SUCCESS);
    }
    @PostMapping("/start/{postId}")
    public BaseResponse<Void> startRecruit(
            @PathVariable(value = "postId") Long postId,
            @RequestBody String userId
    ) throws BaseException {
        postService.stateRecruit(postId,true,userId);
        return new BaseResponse<>(SUCCESS);
    }

    @PostMapping("/grant/{postId}")
    public BaseResponse<Void> grantPost(
            @PathVariable(value = "postId") Long postId,
            @RequestPart(value = "score") String score,
            @RequestPart(value = "userId") String userId
            ) throws BaseException {
        postService.grantPost(postId, userId, Float.parseFloat( score) );
        return new BaseResponse<Void>(SUCCESS);
    }

    @GetMapping("/title/{postId}")
    public BaseResponse<Map> titlePost(
            @PathVariable(value = "postId") Long postId
    ) throws BaseException {
        return new BaseResponse<>(postService.titlePost(postId));
    }

}
