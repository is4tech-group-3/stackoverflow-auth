package com.stackoverflow.service.profile;

import com.stackoverflow.bo.Profile;
import com.stackoverflow.dto.profile.ProfileDto;
import java.util.List;

public interface ProfileService {
    Profile save(ProfileDto profile);

    Profile findById(Long idProfile);

    void delete(Profile profile);

    boolean existsById(Long idProfile);
    
    List<Profile> findAll();

    Profile updateStatusProfile(Long id);
}
