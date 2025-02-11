package org.com.stocknote.domain.searchDoc.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.com.stocknote.domain.post.dto.PostResponseDto;
import org.com.stocknote.domain.post.dto.PostSearchConditionDto;
import org.com.stocknote.domain.searchDoc.document.PortfolioDoc;
import org.com.stocknote.domain.searchDoc.document.PortfolioStockDoc;
import org.com.stocknote.domain.searchDoc.document.PostDoc;
import org.com.stocknote.domain.searchDoc.document.StockDoc;
import org.com.stocknote.domain.searchDoc.dto.request.SearchKeyword;
import org.com.stocknote.domain.searchDoc.dto.response.SearchPortfolioResponse;
import org.com.stocknote.domain.searchDoc.dto.response.SearchedStockResponse;
import org.com.stocknote.domain.searchDoc.service.SearchDocService;
import org.com.stocknote.global.globalDto.GlobalResponse;
import org.com.stocknote.oauth.entity.PrincipalDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/searchDocs")
@Tag(name = "검색 API", description = "검색(Search)")
public class SearchDocController {
  private final SearchDocService searchDocService;

  @PostMapping("/stock")
  @Operation(summary = "종목 조회")
  public GlobalResponse<List<SearchedStockResponse>> searchStocks(
      @RequestBody SearchKeyword searchKeyword
      ) {
    List<StockDoc> stockList = searchDocService.searchStocks(searchKeyword.getKeyword());
    List<SearchedStockResponse> response =
        stockList.stream().map(SearchedStockResponse::of).collect(Collectors.toList());
    return GlobalResponse.success(response);
  }

  @GetMapping("/myPortfolio")
  @Operation(summary = "포트폴리오 몰아보기")
  public GlobalResponse<SearchPortfolioResponse> getMyPortfolioList(
      @AuthenticationPrincipal PrincipalDetails principalDetails
  ) {
    String email = principalDetails.getUsername();
    PortfolioDoc portfolioDoc = searchDocService.getMyPortfolioList(email);
    List<PortfolioStockDoc> portfolioStockDocList = searchDocService.getMyPortfolioStockList(email);
    SearchPortfolioResponse
        response = SearchPortfolioResponse.from(portfolioDoc, portfolioStockDocList);
    return GlobalResponse.success(response);

  }

  @GetMapping("/post/search")
  @Operation(summary = "게시글 검색")
  public GlobalResponse<Page<PostResponseDto>> searchPosts(
      @ModelAttribute PostSearchConditionDto condition,
      @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
  ) {
    Page<PostDoc> postDocs = searchDocService.searchPosts(condition, pageable);
    Page<PostResponseDto> response = postDocs.map(PostResponseDto::fromPost);
    return GlobalResponse.success(response);
  }
}
