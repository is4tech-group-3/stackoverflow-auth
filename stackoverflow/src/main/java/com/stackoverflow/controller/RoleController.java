package com.stackoverflow.controller;

import com.stackoverflow.bo.Role;
import com.stackoverflow.dto.role.RoleRequest;
import com.stackoverflow.service.role.RoleService;
import com.stackoverflow.util.AuditAnnotation;
import lombok.AllArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/role")
public class RoleController {

    private static final String ENTITY_NAME = "ROLE";

    private final RoleService roleService;

    @AuditAnnotation(ENTITY_NAME)
    @PostMapping
    public ResponseEntity<Role> createRole(@RequestBody RoleRequest roleRequest) {
        Role role = roleService.createRole(roleRequest);
        return new ResponseEntity<>(role, HttpStatus.OK);
    }

    @AuditAnnotation(ENTITY_NAME)
    @GetMapping
    public ResponseEntity<List<Role>> getRoles() {
        List<Role> role = roleService.getRoles();
        return new ResponseEntity<>(role, HttpStatus.OK);
    }

    @AuditAnnotation(ENTITY_NAME)
    @GetMapping("/{id}")
    public ResponseEntity<Role> findRoleById(@PathVariable(name = "id") Long idRole) {
        Role role = roleService.findRoleById(idRole);
        return new ResponseEntity<>(role, HttpStatus.OK);
    }

    @AuditAnnotation(ENTITY_NAME)
    @PutMapping("/{id}")
    public ResponseEntity<Role> updateRole(@PathVariable(name = "id") Long idRole, @RequestBody RoleRequest roleRequest) {
        Role role = roleService.updateRole(idRole, roleRequest);
        return new ResponseEntity<>(role, HttpStatus.OK);
    }

    @AuditAnnotation(ENTITY_NAME)
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRole(@PathVariable(name = "id") Long idRole) {
        roleService.deleteRole(idRole);
        return new ResponseEntity<>("Role deleted successfully", HttpStatus.OK);
    }

    @AuditAnnotation(ENTITY_NAME)
    @PatchMapping("/{id}")
    public ResponseEntity<Role> changeStatusRole(@PathVariable(name = "id") Long idRole) {
        Role role = roleService.changeStatusRole(idRole);
        return new ResponseEntity<>(role, HttpStatus.OK);
    }
}
