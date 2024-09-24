package com.stackoverflow.controller.profile;

import com.stackoverflow.bo.Profile;
import com.stackoverflow.dto.profile.ProfileDto;
import com.stackoverflow.service.profile.ProfileService;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/profile")
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    @PostMapping
    public ResponseEntity<ProfileDto> create(@RequestBody ProfileDto profileDto) {
        try {
            Profile profileSave = profileService.save(profileDto);
            ProfileDto profileResponse = ProfileDto.builder()
                    .idProfile(profileSave.getIdProfile())
                    .name(profileSave.getName())
                    .description(profileSave.getDescription())
                    .status(profileSave.getStatus())
                    .build();
            return new ResponseEntity<>(profileResponse, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProfileDto> update(@RequestBody ProfileDto profileDto, @PathVariable Long id) {
        try {
            if (profileService.existsById(id)) {
                profileDto.setIdProfile(id);
                Profile profileUpdate = profileService.save(profileDto);

                ProfileDto profileResponse = ProfileDto.builder()
                        .idProfile(profileUpdate.getIdProfile())
                        .name(profileUpdate.getName())
                        .description(profileUpdate.getDescription())
                        .status(profileUpdate.getStatus())
                        .build();

                return new ResponseEntity<>(profileResponse, HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            if (profileService.existsById(id)) {
                Profile profileDelete = profileService.findById(id);
                profileService.delete(profileDelete);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProfileDto> findById(@PathVariable Long id) {
        try {
            Profile profile = profileService.findById(id);
            if (profile != null) {
                ProfileDto profileDto = ProfileDto.builder()
                        .idProfile(profile.getIdProfile())
                        .name(profile.getName())
                        .description(profile.getDescription())
                        .status(profile.getStatus())
                        .build();
                return new ResponseEntity<>(profileDto, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping()
    public ResponseEntity<List<ProfileDto>> listAll() {
        try {
            List<Profile> profiles = profileService.findAll();
            List<ProfileDto> profileDtos = profiles.stream()
                    .map(profile -> ProfileDto.builder()
                            .idProfile(profile.getIdProfile())
                            .name(profile.getName())
                            .description(profile.getDescription())
                            .status(profile.getStatus())
                            .build())
                    .collect(Collectors.toList());
            return new ResponseEntity<>(profileDtos, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
