package com.stackoverflow.service.role;

import com.stackoverflow.bo.Role;
import com.stackoverflow.dto.role.RoleRequest;
import com.stackoverflow.repository.RoleRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@AllArgsConstructor
@Service
public class RoleServiceImpl implements RoleService {

    private static final String ROLE_NOT_FOUND = "Role not found with id: ";

    private final RoleRepository roleRepository;
    private final Validator validator;

    @Override
    public Role createRole(RoleRequest roleRequest) {
        if (roleRepository.findByName(roleRequest.getName()).isPresent())
            throw new DataIntegrityViolationException("Role name already exists");

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
                .orElseThrow(() -> new EntityNotFoundException(ROLE_NOT_FOUND + idRole));
    }

    @Override
    public Role updateRole(Long idRole, RoleRequest roleRequest) {
        Role role = roleRepository.findById(idRole)
                .orElseThrow(() -> new EntityNotFoundException(ROLE_NOT_FOUND + idRole));

        if (!role.getName().equals(roleRequest.getName()) && roleRepository.findByName(roleRequest.getName()).isPresent()) {
            throw new DataIntegrityViolationException("Role name already exists");
        }
        role.setName(roleRequest.getName());
        role.setDescription(roleRequest.getDescription());
        Set<ConstraintViolation<Role>> violations = validator.validate(role);
        if (!violations.isEmpty()) throw new ConstraintViolationException(violations);

        return roleRepository.save(role);
    }

    @Override
    public void deleteRole(Long idRole) {
        if (!roleRepository.existsById(idRole)) {
            throw new EntityNotFoundException(ROLE_NOT_FOUND + idRole);
        }
        roleRepository.deleteById(idRole);
    }

    @Override
    public Role changeStatusRole(Long idRole) {
        Role role = roleRepository.findById(idRole)
                .orElseThrow(() -> new EntityNotFoundException(ROLE_NOT_FOUND + idRole));
        role.setStatus(!role.getStatus());
        return roleRepository.save(role);
    }
}
