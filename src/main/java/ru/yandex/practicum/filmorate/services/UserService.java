package ru.yandex.practicum.filmorate.services;

import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RequiredArgsConstructor
public class UserService {
    private HashMap<Integer, User> users = new HashMap<>();

    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    public User addUser(User user) {
        user.setId(IdGenerator.getId());
        try {
            if (user.getName().isEmpty()) {
                user.setName(user.getLogin());
            }
        } catch (NullPointerException e) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        return user;
    }

    public User updateUser(User user) {
        if (users.containsKey(user.getId())) {
            users.replace(user.getId(), user);
        } else {
            throw new NotFoundException("Запрашиваемый к обновлению пользователь не найден.");
        }
        return users.get(user.getId());
    }
}
