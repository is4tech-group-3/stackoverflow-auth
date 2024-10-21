package com.stackoverflow.service.role;

import com.stackoverflow.bo.Role;
import com.stackoverflow.dto.role.RoleRequest;
import com.stackoverflow.repository.RoleRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RoleServiceImplTest {

    @InjectMocks
    private RoleServiceImpl roleService;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private jakarta.validation.Validator validator;

    private RoleRequest roleRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        roleRequest = new RoleRequest();
        roleRequest.setName("Admin");
        roleRequest.setDescription("Administrator Role");
    }

    @Test
    void testCreateRoleWhenRoleDoesNotExist() {
        when(roleRepository.findByName(roleRequest.getName())).thenReturn(Optional.empty());
        Role savedRole = Role.builder()
                .idRole(1L)
                .name(roleRequest.getName())
                .description(roleRequest.getDescription())
                .status(true)
                .build();
        when(roleRepository.save(any(Role.class))).thenReturn(savedRole);

        Role result = roleService.createRole(roleRequest);

        assertNotNull(result);
        assertEquals("Admin", result.getName());
        verify(roleRepository).findByName(roleRequest.getName());
        verify(roleRepository).save(any(Role.class));
    }

    @Test
    void testCreateRoleWhenRoleExists() {
        when(roleRepository.findByName(roleRequest.getName())).thenReturn(Optional.of(new Role()));

        assertThrows(DataIntegrityViolationException.class, () -> roleService.createRole(roleRequest));
        verify(roleRepository).findByName(roleRequest.getName());
        verify(roleRepository, never()).save(any(Role.class));
    }

    @Test
    void testGetRoles() {
        List<Role> roles = new ArrayList<>();
        roles.add(new Role());
        when(roleRepository.findAll()).thenReturn(roles);

        List<Role> result = roleService.getRoles();

        assertEquals(1, result.size());
        verify(roleRepository).findAll();
    }

    @Test
    void testFindRoleByIdWhenRoleExists() {
        Role role = new Role();
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));

        Role result = roleService.findRoleById(1L);

        assertNotNull(result);
        verify(roleRepository).findById(1L);
    }

    @Test
    void testFindRoleByIdWhenRoleDoesNotExist() {
        when(roleRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> roleService.findRoleById(1L));
        verify(roleRepository).findById(1L);
    }

    @Test
    void testUpdateRoleWhenRoleExists() {
        Role existingRole = new Role(1L, "Admin", "Admin Role", true);
        when(roleRepository.findById(1L)).thenReturn(Optional.of(existingRole));
        when(roleRepository.findByName("Admin")).thenReturn(Optional.empty());
        when(roleRepository.save(any(Role.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Role result = roleService.updateRole(1L, roleRequest);

        assertEquals("Admin", result.getName());
        verify(roleRepository).save(any(Role.class));
    }

    @Test
    void testUpdateRoleWhenRoleDoesNotExist() {
        when(roleRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> roleService.updateRole(1L, roleRequest));
        verify(roleRepository).findById(1L);
    }

    @Test
    public void testDeleteRoleWhenRoleExists() {
        Long existingRoleId = 1L;

        when(roleRepository.existsById(existingRoleId)).thenReturn(true);

        roleService.deleteRole(existingRoleId);

        verify(roleRepository, times(1)).deleteById(existingRoleId);
    }

    @Test
    void testDeleteRoleWhenRoleDoesNotExist() {
        Long nonExistentRoleId = 1L;

        when(roleRepository.existsById(nonExistentRoleId)).thenReturn(false);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            roleService.deleteRole(nonExistentRoleId);
        });

        assertEquals("Role not found with id: " + nonExistentRoleId, exception.getMessage());

        verify(roleRepository, never()).deleteById(nonExistentRoleId);
    }

    @Test
    void testChangeStatusRoleWhenRoleExists() {
        Role role = new Role(1L, "Admin", "Admin Role", true);
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
        when(roleRepository.save(any(Role.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Role result = roleService.changeStatusRole(1L);

        assertFalse(result.getStatus());
        verify(roleRepository).save(role);
    }

    @Test
    void testChangeStatusRoleWhenRoleDoesNotExist() {
        when(roleRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> roleService.changeStatusRole(1L));
        verify(roleRepository).findById(1L);
    }
}
