package org.com.stocknote.domain.hashtag.service;

import lombok.RequiredArgsConstructor;
import org.com.stocknote.domain.hashtag.entity.Hashtag;
import org.com.stocknote.domain.hashtag.repository.HashtagRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HashtagService {
    private final HashtagRepository hashtagRepository;

    @Transactional
    public void createHashtags(long postId, List<String> hashtagNames) {
        if (hashtagNames == null || hashtagNames.isEmpty()) {
            return;
        }

        List<Hashtag> hashtags = hashtagNames.stream()
                .map(name -> Hashtag.create(name, postId))
                .collect(Collectors.toList());

        hashtagRepository.saveAll(hashtags);
    }

    @Transactional(readOnly = true)
    public List<Hashtag> getHashtagsByPostId(Long postId) {
        return hashtagRepository.findByPostId(postId);
    }

    @Transactional
    public void updateHashtags(Long postId, List<String> newHashtagNames) {
        hashtagRepository.deleteByPostId(postId);
        createHashtags(postId, newHashtagNames);
    }

    @Transactional
    public void deleteHashtagsByPostId(Long postId) {
        hashtagRepository.deleteByPostId(postId);
    }
}
