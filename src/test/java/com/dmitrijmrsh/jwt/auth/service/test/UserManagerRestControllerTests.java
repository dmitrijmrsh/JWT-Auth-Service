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
import org.testcontainers.shaded.com.fasterxml.jackson.databind.JsonNode;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = TestBeans.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureMockMvc
public class UserManagerRestControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Order(1)
    @Test
    public void superAdminFunctionalityTests() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                        "firstName": "Валерий",
                        "lastName": "Супер-админ",
                        "email": "application-admin@example.com",
                        "password": "admin-password"
                    }
                """))
                .andExpect(status().isCreated())
                .andReturn();

        ObjectMapper objectMapper = new ObjectMapper();
        String responseBody = result.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        Integer superAdminId = jsonNode.get("id").asInt();

        result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                        "firstName": "Первый",
                        "lastName": "Пользователь",
                        "email": "first-user@gmail.com",
                        "password": "firstuserpassword"
                    }
                """))
                .andExpect(status().isCreated())
                .andReturn();

        responseBody = result.getResponse().getContentAsString();
        jsonNode = objectMapper.readTree(responseBody);
        Integer firstUserId = jsonNode.get("id").asInt();

        result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "email": "application-admin@example.com",
                        "password": "admin-password"
                    }
                """))
                .andExpect(status().isOk())
                .andReturn();

        responseBody = result.getResponse().getContentAsString();
        jsonNode = objectMapper.readTree(responseBody);
        String bearerTokenForSuperAdmin = jsonNode.get("token").asText();

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/manager/update/role/{id}", firstUserId)
                .header("Authorization", "Bearer " + bearerTokenForSuperAdmin)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "role": ""
                    }
                """))
                .andExpect(status().isBadRequest());

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/manager/update/role/{id}", firstUserId)
                .header("Authorization", "Bearer " + bearerTokenForSuperAdmin)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "role": "ROLE_MANAGER"
                    }
                """))
                .andExpect(status().isNoContent());

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/manager/update/privilege/{id}", firstUserId)
                .header("Authorization", "Bearer " + bearerTokenForSuperAdmin)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "privilege": "PRIVILEGE_HIGH"
                    }
                """))
                .andExpect(status().isNoContent());

        result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "email": "first-user@gmail.com",
                        "password": "firstuserpassword"
                    }
                """))
                .andExpect(status().isOk())
                .andReturn();

        responseBody = result.getResponse().getContentAsString();
        jsonNode = objectMapper.readTree(responseBody);
        String bearerTokenForManager = jsonNode.get("token").asText();

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/manager/update/privilege/{id}", superAdminId)
                        .header("Authorization", "Bearer " + bearerTokenForManager)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                        "privilege": "PRIVILEGE_STANDARD"
                    }
                """))
                .andExpect(status().isUnprocessableEntity());
    }

    @Order(2)
    @Test
    public void loginManagerThenSignUpUserThenDeleteUserByManagerReturnsNoContent() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                        "email": "first-user@gmail.com",
                        "password": "firstuserpassword"
                    }
                """))
                .andExpect(status().isOk())
                .andReturn();

        ObjectMapper objectMapper = new ObjectMapper();
        String responseBody = result.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        String bearerToken = jsonNode.get("token").asText();

        result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                        "firstName": "Второй",
                        "lastName": "Пользователь",
                        "email": "second-user@gmail.com",
                        "password": "seconduserpassword"
                    }
                """))
                .andExpect(status().isCreated())
                .andReturn();

        responseBody = result.getResponse().getContentAsString();
        jsonNode = objectMapper.readTree(responseBody);
        Integer secondUserId = jsonNode.get("id").asInt();

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/manager/delete/{id}", secondUserId)
                .header("Authorization", "Bearer " + bearerToken))
                .andExpect(status().isNoContent());
    }
}
