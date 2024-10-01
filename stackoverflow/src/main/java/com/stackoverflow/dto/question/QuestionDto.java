package com.stackoverflow.dto.question;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class QuestionDto {
    private Long idQuestion;
    private String title;
    private String description;
}
