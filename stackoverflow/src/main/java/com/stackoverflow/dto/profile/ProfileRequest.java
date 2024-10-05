package com.stackoverflow.dto.profile;

import java.io.Serializable;
import java.util.Set;

import lombok.Data;

@Data
public class ProfileRequest implements Serializable{
    private String name;
    private String description;
    private Set<Long> idRoles;
}
