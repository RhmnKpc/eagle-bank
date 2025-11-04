package com.eaglebank.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * TestContainers configuration for integration tests
 */
@TestConfiguration(proxyBeanMethods = false)
public class TestContainersConfiguration {

    private static final PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>(
        DockerImageName.parse("postgres:15-alpine"))
        .withDatabaseName("testdb")
        .withUsername("test")
        .withPassword("test")
        .withReuse(true);

    static {
        postgresContainer.start();
    }

    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext context) {
            TestPropertyValues.of(
                // DataSource
                "spring.datasource.url=" + postgresContainer.getJdbcUrl(),
                "spring.datasource.username=" + postgresContainer.getUsername(),
                "spring.datasource.password=" + postgresContainer.getPassword(),
                "spring.datasource.driver-class-name=org.postgresql.Driver",

                // JPA / Hibernate
                "spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect",
                "spring.jpa.hibernate.ddl-auto=create-drop"
            ).applyTo(context.getEnvironment());
        }
    }
}
