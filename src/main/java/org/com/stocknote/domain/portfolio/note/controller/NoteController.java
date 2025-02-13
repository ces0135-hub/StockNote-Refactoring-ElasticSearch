package org.com.stocknote.domain.portfolio.note.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.com.stocknote.domain.portfolio.note.dto.NoteResponse;
import org.com.stocknote.domain.portfolio.note.entity.Note;
import org.com.stocknote.domain.portfolio.note.service.NoteService;
import org.com.stocknote.domain.portfolio.portfolio.service.PortfolioService;
import org.com.stocknote.global.globalDto.GlobalResponse;
import org.com.stocknote.oauth.entity.PrincipalDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/portfolios")
@Tag(name = "포트폴리오 노트 API", description = "포트폴리오 노트 API")
public class NoteController {
  private final NoteService noteService;
  private final PortfolioService portfolioService;

  @GetMapping("/{portfolio_no}/note")
  @Operation(summary = "포트폴리오 노트 조회")
  public GlobalResponse<List<NoteResponse>> getNote(@PathVariable("portfolio_no") Long portfolioNo) {
    List<Note> noteList = noteService.getNoteByPortfolioNo(portfolioNo);
    List<NoteResponse> response = noteList.stream().map(NoteResponse::from).collect(Collectors.toList());
    return GlobalResponse.success(response);
  }

  @GetMapping("/allNote")
  @Operation(summary = "노트 리스트 조회")
  public GlobalResponse<List<NoteResponse>> getNoteList(
      @AuthenticationPrincipal PrincipalDetails principalDetails
  ) {
    String email = principalDetails.getUsername();
    List<Note> noteList = noteService.getNoteList(email);
    List<NoteResponse> response = noteList.stream().map(NoteResponse::from).collect(Collectors.toList());
    return GlobalResponse.success(response);
  }

  @GetMapping("/allNote/forTest")
  @Operation(summary = "테스트 노트 조회")
  public GlobalResponse<List<NoteResponse>> getAllNoteList(
  ) {
    List<Note> noteList = noteService.getAllNoteList();
    List<NoteResponse> response = noteList.stream().map(NoteResponse::from).collect(Collectors.toList());
    return GlobalResponse.success(response);
  }
}
