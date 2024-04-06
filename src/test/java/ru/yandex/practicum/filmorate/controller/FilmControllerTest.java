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
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.services.FilmService;
import ru.yandex.practicum.filmorate.services.IdGenerator;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
class FilmControllerTest {
    @Mock
    private FilmService filmService;

    @InjectMocks
    private FilmController filmController;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(filmController).build();
        objectMapper = JsonMapper.builder()
                .addModule(new ParameterNamesModule())
                .addModule(new Jdk8Module())
                .addModule(new JavaTimeModule())
                .build();
    }

    @Test
    void validationCheck() throws Exception {
        Film film = new Film();
        film.setId(IdGenerator.getFilmId());
        film.setName("name");
        film.setDescription("description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(100);

        String filmJson = objectMapper.writeValueAsString(film);

        mockMvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON).content(filmJson))
                .andExpect(status().isOk()); //обычное поведение

        film.setName("");
        filmJson = objectMapper.writeValueAsString(film);
        mockMvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON).content(filmJson))
                .andExpect(status().isBadRequest()); //пустое имя

        film.setName(null);
        filmJson = objectMapper.writeValueAsString(film);
        mockMvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON).content(filmJson))
                .andExpect(status().isBadRequest()); //null имя
        film.setName("name");


        film.setDescription("очень длинное описание: максимальная длина описания — 200 символов;максимальная длина" +
                " описания — 200 символов;максимальная длина описания — 200 символов;максимальная длина описания — " +
                "200 символов;максимальная длина описания — 200 символов;максимальная длина описания — 200 символов;" +
                "максимальная длина описания — 200 символов;максимальная длина описания — 200 символов;");
        filmJson = objectMapper.writeValueAsString(film);
        mockMvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON).content(filmJson))
                .andExpect(status().isBadRequest()); //описание 200+ символов

        film.setDescription("");
        filmJson = objectMapper.writeValueAsString(film);
        mockMvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON).content(filmJson))
                .andExpect(status().isBadRequest()); //отсутствует описание

        film.setDescription(null);
        filmJson = objectMapper.writeValueAsString(film);
        mockMvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON).content(filmJson))
                .andExpect(status().isBadRequest()); //null описание
        film.setDescription("description");

        film.setReleaseDate(LocalDate.of(999, 1, 1));
        filmJson = objectMapper.writeValueAsString(film);
        mockMvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON).content(filmJson))
                .andExpect(status().isBadRequest()); //дата релиза раньше 28.12.1895

        film.setReleaseDate(null);
        filmJson = objectMapper.writeValueAsString(film);
        mockMvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON).content(filmJson))
                .andExpect(status().isBadRequest()); //дата релиза отсутствует
        film.setReleaseDate(LocalDate.of(2000, 1, 1));

        film.setDuration(0);
        filmJson = objectMapper.writeValueAsString(film);
        mockMvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON).content(filmJson))
                .andExpect(status().isBadRequest()); //нулевая продолжительность
        film.setDuration(100);

        film.setDuration(-100);
        filmJson = objectMapper.writeValueAsString(film);
        mockMvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON).content(filmJson))
                .andExpect(status().isBadRequest()); //отрицательная продолжительность
        film.setDuration(100);
    }
}
