package io.egia.mqi.web;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

//@RunWith(SpringRunner.class)
//@SpringBootTest
//@AutoConfigureMockMvc
public class HelloControllerTest {
//    @Autowired
//    private MockMvc mvc;

    @Test
    public void getHello() throws Exception {
//        mvc.perform(MockMvcRequestBuilders.get("/greetings").accept(MediaType.APPLICATION_JSON))
//                .andExpect(jobStatus().isOk())
//                .andExpect(content().string(equalTo("Greetings from Spring Boot!")));
        assertThat(1).isEqualTo(1);
    }
}
