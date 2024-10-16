package com.stackoverflow.service.profile;

import com.stackoverflow.bo.Role;
import com.stackoverflow.dto.profile.ProfileRequest;
import com.stackoverflow.repository.RoleRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.AllArgsConstructor;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.stackoverflow.bo.Profile;
import com.stackoverflow.repository.ProfileRepository;

import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@Service
public class ProfileServiceImpl implements ProfileService {

    private final RoleRepository roleRepository;
    private final ProfileRepository profileRepository;
    private final Validator validator;

    @Override
    public Profile createProfile(ProfileRequest profileRequest) {
        if (profileRepository.findByName(profileRequest.getName()).isPresent())
            throw new DataIntegrityViolationException("Profile already exists");

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
    public Page<Profile> getProfiles(int page, int size, String sortBy, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        return profileRepository.findAll(pageable);
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

        if (!profile.getName().equals(profileRequest.getName()) && profileRepository.findByName(profileRequest.getName()).isPresent()) {
            throw new DataIntegrityViolationException("Profile name already exists");
        }

        Set<Role> roles = new HashSet<>(roleRepository.findAllById(profileRequest.getIdRoles()));
        profile.setName(profileRequest.getName());
        profile.setDescription(profileRequest.getDescription());
        profile.setRoles(roles);

        Set<ConstraintViolation<Profile>> violations = validator.validate(profile);
        if (!violations.isEmpty()) throw new ConstraintViolationException(violations);

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
