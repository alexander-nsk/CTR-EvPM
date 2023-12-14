package com.allmagen.testtask;

import com.allmagen.testtask.model.ActionEntity;
import com.allmagen.testtask.model.ViewEntity;
import com.allmagen.testtask.repository.ActionRepository;
import com.allmagen.testtask.repository.ViewRepository;
import jakarta.transaction.Transactional;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.PostgreSQLContainer;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestTaskApplication.class)
@ActiveProfiles("tc")
@ContextConfiguration(initializers = {CommonRepositoryTest.Initializer.class})
public class CommonRepositoryTest {
    @Autowired
    protected ActionRepository actionRepository;
    @Autowired
    private ViewRepository viewRepository;
    @ClassRule
    public static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:15-alpine3.17")
            .withDatabaseName("mydb")
            .withUsername("myuser")
            .withPassword("mypass");

    @Test
    @Transactional
    public void givenViewsInDB_WhenCheckViewsNumber() {
        viewRepository.save(new ViewEntity("1"));
        viewRepository.save(new ViewEntity("2"));

        long count = viewRepository.count();

        assertThat(count).isEqualTo(2);
    }

    @Test
    @Transactional
    public void givenActionsInDB_WhenCheckActionsNumber() {
        viewRepository.save(new ViewEntity("1"));
        viewRepository.save(new ViewEntity("2"));

        actionRepository.save(new ActionEntity(1L, new ViewEntity("1"), "11"));
        actionRepository.save(new ActionEntity(2L, new ViewEntity("2"), "22"));

        long count = actionRepository.count();

        assertThat(count).isEqualTo(2);
    }

    static class Initializer
            implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                    "spring.datasource.url=" + postgreSQLContainer.getJdbcUrl(),
                    "spring.datasource.username=" + postgreSQLContainer.getUsername(),
                    "spring.datasource.password=" + postgreSQLContainer.getPassword()
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }
}
