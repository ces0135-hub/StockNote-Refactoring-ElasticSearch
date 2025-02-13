package org.com.stocknote.domain.post.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.com.stocknote.domain.hashtag.entity.QHashtag;
import org.com.stocknote.domain.member.entity.QMember;
import org.com.stocknote.domain.post.dto.PostSearchConditionDto;
import org.com.stocknote.domain.post.entity.Post;
import org.com.stocknote.domain.post.entity.PostCategory;
import org.com.stocknote.domain.post.entity.QPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@Repository
@RequiredArgsConstructor
public class PostSearchRepositoryImpl implements PostSearchRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Post> search(PostSearchConditionDto condition, Pageable pageable) {
        QPost post = QPost.post;
        QMember member = QMember.member;
        QHashtag hashtag = QHashtag.hashtag;

        BooleanBuilder builder = new BooleanBuilder();

        if (condition.getCategory() != null) {
            try {
                PostCategory categoryEnum = PostCategory.valueOf(condition.getCategory());
                builder.and(post.category.eq(categoryEnum));
            } catch (IllegalArgumentException e) {
                // 잘못된 카테고리 문자열이 들어온 경우의 처리
                // 1. 무시하고 지나가거나
                // 2. 예외를 던지거나
                // 3. 기본 카테고리로 설정하는 등의 처리가 가능
            }
        }

        if (StringUtils.hasText(condition.getKeyword())) {
            switch (condition.getSearchType()) {
                case TITLE ->
                        builder.and(post.title.containsIgnoreCase(condition.getKeyword()));
                case CONTENT ->
                        builder.and(post.body.containsIgnoreCase(condition.getKeyword()));
                case USERNAME ->
                        builder.and(post.member.name.containsIgnoreCase(condition.getKeyword()));
                case HASHTAG ->
                        builder.and(hashtag.name.containsIgnoreCase(condition.getKeyword()));
                case ALL ->
                        builder.andAnyOf(
                                post.title.containsIgnoreCase(condition.getKeyword()),
                                post.body.containsIgnoreCase(condition.getKeyword()),
                                post.member.name.containsIgnoreCase(condition.getKeyword()),
                                hashtag.name.containsIgnoreCase(condition.getKeyword())
                        );
            }
        }

        QueryResults<Post> results = queryFactory
                .selectFrom(post)
                .leftJoin(post.member, member)
                .leftJoin(hashtag).on(hashtag.postId.eq(post.id))
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(post.createdAt.desc())
                .fetchResults();

        return new PageImpl<>(results.getResults(), pageable, results.getTotal());
    }
}
