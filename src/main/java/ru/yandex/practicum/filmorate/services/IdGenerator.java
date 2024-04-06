package ru.yandex.practicum.filmorate.services;

public class IdGenerator {
    private static int filmId = 1;
    private static int userId = 1;

    public static int getFilmId() {
        return filmId++;
    }

    public static int getUserId() {
        return userId++;
    }
}

