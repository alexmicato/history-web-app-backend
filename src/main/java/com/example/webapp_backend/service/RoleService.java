package com.example.webapp_backend.service;

import com.example.webapp_backend.model.data.Roles;
import com.example.webapp_backend.model.RoleEntity;
import com.example.webapp_backend.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    @Autowired
    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public RoleEntity save(RoleEntity roleEntity) {
        return roleRepository.save(roleEntity);
    }

    public Optional<RoleEntity> findByName(Roles role) {
        return roleRepository.findByName(role.name());
    }

    public boolean existsByName(String name) {
        return roleRepository.existsByName(name);
    }

}
