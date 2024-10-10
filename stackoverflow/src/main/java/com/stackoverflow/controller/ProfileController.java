package com.stackoverflow.controller;

import com.stackoverflow.bo.Profile;
import com.stackoverflow.dto.profile.ProfileRequest;
import com.stackoverflow.service.profile.ProfileService;

import java.util.List;

import org.springframework.web.bind.annotation.*;
import com.stackoverflow.util.AuditAnnotation;
import lombok.AllArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/profile")
public class ProfileController {

    private final ProfileService profileService;

    private final String ENTITY_NAME = "PROFILE";

    @AuditAnnotation(ENTITY_NAME)
    @PostMapping
    public ResponseEntity<Profile> createProfile(@RequestBody ProfileRequest profileRequest) {
        Profile profile = profileService.createProfile(profileRequest);
        return new ResponseEntity<>(profile, HttpStatus.CREATED);
    }

    @AuditAnnotation(ENTITY_NAME)
    @GetMapping()
    public Page<Profile> getProfiles(
            @RequestParam(defaultValue = "0") int page, 
            @RequestParam(defaultValue = "8") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {
        return profileService.getProfiles(page, size, sortBy, sortDirection);
    }

    @AuditAnnotation(ENTITY_NAME)
    @GetMapping("/{id}")
    public ResponseEntity<Profile> findProfileById(@PathVariable("id") Long idProfile) {
        Profile profile = profileService.findProfileById(idProfile);
        return new ResponseEntity<>(profile, HttpStatus.OK);
    }

    @AuditAnnotation(ENTITY_NAME)
    @PutMapping("/{id}")
    public ResponseEntity<Profile> updateProfile(@PathVariable("id") Long idProfile, @RequestBody ProfileRequest profileRequest) {
        Profile profile = profileService.updateProfile(idProfile, profileRequest);
        return new ResponseEntity<>(profile, HttpStatus.CREATED);
    }

    @AuditAnnotation(ENTITY_NAME)
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProfile(@PathVariable("id") Long idProfile) {
        profileService.deleteProfile(idProfile);
        return new ResponseEntity<>("Profile deleted successfully", HttpStatus.NO_CONTENT);
    }


    @PatchMapping("/{id}")
    public ResponseEntity<Profile> changeStatusProfile(@PathVariable("id") Long idProfile) {
        Profile profile = profileService.changeStatusProfile(idProfile);
        return new ResponseEntity<>(profile, HttpStatus.OK);
    }


}
