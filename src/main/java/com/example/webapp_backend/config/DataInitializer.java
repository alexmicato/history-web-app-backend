package com.example.webapp_backend.config;

import com.example.webapp_backend.model.UserEntity;
import com.example.webapp_backend.model.data.Roles;
import com.example.webapp_backend.model.data.PostCategories;
import com.example.webapp_backend.model.PostCategoryEntity;
import com.example.webapp_backend.model.RoleEntity;
import com.example.webapp_backend.service.PostCategoryService;
import com.example.webapp_backend.service.RoleService;
import com.example.webapp_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class DataInitializer implements ApplicationRunner {
    @Autowired
    private UserService userService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private PostCategoryService postCategoryService;

    @Value("${admin.username}")
    private String adminUsername;
    @Value("${admin.email}")
    private String adminEmail;
    @Value("${admin.password}")
    private String adminPassword;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        for (Roles value : Roles.values()) {
            String roleName = value.name();
            if (!roleService.existsByName(roleName)) {
                RoleEntity roleEntity = new RoleEntity();
                roleEntity.setName(roleName);
                roleService.save(roleEntity);
            }
        }

        for(PostCategories value : PostCategories.values())
        {
            String categoryName = value.name();
            if (!postCategoryService.existsByName(categoryName)) {
                PostCategoryEntity postCategoryEntity = new PostCategoryEntity();
                postCategoryEntity.setName(categoryName);
                postCategoryService.save(postCategoryEntity);
            }
        }

        initializeAdmin();

    }

    private void initializeAdmin() {
        if (!userService.existsByUsername(adminUsername)) {
           userService.registerAdmin(adminUsername, adminEmail, adminPassword);
        }
    }
}