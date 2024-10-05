package com.stackoverflow.service.profile;

import com.stackoverflow.bo.Profile;
import com.stackoverflow.dto.profile.ProfileRequest;

import java.util.List;

public interface ProfileService {
    Profile createProfile(ProfileRequest profileRequest);

    List<Profile> getProfiles();

    Profile findProfileById(Long idProfile);

    Profile updateProfile(Long idProfile, ProfileRequest profileRequest);

    void deleteProfile(Long idProfile);

    Profile changeStatusProfile(Long idProfile);
}
