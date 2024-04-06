package ru.yandex.practicum.filmorate.model;



import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
public class User {

    private Integer id;
    @NotEmpty
    @Email(message =  "Некорректный формат email.")
    private String email;
    @NotBlank(message = "Логин должен содержать символы.")
    @Pattern(regexp = "\\S*", message = "Логин не должен содержать пробелы")
    private String login;
    private String name;
    @NotNull
    @PastOrPresent(message = "Дата рождения не соответствует формату даты.")
    private LocalDate birthday;
}
