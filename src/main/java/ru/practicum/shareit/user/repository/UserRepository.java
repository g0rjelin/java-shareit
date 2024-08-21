package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserRepository {

    Collection<User> findAll();

    User create(User newUser);

    User update(User updUser);

    void delete(Long id);

    Optional<User> findUserById(Long id);
}
