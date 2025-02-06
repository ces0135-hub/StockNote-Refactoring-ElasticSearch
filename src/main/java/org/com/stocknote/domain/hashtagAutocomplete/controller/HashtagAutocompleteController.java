package org.com.stocknote.domain.hashtagAutocomplete.controller;

import lombok.RequiredArgsConstructor;
import org.com.stocknote.domain.hashtagAutocomplete.service.HashtagAutocompleteService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/hashtag")
@RequiredArgsConstructor
public class HashtagAutocompleteController {
    private final HashtagAutocompleteService hashtagAutocompleteService;

    @GetMapping("/search/{q}")
    public List<String> query(@PathVariable("q") String query) {
        return hashtagAutocompleteService.autocorrect(query);
    }
}
