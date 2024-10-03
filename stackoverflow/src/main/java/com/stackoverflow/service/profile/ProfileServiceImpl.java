package com.stackoverflow.service.profile;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stackoverflow.bo.Profile;
import com.stackoverflow.dto.profile.ProfileDto;
import com.stackoverflow.repository.ProfileRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ProfileServiceImpl implements ProfileService {

    @Autowired
    private ProfileRepository profileRepository;

    @Override
    public Profile save(ProfileDto profileDto) {
        Profile profile = Profile.builder()
                .idProfile(profileDto.getIdProfile())
                .name(profileDto.getName())
                .description(profileDto.getDescription())
                .status(profileDto.getStatus())
                .build();
        return profileRepository.save(profile);
    }

    @Override
    public Profile findById(Long idProfile) {
        return profileRepository.findById(idProfile).orElse(null);
    }

    @Override
    public void delete(Profile profile) {
        profileRepository.delete(profile);
    }

    @Override
    public boolean existsById(Long idProfile) {
        return profileRepository.existsById(idProfile);
    }

    @Override
    public List<Profile> findAll() {
        return profileRepository.findAll(); 
    }

    @Override
    public Profile updateStatusProfile(Long id) {
        Optional<Profile> profileFound = profileRepository.findById(id);
        if(profileFound.isEmpty()){
            throw new EntityNotFoundException("Profile not found");
        }
        Profile profile = profileFound.get();
        profile.setStatus(!profile.getStatus());
        return profileRepository.save(profile);
    }
}
