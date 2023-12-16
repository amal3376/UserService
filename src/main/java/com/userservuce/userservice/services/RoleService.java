package com.userservuce.userservice.services;

import com.userservuce.userservice.models.Role;
import com.userservuce.userservice.repositories.RoleRepository;
import org.springframework.stereotype.Service;

@Service
public class RoleService {
    private RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository){
        this.roleRepository =roleRepository;
    }
    public Role createRole(String name){
        Role role = new Role();
        role.setRole(name);

        return roleRepository.save(role);
    }
}
