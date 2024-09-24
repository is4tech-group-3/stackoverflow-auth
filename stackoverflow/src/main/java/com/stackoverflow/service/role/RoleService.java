package com.stackoverflow.service.role;

import com.stackoverflow.bo.Role;
import com.stackoverflow.dto.role.RoleRequest;

import java.util.List;
import java.util.Optional;

public interface RoleService {
    List<Role> getAllRoles();
    Optional<Role> findRoleById(Long idRole);
    Role createRole(RoleRequest roleRequest);
    Role updateRole(Long idRole, RoleRequest roleRequest);
    void deleteRole(Long idRole);
    Role updateStatusRole(Long idRole);
}
