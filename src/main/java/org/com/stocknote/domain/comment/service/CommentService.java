package org.com.stocknote.domain.comment.service;

import lombok.RequiredArgsConstructor;
import org.com.stocknote.domain.comment.dto.CommentDetailResponse;
import org.com.stocknote.domain.comment.dto.CommentRequest;
import org.com.stocknote.domain.comment.dto.CommentUpdateDto;
import org.com.stocknote.domain.comment.entity.Comment;
import org.com.stocknote.domain.comment.repository.CommentRepository;
import org.com.stocknote.domain.member.entity.Member;
import org.com.stocknote.domain.member.repository.MemberRepository;
import org.com.stocknote.domain.notification.repository.CommentNotificationRepository;
import org.com.stocknote.domain.post.entity.Post;
import org.com.stocknote.domain.post.repository.PostRepository;
import org.com.stocknote.global.error.ErrorCode;
import org.com.stocknote.global.exception.CustomException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final CommentNotificationRepository commentNotificationRepository;


    @Transactional(readOnly = true)
    public CommentDetailResponse getCommentDetail(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));

        Member member = memberRepository.findById(comment.getMember().getId()).orElseThrow(() -> new IllegalArgumentException("user not found"));

        return new CommentDetailResponse(commentId, comment.getBody(), comment.getCreatedAt(), member.getId(), member.getName(),member.getProfile() );
    }

    public Page<CommentDetailResponse> getComments(Long postId, Pageable pageable) {
        Page<CommentDetailResponse> comments = commentRepository.findByPostId(pageable, postId)
                .map(comment -> {
                            Member member = memberRepository.findById(comment.getMember().getId()).orElseThrow(() -> new IllegalArgumentException("user not found"));
                            return new CommentDetailResponse(comment.getId(), comment.getBody(), comment.getCreatedAt(), member.getId(), member.getName(),member.getProfile());
                        }
                );
        return comments;
    }

    @Transactional
    public Comment createComment(Long postId, CommentRequest commentRequest, Member member) {
        Post post= postRepository.findById(postId).orElseThrow();
        Comment comment = new Comment(post, commentRequest.getBody(), member);
        commentRepository.save(comment);
        return comment;
    }

    @Transactional
    public void updateComment(CommentUpdateDto commentUpdateDto, Member member) {
        Comment comment = commentRepository.findById(commentUpdateDto.getCommentId())
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));

        if (!Objects.equals(member.getId(), comment.getMember().getId())) {
            throw new CustomException(ErrorCode.COMMENT_UPDATE_DENIED);
        }

        comment.setBody(commentUpdateDto.getBody());
        commentRepository.save(comment);
    }

    @Transactional
    public void deleteComment(Long commentId, Member member) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));

        if (!Objects.equals(member.getId(), comment.getMember().getId())) {
            throw new CustomException(ErrorCode.COMMENT_DELETE_DENIED);
        }
        commentNotificationRepository.deleteByRelatedCommentId(commentId);
        commentRepository.delete(comment);
    }


}
