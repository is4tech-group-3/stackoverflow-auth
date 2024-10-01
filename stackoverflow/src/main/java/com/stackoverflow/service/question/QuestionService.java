package com.stackoverflow.service.question;

import com.stackoverflow.bo.Question;
import com.stackoverflow.dto.question.QuestionDto;

import java.util.List;
import java.util.Optional;

public interface QuestionService {
    List<Question> getAllQuestion();
    Optional<Question> findQuestionById(Long idQuestion);
    Question createQuestion(QuestionDto questionDto);
    Question updateQuestion(Long idQuestion, QuestionDto questionDto);
    void deleteQuestion(Long idQuestion);
}
