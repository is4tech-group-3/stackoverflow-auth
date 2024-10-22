package com.stackoverflow.service.profile;

import com.stackoverflow.bo.Profile;
import com.stackoverflow.dto.profile.ProfileRequest;

import org.springframework.data.domain.Page;

public interface ProfileService {
    Profile createProfile(ProfileRequest profileRequest);

    Page<Profile> getProfiles(int page, int size, String sortBy, String sortDirection);

    Profile findProfileById(Long idProfile);

    Profile updateProfile(Long idProfile, ProfileRequest profileRequest);

    void deleteProfile(Long idProfile);

    Profile changeStatusProfile(Long idProfile);
}
