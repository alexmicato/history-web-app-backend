package com.example.webapp_backend;

import com.example.webapp_backend.config.util.JwtProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.context.SecurityContextHolder;

@SpringBootApplication
@EnableConfigurationProperties(JwtProperties.class)
public class WebappBackendApplication {

	public static void main(String[] args) {

		SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
		SpringApplication.run(WebappBackendApplication.class, args);
	}

}
