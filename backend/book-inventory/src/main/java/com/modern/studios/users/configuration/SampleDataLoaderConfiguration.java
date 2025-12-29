package com.modern.studios.users.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.modern.studios.users.dto.register.RegisterUserDTO;
import com.modern.studios.users.repository.UserRepository;
import com.modern.studios.users.service.AuthenticationService;

/**
 * Configuration class for loading sample users on application startup.
 * Uses AuthenticationService to register users, ensuring proper password encoding
 * and validation through the service layer.
 */
@Configuration
public class SampleDataLoaderConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(SampleDataLoaderConfiguration.class);

    @Value("${app.sample-data.enabled:true}")
    private boolean sampleDataEnabled;

    /**
     * Loads sample users on application startup.
     * Only runs if sample data is enabled and users don't already exist.
     *
     * @param authenticationService Service for user registration
     * @param userRepository Repository to check existing users
     * @return CommandLineRunner that loads sample data
     */
    @Bean
    public CommandLineRunner loadSampleUsers(
            AuthenticationService authenticationService,
            UserRepository userRepository) {
        return args -> {
            if (!sampleDataEnabled) {
                logger.info("Sample data loading is disabled");
                return;
            }

            logger.info("Starting to load sample users...");
            
            // Verify database is accessible
            try {
                long existingUserCount = userRepository.count();
                logger.info("Current user count in database: {}", existingUserCount);
            } catch (Exception e) {
                logger.error("Failed to access database. Sample data loading aborted.", e);
                return;
            }

            int usersCreated = 0;
            int usersSkipped = 0;

            // Sample user 1: Admin user
            if (!userRepository.existsByEmail("admin@todolist.com")) {
                try {
                    RegisterUserDTO adminUser = new RegisterUserDTO(
                            "admin@todolist.com",
                            "admin123",
                            "Admin",
                            "User"
                    );
                    authenticationService.signup(adminUser);
                    logger.info("✓ Sample user created: admin@todolist.com");
                    usersCreated++;
                } catch (Exception e) {
                    logger.error("✗ Failed to create admin user: {}", e.getMessage(), e);
                }
            } else {
                logger.info("⊘ Admin user already exists, skipping");
                usersSkipped++;
            }

            // Sample user 2: Regular user
            if (!userRepository.existsByEmail("john.doe@todolist.com")) {
                try {
                    RegisterUserDTO regularUser = new RegisterUserDTO(
                            "john.doe@todolist.com",
                            "password123",
                            "John",
                            "Doe"
                    );
                    authenticationService.signup(regularUser);
                    logger.info("✓ Sample user created: john.doe@todolist.com");
                    usersCreated++;
                } catch (Exception e) {
                    logger.error("✗ Failed to create regular user: {}", e.getMessage(), e);
                }
            } else {
                logger.info("⊘ Regular user already exists, skipping");
                usersSkipped++;
            }

            // Sample user 3: Test user
            if (!userRepository.existsByEmail("test@todolist.com")) {
                try {
                    RegisterUserDTO testUser = new RegisterUserDTO(
                            "test@todolist.com",
                            "test123",
                            "Test",
                            "User"
                    );
                    authenticationService.signup(testUser);
                    logger.info("✓ Sample user created: test@todolist.com");
                    usersCreated++;
                } catch (Exception e) {
                    logger.error("✗ Failed to create test user: {}", e.getMessage(), e);
                }
            } else {
                logger.info("⊘ Test user already exists, skipping");
                usersSkipped++;
            }

            // Sample user 4: Demo user
            if (!userRepository.existsByEmail("demo@todolist.com")) {
                try {
                    RegisterUserDTO demoUser = new RegisterUserDTO(
                            "demo@todolist.com",
                            "demo123",
                            "Demo",
                            "Account"
                    );
                    authenticationService.signup(demoUser);
                    logger.info("✓ Sample user created: demo@todolist.com");
                    usersCreated++;
                } catch (Exception e) {
                    logger.error("✗ Failed to create demo user: {}", e.getMessage(), e);
                }
            } else {
                logger.info("⊘ Demo user already exists, skipping");
                usersSkipped++;
            }

            // Sample user 5: Developer user
            if (!userRepository.existsByEmail("developer@todolist.com")) {
                try {
                    RegisterUserDTO developerUser = new RegisterUserDTO(
                            "developer@todolist.com",
                            "dev123",
                            "Developer",
                            "Account"
                    );
                    authenticationService.signup(developerUser);
                    logger.info("✓ Sample user created: developer@todolist.com");
                    usersCreated++;
                } catch (Exception e) {
                    logger.error("✗ Failed to create developer user: {}", e.getMessage(), e);
                }
            } else {
                logger.info("⊘ Developer user already exists, skipping");
                usersSkipped++;
            }

            logger.info("Sample data loading completed. Created: {}, Skipped: {}, Total users: {}", 
                    usersCreated, usersSkipped, userRepository.count());
        };
    }
}

