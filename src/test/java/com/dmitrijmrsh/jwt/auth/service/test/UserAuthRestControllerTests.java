package com.dmitrijmrsh.jwt.auth.service.test;

import com.dmitrijmrsh.jwt.auth.service.test.config.TestBeans;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = TestBeans.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureMockMvc
public class UserAuthRestControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Order(1)
    @Test
    public void signUpWithValidRequestReturnsValidData() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "firstName": "Johny",
                        "lastName": "Dang",
                        "email": "example@gmail.com",
                        "password": "123456"
                    }
                """))
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("Johny"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value("Dang"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("example@gmail.com"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.privilege").value("PRIVILEGE_STANDARD"));
    }

    @Order(2)
    @Test
    public void loginWithValidRequestReturnsValidData() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "email": "example@gmail.com",
                        "password": "123456"
                    }
                """)).
                andExpect(status().isOk());
    }

    @Order(3)
    @Test
    public void signupUserWithEmailThatAlreadyExistsReturnsUnprocessableEntity() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                        "firstName": "Антон",
                        "lastName": "Иванов",
                        "email": "example@gmail.com",
                        "password": "332231212"
                    }
                """))
                .andExpect(status().isUnprocessableEntity());
    }

    @Order(4)
    @Test
    public void signUpUserWithInvalidRequestReturnsBadRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "firstName": "Игорь",
                        "lastName": "         ",
                        "email": "dhbvshdb",
                        "password": "123"
                    }
                """))
                .andExpect(status().isBadRequest());
    }
}
