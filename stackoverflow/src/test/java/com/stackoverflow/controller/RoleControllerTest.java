package com.stackoverflow.controller;

import com.stackoverflow.bo.Role;
import com.stackoverflow.dto.role.RoleRequest;
import com.stackoverflow.service.role.RoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RoleControllerTest {

    @InjectMocks
    private RoleController roleController;

    @Mock
    private RoleService roleService;

    private RoleRequest roleRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        roleRequest = new RoleRequest();
        roleRequest.setName("Admin");
        roleRequest.setDescription("Administrator Role");
    }

    @Test
    void testCreateRole() {
        Role role = new Role(1L, "Admin", "Administrator Role", true);
        when(roleService.createRole(roleRequest)).thenReturn(role);

        ResponseEntity<Role> response = roleController.createRole(roleRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Admin", response.getBody().getName());
        verify(roleService).createRole(roleRequest);
    }

    @Test
    void testGetRoles() {
        List<Role> roles = new ArrayList<>();
        roles.add(new Role());
        when(roleService.getRoles()).thenReturn(roles);

        ResponseEntity<List<Role>> response = roleController.getRoles();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(roleService).getRoles();
    }

    @Test
    void testFindRoleById() {
        Role role = new Role(1L, "Admin", "Administrator Role", true);
        when(roleService.findRoleById(1L)).thenReturn(role);

        ResponseEntity<Role> response = roleController.findRoleById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Admin", response.getBody().getName());
        verify(roleService).findRoleById(1L);
    }

    @Test
    void testUpdateRole() {
        Role role = new Role(1L, "Admin", "Administrator Role", true);
        when(roleService.updateRole(1L, roleRequest)).thenReturn(role);

        ResponseEntity<Role> response = roleController.updateRole(1L, roleRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Admin", response.getBody().getName());
        verify(roleService).updateRole(1L, roleRequest);
    }

    @Test
    void testDeleteRole() {
        ResponseEntity<String> response = roleController.deleteRole(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Role deleted successfully", response.getBody());
        verify(roleService).deleteRole(1L);
    }

    @Test
    void testChangeStatusRole() {
        Role role = new Role(1L, "Admin", "Administrator Role", true);
        when(roleService.changeStatusRole(1L)).thenReturn(role);

        ResponseEntity<Role> response = roleController.changeStatusRole(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Admin", response.getBody().getName());
        verify(roleService).changeStatusRole(1L);
    }
}
