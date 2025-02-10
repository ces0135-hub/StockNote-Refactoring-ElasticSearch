package org.com.stocknote.domain.member.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.com.stocknote.domain.comment.entity.Comment;
import org.com.stocknote.domain.comment.repository.CommentRepository;
import org.com.stocknote.domain.member.dto.*;
import org.com.stocknote.domain.member.entity.Member;
import org.com.stocknote.domain.member.repository.MemberRepository;
import org.com.stocknote.domain.post.entity.Post;
import org.com.stocknote.domain.post.repository.PostRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {
    private final MemberRepository memberRepository;
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    @Transactional
    public Member updateProfile(Long id, ChangeNameRequest request) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("회원을 찾을 수 없습니다. ID: " + id));

        member.setName(request.getName());
        return member;
    }

    @Transactional(readOnly = true)
    public Page<MyPostResponse> findPostsByMember(Member member, Pageable pageable) {
        Page<Post> memberPosts = postRepository.findByMember(member, pageable);
        return memberPosts.map(MyPostResponse::of);
    }

    @Transactional(readOnly = true)
    public Page<MyCommentResponse> findCommentsByMember(Member member, Pageable pageable) {
        Page<Comment> memberComments = commentRepository.findByMember(member, pageable);
        return memberComments.map(MyCommentResponse::of);
    }

}
