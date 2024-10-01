package com.stackoverflow.bo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "questions")
public class Question implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_id", nullable = false, updatable = false)
    private Long idQuestion;

    private String title;
    private String description;

    @CreationTimestamp
    @Column(updatable = false, name = "date_creation")
    private LocalDate dateCreate;

    @UpdateTimestamp
    @Column(name = "date_update")
    private LocalDate dateUpdate;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

}
