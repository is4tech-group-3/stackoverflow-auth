package com.stackoverflow.controller;

import com.stackoverflow.bo.Role;
import com.stackoverflow.dto.role.RoleRequest;
import com.stackoverflow.service.role.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/role")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @PostMapping
    public ResponseEntity<Role> createRole(@RequestBody RoleRequest roleRequest) {
        Role role = roleService.createRole(roleRequest);
        return new ResponseEntity<>(role, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<Role>> getRoles() {
        List<Role> role = roleService.getAllRoles();
        return new ResponseEntity<>(role, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Role> getRole(@PathVariable(name = "id") Long id) {
        Optional<Role> roleFound = roleService.findRoleById(id);
        return new ResponseEntity<>(roleFound.get(), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Role> updateRole(@PathVariable(name = "id") Long id, @RequestBody RoleRequest roleRequest) {
        Role role = roleService.updateRole(id, roleRequest);
        return new ResponseEntity<>(role, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRole(@PathVariable(name = "id") Long id) {
        roleService.deleteRole(id);
        return new ResponseEntity<>("Role deleted successfully", HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Role> patchRole(@PathVariable(name = "id") Long id) {
        Role role = roleService.updateStatusRole(id);
        return new ResponseEntity<>(role, HttpStatus.OK);
    }
}
