package com.stackoverflow.bo;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@Table(name = "roles")
@NoArgsConstructor
@AllArgsConstructor
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id", unique = true, nullable = false)
    private Long idRole;

    @Size(max = 20, message = "The name must not be longer than 20 characters")
    @NotNull(message = "The name field cannot be null")
    @NotBlank(message = "Name is required")
    private String name;

    @Size(max = 50, message = "The description must not be longer than 50 characters")
    @NotNull(message = "The description field cannot be null")
    @NotBlank(message = "Description is required")
    private String description;

    private Boolean status;
}
