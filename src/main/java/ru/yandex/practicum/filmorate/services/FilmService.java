package ru.yandex.practicum.filmorate.services;

import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


@RequiredArgsConstructor
public class FilmService {
    private HashMap<Integer, Film> films = new HashMap<>();

    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }

    public Film addFilm(Film film) {
        film.setId(IdGenerator.getId());
        films.put(film.getId(),film);
        return film;
    }

    public Film updateFilm(Film film){
        if(films.containsKey(film.getId())){
            films.replace(film.getId(),film);
        } else {
            throw new NotFoundException("Запрашиваемый к обновлению фильм не найден.");
        }
        return films.get(film.getId());
    }
}
