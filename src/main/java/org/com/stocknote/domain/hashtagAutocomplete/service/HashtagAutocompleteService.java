package org.com.stocknote.domain.hashtagAutocomplete.service;

import jakarta.annotation.PostConstruct;
import org.com.stocknote.domain.stock.repository.StockRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.com.stocknote.domain.hashtagAutocomplete.RedisSortedSetService;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class HashtagAutocompleteService {
    private final StockRepository stockRepository;
    private final RedisSortedSetService redisSortedSetService;
    private static final int MAX_SIZE = 10;

    @PostConstruct
    public void init() {
        redisSortedSetService.clearAll();

        List<String> allNames = stockRepository.findAllName();
        if (allNames != null && !allNames.isEmpty()) {
            redisSortedSetService.addAllToSortedSet(allNames);
        }
    }

    public List<String> autocomplete(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return Collections.emptyList();
        }

        return redisSortedSetService.autocomplete(keyword.trim());
    }
}


