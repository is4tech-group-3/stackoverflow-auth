package com.stackoverflow.service.role;

import com.stackoverflow.bo.Role;
import com.stackoverflow.dto.role.RoleRequest;
import com.stackoverflow.repository.RoleRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    @Override
    public Optional<Role> findRoleById(Long idRole) {
        Optional<Role> roleFound = roleRepository.findById(idRole);
        if(roleFound.isEmpty()) {
            throw new EntityNotFoundException("Role not found with id " + idRole);
        }
        return roleFound;
    }

    @Override
    public Role createRole(RoleRequest roleRequest) {
        Role role = Role.builder()
                .name(roleRequest.getName())
                .description(roleRequest.getDescription())
                .status(true)
                .build();
        return roleRepository.save(role);
    }

    @Override
    public Role updateRole(Long idRole, RoleRequest roleRequest) {
        Optional<Role> roleFound = roleRepository.findById(idRole);
        if (roleFound.isEmpty()) {
            throw new EntityNotFoundException("Role not found with ID: " + idRole);
        }
        Role role = roleFound.get();
        role.setName(roleRequest.getName());
        role.setDescription(roleRequest.getDescription());
        return roleRepository.save(role);
    }

    @Override
    public void deleteRole(Long idRole) {
        Optional<Role> roleFound = roleRepository.findById(idRole);
        if (roleFound.isEmpty()) {
            throw new EntityNotFoundException("Role not found with ID: " + idRole);
        }
        roleRepository.deleteById(idRole);
    }

    @Override
    public Role updateStatusRole(Long idRole) {
        Optional<Role> roleFound = roleRepository.findById(idRole);
        if (roleFound.isEmpty()) {
            throw new EntityNotFoundException("Role not found with ID: " + idRole);
        }
        Role role = roleFound.get();
        role.setStatus(!role.getStatus());
        return roleRepository.save(role);
    }
}
