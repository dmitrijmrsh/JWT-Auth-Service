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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.JsonNode;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = TestBeans.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureMockMvc
public class UserRestControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Order(1)
    @Test
    public void signUpUserWithValidDataThenLoginThenGetUserInfoReturnsOk() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                        "firstName": "Нурлан",
                        "lastName": "Сабуров",
                        "email": "saburov@gmail.com",
                        "password": "123456"
                    }
                """))
                .andExpect(status().isCreated());

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "email": "saburov@gmail.com",
                        "password": "123456"
                    }
                """))
                .andExpect(status().isOk())
                .andReturn();

        ObjectMapper objectMapper = new ObjectMapper();
        String responseBody = result.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        String bearerToken = jsonNode.get("token").asText();

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/users")
                .header("Authorization", "Bearer " + bearerToken))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("Нурлан"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value("Сабуров"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("saburov@gmail.com"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.privilege").value("PRIVILEGE_STANDARD"));
    }

    @Order(2)
    @Test
    public void loginUserThenUpdateUserDataWithValidRequestReturnsOk() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                        "email": "saburov@gmail.com",
                        "password": "123456"
                    }
                """))
                .andExpect(status().isOk())
                .andReturn();

        ObjectMapper objectMapper = new ObjectMapper();
        String responseBody = result.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        String bearerToken = jsonNode.get("token").asText();

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/users")
                .header("Authorization", "Bearer " + bearerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "firstName": "Александр",
                        "lastName": "Розенбаум",
                        "email": "rozenbaum@gmail.com"
                    }
                """))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("Александр"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value("Розенбаум"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("rozenbaum@gmail.com"));
    }

    @Order(3)
    @Test
    public void loginThenUpdateUserWithInvalidRequestReturnsBadRequest() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                        "email": "rozenbaum@gmail.com",
                        "password": "123456"
                    }
                """))
                .andExpect(status().isOk())
                .andReturn();

        ObjectMapper objectMapper = new ObjectMapper();
        String responseBody = result.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        String bearerToken = jsonNode.get("token").asText();

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/users")
                .header("Authorization", "Bearer " + bearerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "firstName": "Сергей",
                        "lastName": "Жуков",
                        "email": "sergeyzhukov"
                    }
                """))
                .andExpect(status().isBadRequest());
    }

    @Order(4)
    @Test
    public void loginThenDeleteUserReturnsNoContent() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                        "email": "rozenbaum@gmail.com",
                        "password": "123456"
                    }
                """))
                .andExpect(status().isOk())
                .andReturn();

        ObjectMapper objectMapper = new ObjectMapper();
        String responseBody = result.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        String bearerToken = jsonNode.get("token").asText();

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/users")
                .header("Authorization", "Bearer " + bearerToken))
                .andExpect(status().isNoContent());
    }
}
