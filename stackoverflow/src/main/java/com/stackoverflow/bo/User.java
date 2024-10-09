package com.stackoverflow.bo;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Size(max = 50, message = "The name must not be longer than 50 characters")
    @NotNull(message = "The name field cannot be null")
    @NotBlank(message = "Name is required")
    @Column(nullable = false)
    private String name;

    @Size(max = 50, message = "The surname must not be longer than 50 characters")
    @NotNull(message = "The surname field cannot be null")
    @NotBlank(message = "Surname is required")
    @Column(nullable = false)
    private String surname;

    @NotNull(message = "The email field cannot be null")
    @NotBlank(message =  "Email is required")
    @Email(message = "Email should be valid")
    @Column(unique = true, length = 50, nullable = false)
    private String email;

    @Size(max = 50, message = "The username must not be longer than 50 characters")
    @NotNull(message = "The username field cannot be null")
    @NotBlank(message =  "Username is required")
    @Column(nullable = false)
    private String username;

    @NotNull(message = "The password field cannot be null")
    @NotBlank(message =  "Password is required")
    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private Boolean status = true;

    @CreationTimestamp
    @Column(updatable = false, name = "created")
    private LocalDate created;

    @Column(name = "profile_id")
    private Long profileId;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return status != null && status;
    }
}