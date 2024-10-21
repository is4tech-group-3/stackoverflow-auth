package com.stackoverflow.service.profile;

import com.stackoverflow.bo.Profile;
import com.stackoverflow.bo.Role;
import com.stackoverflow.dto.profile.ProfileRequest;
import com.stackoverflow.repository.ProfileRepository;
import com.stackoverflow.repository.RoleRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProfileServiceImplTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private Validator validator;

    @InjectMocks
    private ProfileServiceImpl profileService;

    private ProfileRequest profileRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        profileRequest = new ProfileRequest();
        profileRequest.setName("Admin");
        profileRequest.setDescription("Administrator role");
        profileRequest.setIdRoles(new HashSet<>(Arrays.asList(1L)));
    }

    @Test
    void testCreateProfileSuccess() {
        Role role = new Role();
        role.setIdRole(1L);

        when(profileRepository.findByName("Admin")).thenReturn(Optional.empty());
        when(roleRepository.findAllById(profileRequest.getIdRoles())).thenReturn(Arrays.asList(role));

        Profile savedProfile = new Profile();
        savedProfile.setName("Admin");
        savedProfile.setIdProfile(1L);
        when(profileRepository.save(any(Profile.class))).thenReturn(savedProfile);

        Profile createdProfile = profileService.createProfile(profileRequest);

        assertNotNull(createdProfile);
        assertEquals("Admin", createdProfile.getName());
        verify(profileRepository, times(1)).save(any(Profile.class));
    }

    @Test
    void testCreateProfileWhenProfileExists() {
        when(profileRepository.findByName("Admin")).thenReturn(Optional.of(new Profile()));

        assertThrows(DataIntegrityViolationException.class, () -> {
            profileService.createProfile(profileRequest);
        });
    }

    @Test
    void testFindProfileByIdSuccess() {
        Profile profile = new Profile();
        profile.setIdProfile(1L);
        profile.setName("Admin");

        when(profileRepository.findById(1L)).thenReturn(Optional.of(profile));

        Profile foundProfile = profileService.findProfileById(1L);
        assertNotNull(foundProfile);
        assertEquals("Admin", foundProfile.getName());
    }

    @Test
    void testFindProfileByIdWhenNotFound() {
        when(profileRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            profileService.findProfileById(1L);
        });
    }


    @Test
    void testUpdateProfileSuccess() {
        // Crear un perfil existente
        Profile existingProfile = new Profile();
        existingProfile.setIdProfile(1L);
        existingProfile.setName("Admin");
        existingProfile.setDescription("Administrator role");

        when(profileRepository.findById(1L)).thenReturn(Optional.of(existingProfile));
        when(profileRepository.findByName("User")).thenReturn(Optional.empty()); // No existe otro perfil con el mismo nombre

        ProfileRequest updateRequest = new ProfileRequest();
        updateRequest.setName("User");
        updateRequest.setDescription("User role");
        updateRequest.setIdRoles(new HashSet<>(Arrays.asList(1L)));

        Role role = new Role();
        role.setIdRole(1L);
        when(roleRepository.findAllById(updateRequest.getIdRoles())).thenReturn(Arrays.asList(role));

        Profile updatedProfile = new Profile();
        updatedProfile.setIdProfile(1L);
        updatedProfile.setName("User");
        updatedProfile.setDescription("User role");
        when(profileRepository.save(any(Profile.class))).thenReturn(updatedProfile);

        Profile result = profileService.updateProfile(1L, updateRequest);

        assertNotNull(result);
        assertEquals("User", result.getName());
        verify(profileRepository, times(1)).save(any(Profile.class));
    }

    @Test
    void testUpdateProfileWhenProfileNotFound() {
        when(profileRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            profileService.updateProfile(1L, profileRequest);
        });
    }

    @Test
    void testDeleteProfileSuccess() {
        doNothing().when(profileRepository).deleteById(1L);

        assertDoesNotThrow(() -> {
            profileService.deleteProfile(1L);
        });

        verify(profileRepository, times(1)).deleteById(1L);
    }

    @Test
    void testChangeStatusProfileSuccess() {
        Profile profile = new Profile();
        profile.setIdProfile(1L);
        profile.setStatus(true);
        when(profileRepository.findById(1L)).thenReturn(Optional.of(profile));
        when(profileRepository.save(profile)).thenReturn(profile);

        Profile updatedProfile = profileService.changeStatusProfile(1L);

        assertNotNull(updatedProfile);
        assertFalse(updatedProfile.getStatus()); // El estado debería cambiar a false
        verify(profileRepository, times(1)).save(profile);
    }

    @Test
    void testChangeStatusProfileWhenNotFound() {
        when(profileRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            profileService.changeStatusProfile(1L);
        });
    }


}
