package org.com.stocknote.domain.portfolio.note.service;

import lombok.RequiredArgsConstructor;
import org.com.stocknote.domain.portfolio.note.dto.NoteRequest;
import org.com.stocknote.domain.portfolio.note.entity.Note;
import org.com.stocknote.domain.portfolio.note.repository.NoteRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NoteService {
  private final NoteRepository noteRepository;

  public void save(NoteRequest noteRequest) {
    // Save note
    Note note = Note.builder()
      .title(noteRequest.getTitle())
      .content(noteRequest.getContent())
      .portfolio(noteRequest.getPortfolio())
      .member(noteRequest.getMember())
      .build();
    noteRepository.save(note);
  }

  public void update(Long id, NoteRequest noteRequest) {
    // Update note
    Note note = noteRepository.findById(id).orElseThrow();
    note.setTitle(noteRequest.getTitle());
    note.setContent(noteRequest.getContent());
    noteRepository.save(note);
  }

  public void delete(Long id) {
      // Delete note
      noteRepository.deleteById(id);
  }
}
