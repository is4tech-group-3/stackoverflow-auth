package com.stackoverflow.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stackoverflow.bo.Question;
import com.stackoverflow.dto.question.QuestionDto;
import com.stackoverflow.service.question.QuestionService;
import com.stackoverflow.util.LoggerUtil;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1/question")
public class QuestionController {
    @Autowired
    private QuestionService questionService;

    @PostMapping
    public ResponseEntity<Question> createQuestion(@RequestBody QuestionDto questionDto, HttpServletRequest request) {
        try {
            Question question = questionService.createQuestion(questionDto);
            LoggerUtil.loggerInfo(request, HttpStatus.CREATED, "Question successfully added");
            return new ResponseEntity<>(question, HttpStatus.CREATED);
        } catch (Exception e) {
            LoggerUtil.loggerError(request, HttpStatus.INTERNAL_SERVER_ERROR, "Error adding the question", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public ResponseEntity<List<Question>> getQuestions(HttpServletRequest request) {
        try {
            List<Question> question = questionService.getAllQuestion();
            LoggerUtil.loggerInfo(request, HttpStatus.OK, "Questions successfully listed");
            return new ResponseEntity<>(question, HttpStatus.OK);
        } catch (Exception e) {
            LoggerUtil.loggerError(request, HttpStatus.INTERNAL_SERVER_ERROR, "Error listing the question", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Question> getQuestion(@PathVariable(name = "id") Long id, HttpServletRequest request) {
        try {
            Optional<Question> questionFound = questionService.findQuestionById(id);
            LoggerUtil.loggerInfo(request, HttpStatus.OK, "Question successfully listed");
            return new ResponseEntity<>(questionFound.get(), HttpStatus.OK);
        } catch (Exception e) {
            LoggerUtil.loggerError(request, HttpStatus.INTERNAL_SERVER_ERROR, "Error listing the question", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Question> updateQuestion(@PathVariable(name = "id") Long id,
            @RequestBody QuestionDto questionDto, HttpServletRequest request) {
        try {
            Question question = questionService.updateQuestion(id, questionDto);
            LoggerUtil.loggerInfo(request, HttpStatus.OK, "Question updated correctly");
            return new ResponseEntity<>(question, HttpStatus.OK);
        } catch (Exception e) {
            LoggerUtil.loggerError(request, HttpStatus.INTERNAL_SERVER_ERROR, "Error updating the question", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteQuestion(@PathVariable(name = "id") Long id, HttpServletRequest request) {
        try {
            questionService.deleteQuestion(id);
            LoggerUtil.loggerInfo(request, HttpStatus.NO_CONTENT, "Question successfully eliminated");
            return new ResponseEntity<>("Question deleted successfully", HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            LoggerUtil.loggerError(request, HttpStatus.INTERNAL_SERVER_ERROR, "Error deleting question", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
