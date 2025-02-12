package org.com.stocknote.domain.member.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChangeNameRequest {
    @NotBlank(message = "이름을 작성해주세요")
    @Size(min = 2, max = 50, message = "2~50자로 작성해주세요")
    private String name;
}
