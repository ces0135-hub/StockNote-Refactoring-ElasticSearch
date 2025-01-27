package org.com.stocknote.domain.comment.service;

import lombok.RequiredArgsConstructor;
import org.com.stocknote.domain.comment.dto.CommentRequest;

import org.com.stocknote.domain.comment.dto.CommentUpdateDto;

import org.com.stocknote.domain.comment.entity.Comment;
import org.com.stocknote.domain.comment.repository.CommentRepository;
import org.com.stocknote.domain.member.entity.Member;
import org.com.stocknote.domain.member.repository.MemberRepository;

import org.com.stocknote.global.error.ErrorCode;
import org.com.stocknote.global.exception.CustomException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public Long createComment(Long postId, CommentRequest commentRequest, String userEmail) {
        Member member = memberRepository.findByEmail(userEmail).orElseThrow();
        Comment comment = new Comment(postId, commentRequest.getBody(), member.getId());

        return commentRepository.save(comment).getId();
    }

    @Transactional
    public void updateComment(CommentUpdateDto commentUpdateDto) {
        Member member = memberRepository.findByEmail(commentUpdateDto.getUserEmail()).orElseThrow(() -> new IllegalArgumentException("user not found"));
        Comment comment = commentRepository.findById(commentUpdateDto.getCommentId())
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));

        if (!Objects.equals(member.getId(), comment.getUserId())) {
            throw new CustomException(ErrorCode.COMMENT_UPDATE_DENIED);
        }

        comment.setBody(commentUpdateDto.getBody());
        commentRepository.save(comment);
    }

}
