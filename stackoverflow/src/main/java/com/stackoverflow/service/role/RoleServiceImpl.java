package com.stackoverflow.service.role;

import com.stackoverflow.bo.Role;
import com.stackoverflow.dto.role.RoleRequest;
import com.stackoverflow.repository.RoleRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class RoleServiceImpl implements RoleService {

    private RoleRepository roleRepository;

    @Override
    public Role createRole(RoleRequest roleRequest) {
        Role role = Role.builder()
                .name(roleRequest.getName())
                .description(roleRequest.getDescription())
                .status(true)
                .build();
        return roleRepository.save(role);
    }

    public List<Role> getRoles() {
        return roleRepository.findAll();
    }

    @Override
    public Role findRoleById(Long idRole) {
        return roleRepository.findById(idRole)
                .orElseThrow(() -> new EntityNotFoundException("Role not found with id: " + idRole));
    }


    @Override
    public Role updateRole(Long idRole, RoleRequest roleRequest) {
        Role role = roleRepository.findById(idRole)
                .orElseThrow(() -> new EntityNotFoundException("Role not found with id: " + idRole));
        role.setName(roleRequest.getName());
        role.setDescription(roleRequest.getDescription());
        return roleRepository.save(role);
    }

    @Override
    public void deleteRole(Long idRole) {
        roleRepository.deleteById(idRole);
    }

    @Override
    public Role changeStatusRole(Long idRole) {
        Role role = roleRepository.findById(idRole)
                .orElseThrow(() -> new EntityNotFoundException("Role not found with id: " + idRole));
        role.setStatus(!role.getStatus());
        return roleRepository.save(role);
    }
}
