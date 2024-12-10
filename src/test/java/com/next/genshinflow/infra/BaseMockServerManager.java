package com.next.genshinflow.infra;

import com.next.genshinflow.util.DesiredResponseLoader;
import java.io.IOException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

@TestInstance(Lifecycle.PER_CLASS)
public abstract class BaseMockServerManager {

    private final WebApplicationContext webApplicationContext;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    protected MockMvc mockMvc;

    protected BaseMockServerManager(
        WebApplicationContext webApplicationContext
    ) {
        this.webApplicationContext = webApplicationContext;
    }


    @BeforeEach
    public void setup() throws IOException {
        DesiredResponseLoader.load();

        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
            .addFilters(new CharacterEncodingFilter("UTF-8", true))
            .alwaysDo(MockMvcResultHandlers.print())
            .build();
    }

    @AfterEach
    public void cleanup() {
        databaseCleaner.execute();
    }
}
