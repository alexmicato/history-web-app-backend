package com.example.webapp_backend.unitTests.serviceTests;
import com.example.webapp_backend.model.data.Roles;
import com.example.webapp_backend.model.RoleEntity;
import com.example.webapp_backend.repository.RoleRepository;
import com.example.webapp_backend.service.RoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleService roleService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void saveRole_Success() {
        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setName(Roles.ADMIN.name());
        when(roleRepository.save(any(RoleEntity.class))).thenReturn(roleEntity);

        RoleEntity savedRole = roleService.save(roleEntity);
        assertEquals(Roles.ADMIN.name(), savedRole.getName());
        verify(roleRepository).save(roleEntity);
    }

    @Test
    void findByName_ExistingRole() {
        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setName(Roles.USER.name());
        when(roleRepository.findByName(Roles.USER.name())).thenReturn(Optional.of(roleEntity));

        Optional<RoleEntity> foundRole = roleService.findByName(Roles.USER);
        assertTrue(foundRole.isPresent());
        assertEquals(Roles.USER.name(), foundRole.get().getName());
    }

    @Test
    void existsByName_ExistingName() {
        String roleName = "ADMIN";
        when(roleRepository.existsByName(roleName)).thenReturn(true);

        boolean exists = roleService.existsByName(roleName);
        assertTrue(exists);
        verify(roleRepository).existsByName(roleName);
    }
}
