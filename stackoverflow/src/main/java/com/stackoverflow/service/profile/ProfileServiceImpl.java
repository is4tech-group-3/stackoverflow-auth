package com.stackoverflow.service.profile;

import com.stackoverflow.bo.Role;
import com.stackoverflow.dto.profile.ProfileRequest;
import com.stackoverflow.repository.RoleRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import com.stackoverflow.bo.Profile;
import com.stackoverflow.repository.ProfileRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
@Service
public class ProfileServiceImpl implements ProfileService {

    private final RoleRepository roleRepository;
    private ProfileRepository profileRepository;

    @Override
    public Profile createProfile(ProfileRequest profileRequest) {
        Set<Role> roles = new HashSet<>(roleRepository.findAllById(profileRequest.getIdRoles()));
        Profile profile = Profile.builder()
                .name(profileRequest.getName())
                .description(profileRequest.getDescription())
                .status(true)
                .roles(roles)
                .build();
        return profileRepository.save(profile);
    }

    @Override
    public List<Profile> getProfiles() {
        return profileRepository.findAll();
    }

    @Override
    public Profile findProfileById(Long idProfile) {
        return profileRepository.findById(idProfile)
                .orElseThrow(() -> new EntityNotFoundException("Profile not found with id: " + idProfile));
    }

    @Override
    public Profile updateProfile(Long idProfile, ProfileRequest profileRequest) {
        Profile profile = profileRepository.findById(idProfile)
                .orElseThrow(() -> new EntityNotFoundException("Profile not found with id: " + idProfile));
        Set<Role> roles = new HashSet<>(roleRepository.findAllById(profileRequest.getIdRoles()));
        profile.setName(profileRequest.getName());
        profile.setDescription(profileRequest.getDescription());
        profile.setRoles(roles);
        return profileRepository.save(profile);
    }

    @Override
    public void deleteProfile(Long idProfile) {
        profileRepository.deleteById(idProfile);
    }


    @Override
    public Profile changeStatusProfile(Long id) {
        Profile profile = profileRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Profile not found with id: " + id));
        profile.setStatus(!profile.getStatus());
        return profileRepository.save(profile);
    }
}
