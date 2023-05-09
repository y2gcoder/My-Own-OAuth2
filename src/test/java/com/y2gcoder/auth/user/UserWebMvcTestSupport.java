package com.y2gcoder.auth.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.y2gcoder.auth.auth.infra.TokenConfig;
import com.y2gcoder.auth.auth.infra.security.SecurityConfig;
import com.y2gcoder.auth.user.application.SignUpService;
import com.y2gcoder.auth.user.ui.SignUpController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = {
        SignUpController.class
})
@Import({SecurityConfig.class, TokenConfig.class})
public abstract class UserWebMvcTestSupport {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockBean
    protected SignUpService signUpService;
}
