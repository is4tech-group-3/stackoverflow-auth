package com.stackoverflow.controller;

import com.stackoverflow.bo.Role;
import com.stackoverflow.dto.role.RoleRequest;
import com.stackoverflow.service.role.RoleService;
import com.stackoverflow.util.AuditAnnotation;
import com.stackoverflow.util.LoggerUtil;
import lombok.AllArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/role")
public class RoleController {

    private final String ENTITY_NAME = "ROLE";

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
        List<Role> role = roleService.getAllRoles();
        LoggerUtil.loggerDebug("Reques body en controlador getRoles");
        return new ResponseEntity<>(role, HttpStatus.OK);
    }

    @AuditAnnotation(ENTITY_NAME)
    @GetMapping("/{id}")
    public ResponseEntity<Role> getRole(@PathVariable(name = "id") Long id) {
        Optional<Role> roleFound = roleService.findRoleById(id);
        return new ResponseEntity<>(roleFound.get(), HttpStatus.OK);
    }

    @AuditAnnotation(ENTITY_NAME)
    @PutMapping("/{id}")
    public ResponseEntity<Role> updateRole(@PathVariable(name = "id") Long id, @RequestBody RoleRequest roleRequest) {
        Role role = roleService.updateRole(id, roleRequest);
        return new ResponseEntity<>(role, HttpStatus.OK);
    }

    @AuditAnnotation(ENTITY_NAME)
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRole(@PathVariable(name = "id") Long id) {
        roleService.deleteRole(id);
        return new ResponseEntity<>("Role deleted successfully", HttpStatus.OK);
    }

    @AuditAnnotation(ENTITY_NAME)
    @PatchMapping("/{id}")
    public ResponseEntity<Role> patchRole(@PathVariable(name = "id") Long id) {
        Role role = roleService.updateStatusRole(id);
        return new ResponseEntity<>(role, HttpStatus.OK);
    }
}
