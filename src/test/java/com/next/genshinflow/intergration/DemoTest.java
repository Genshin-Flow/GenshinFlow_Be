package com.next.genshinflow.intergration;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.junit5.api.DBRider;
import com.next.genshinflow.domain.repository.DemoRepository;
import com.next.genshinflow.infra.BaseMockServerManager;
import com.next.genshinflow.util.DesiredResponseLoader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.context.WebApplicationContext;

@DisplayName("Demo Test")
@DBRider
@SpringBootTest
class DemoTest extends BaseMockServerManager {

    @Autowired
    private DemoRepository demoRepository;

    public DemoTest(
        WebApplicationContext webApplicationContext
    ) {
        super(webApplicationContext);
    }

    @DisplayName("데모데모")
    @Test
    @DataSet(value = "dataset/demoController/default.yml")
    void demo() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
            .get("/demo")
            .contentType(MediaType.APPLICATION_JSON);

        String desiredResponse = DesiredResponseLoader.getByName("demo");

        mockMvc.perform(requestBuilder)
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().json(desiredResponse));

        Assertions.assertEquals(3L, demoRepository.count());
    }
}
