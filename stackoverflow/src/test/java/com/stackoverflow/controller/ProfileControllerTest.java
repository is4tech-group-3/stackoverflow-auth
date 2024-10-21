package com.stackoverflow.controller;

import com.stackoverflow.bo.Profile;
import com.stackoverflow.dto.profile.ProfileRequest;
import com.stackoverflow.service.profile.ProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProfileControllerTest {

    private ProfileController profileController;
    private ProfileService profileService;

    @BeforeEach
    void setUp() {
        profileService = mock(ProfileService.class);
        profileController = new ProfileController(profileService);
    }

    @Test
    void testCreateProfileSuccessful() {
        ProfileRequest profileRequest = new ProfileRequest();
        profileRequest.setName("UserProfile");
        profileRequest.setDescription("User description");

        Profile createdProfile = new Profile(1L, "UserProfile", "User description", true, new HashSet<>());
        when(profileService.createProfile(any())).thenReturn(createdProfile);

        ResponseEntity<Profile> response = profileController.createProfile(profileRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("UserProfile", response.getBody().getName());
        verify(profileService).createProfile(profileRequest);
    }

    @Test
    void testGetProfiles() {
        Profile profile = new Profile(1L, "UserProfile", "User description", true, new HashSet<>());
        Page<Profile> profilePage = new PageImpl<>(List.of(profile));
        when(profileService.getProfiles(0, 8, "name", "desc")).thenReturn(profilePage);

        Page<Profile> response = profileController.getProfiles(0, 8, "name", "desc");

        assertEquals(1, response.getTotalElements());
        assertEquals("UserProfile", response.getContent().get(0).getName());
        verify(profileService).getProfiles(0, 8, "name", "desc");
    }

    @Test
    void testFindProfileById() {
        Profile profile = new Profile(1L, "UserProfile", "User description", true, new HashSet<>());
        when(profileService.findProfileById(1L)).thenReturn(profile);

        ResponseEntity<Profile> response = profileController.findProfileById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("UserProfile", response.getBody().getName());
        verify(profileService).findProfileById(1L);
    }

    @Test
    void testUpdateProfile() {
        ProfileRequest profileRequest = new ProfileRequest();
        profileRequest.setName("UpdatedProfile");
        profileRequest.setDescription("Updated description");

        Profile updatedProfile = new Profile(1L, "UpdatedProfile", "Updated description", true, new HashSet<>());
        when(profileService.updateProfile(1L, profileRequest)).thenReturn(updatedProfile);

        ResponseEntity<Profile> response = profileController.updateProfile(1L, profileRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("UpdatedProfile", response.getBody().getName());
        verify(profileService).updateProfile(1L, profileRequest);
    }

    @Test
    void testDeleteProfile() {
        doNothing().when(profileService).deleteProfile(1L);
        ResponseEntity<String> response = profileController.deleteProfile(1L);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(profileService).deleteProfile(1L);
    }

    @Test
    void testChangeStatusProfile() {
        Profile profile = new Profile(1L, "UserProfile", "User description", true, new HashSet<>());
        when(profileService.changeStatusProfile(1L)).thenReturn(profile);

        ResponseEntity<Profile> response = profileController.changeStatusProfile(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("UserProfile", response.getBody().getName());
        verify(profileService).changeStatusProfile(1L);
    }
}
