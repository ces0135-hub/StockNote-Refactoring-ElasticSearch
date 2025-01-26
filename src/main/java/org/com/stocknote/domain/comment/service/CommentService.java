package org.com.stocknote.domain.comment.service;

import lombok.RequiredArgsConstructor;
import org.com.stocknote.domain.comment.dto.CommentRequest;
import org.com.stocknote.domain.comment.entity.Comment;
import org.com.stocknote.domain.comment.repository.CommentRepository;
import org.com.stocknote.domain.member.entity.Member;
import org.com.stocknote.domain.member.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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


}
