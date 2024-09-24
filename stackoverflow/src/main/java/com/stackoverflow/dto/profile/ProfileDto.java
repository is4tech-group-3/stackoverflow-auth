package com.stackoverflow.dto.profile;

import java.io.Serializable;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@Builder
public class ProfileDto implements Serializable{
    private Long idProfile;
    private String name;
    private String description;
    private Boolean status;
}
