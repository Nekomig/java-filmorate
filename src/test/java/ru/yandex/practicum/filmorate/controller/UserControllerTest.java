package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.services.IdGenerator;
import ru.yandex.practicum.filmorate.services.UserService;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        objectMapper = JsonMapper.builder()
                .addModule(new ParameterNamesModule())
                .addModule(new Jdk8Module())
                .addModule(new JavaTimeModule())
                .build();
    }

    @Test
    void validationCheck() throws Exception {
        User user = new User();
        user.setId(IdGenerator.getId());
        user.setEmail("email@ya.ru");
        user.setLogin("login");
        user.setName("name");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        String userJson = objectMapper.writeValueAsString(user);

        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(userJson))
                .andExpect(status().isOk()); //обычное поведение

        user.setEmail("");
        userJson = objectMapper.writeValueAsString(user);
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(userJson))
                .andExpect(status().isBadRequest()); //пустой email

        user.setEmail("emailWithoutArobase");
        userJson = objectMapper.writeValueAsString(user);
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(userJson))
                .andExpect(status().isBadRequest()); //не соблюдается формат email
        user.setEmail("email@ya.ru");

        user.setLogin("");
        userJson = objectMapper.writeValueAsString(user);
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(userJson))
                .andExpect(status().isBadRequest()); //пустой логин

        user.setLogin("login with spaces");
        userJson = objectMapper.writeValueAsString(user);
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(userJson))
                .andExpect(status().isBadRequest()); //логин с пробелами
        user.setLogin("login");

        user.setName("");
        userJson = objectMapper.writeValueAsString(user);
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(userJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("login")); //пустое имя

        user.setName(null);
        userJson = objectMapper.writeValueAsString(user);
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(userJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("login")); //null имя

        user.setBirthday(null);
        userJson = objectMapper.writeValueAsString(user);
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(userJson))
                .andExpect(status().isBadRequest()); //дата рождения null

        user.setBirthday(LocalDate.of(2049, 1, 1));
        userJson = objectMapper.writeValueAsString(user);
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(userJson))
                .andExpect(status().isBadRequest()); //дата рождения в будущем


    }
}
