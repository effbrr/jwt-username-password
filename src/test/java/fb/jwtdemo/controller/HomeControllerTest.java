package fb.jwtdemo.controller;

import fb.jwtdemo.config.SecurityConfig;
import fb.jwtdemo.service.TokenService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({HomeController.class, AuthController.class})
@Import({SecurityConfig.class, TokenService.class})
class HomeControllerTest {

    @Autowired
    MockMvc mvc;

    @Test
    void rootWhenUnauthenticated_then401() throws Exception {
        this.mvc.perform(get("/")).andExpect(status().isUnauthorized());
    }

    @Test
    void rootWhenAuthenticated_thenSaysHelloUser() throws Exception {
        final MvcResult result = this.mvc.perform(post("/token")
                                         .with(httpBasic("fredrik", "password")))
                                         .andExpect(status().isOk())
                                         .andReturn();
        final String token = result.getResponse().getContentAsString();

        this.mvc.perform(get("/")
                .header("Authorization", "Bearer " + token))
                .andExpect(content().string("Hello, Fredrik"));
    }

    @Test
    @WithMockUser
    public void rootWithMockUser_statusIsOK() throws Exception {
        this.mvc.perform(get("/")).andExpect(status().isOk());
    }
}