package com.stackoverflow.service.question;

import com.stackoverflow.bo.Question;
import com.stackoverflow.bo.User;
import com.stackoverflow.dto.question.QuestionDto;
import com.stackoverflow.repository.QuestionRepository;
import com.stackoverflow.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class QuestionServiceImpl implements QuestionService {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<Question> getAllQuestion() {
        return questionRepository.findAll();
    }

    @Override
    public Optional<Question> findQuestionById(Long idQuestion) {
        Optional<Question> questionFound = questionRepository.findById(idQuestion);
        if (questionFound.isEmpty()) {
            throw new EntityNotFoundException("Question not found");
        }
        return questionFound;
    }

    @Override
    public Question createQuestion(QuestionDto questionDto) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = ((User) userDetails).getId(); 

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));
            
        //user.setId(userId); 

        Question question = Question.builder()
                .title(questionDto.getTitle())
                .description(questionDto.getDescription())
                .user(user)
                .build();

        return questionRepository.save(question);
    }

    @Override
    public Question updateQuestion(Long idQuestion, QuestionDto questionDto) {
        Optional<Question> questionFound = questionRepository.findById(idQuestion);
        if(questionFound.isEmpty()){
            throw new EntityNotFoundException("Question not found");
        }
        Question question = questionFound.get();
        question.setTitle(questionDto.getTitle());
        question.setDescription(questionDto.getDescription());
        return questionRepository.save(question);
    }

    @Override
    public void deleteQuestion(Long idQuestion) {
        Optional<Question> questionFound = questionRepository.findById(idQuestion);
        if(questionFound.isEmpty()){
            throw new EntityNotFoundException("User not found");
        }
        questionRepository.deleteById(idQuestion);
    }
}
