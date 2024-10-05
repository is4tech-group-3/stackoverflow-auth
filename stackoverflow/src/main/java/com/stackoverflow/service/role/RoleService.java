package com.stackoverflow.service.role;

import com.stackoverflow.bo.Role;
import com.stackoverflow.dto.role.RoleRequest;

import java.util.List;

public interface RoleService {
    Role createRole(RoleRequest roleRequest);

    List<Role> getRoles();

    Role findRoleById(Long idRole);

    Role updateRole(Long idRole, RoleRequest roleRequest);

    void deleteRole(Long idRole);

    Role changeStatusRole(Long idRole);
}
