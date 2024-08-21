package ru.practicum.shareit.user.repository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.Map;
import java.util.Optional;
import java.util.Collection;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {
    final Map<Long, User> users;

    @Override
    public Collection<User> findAll() {
        log.debug("Список пользователей для вывода: {}", users.values());
        return users.values();
    }

    @Override
    public User create(User newUser) {
        newUser.setId(getNextId());
        users.put(newUser.getId(), newUser);
        log.info("Пользователь {} добавлен", newUser);
        return newUser;
    }

    @Override
    public User update(User updUser) {
        users.put(updUser.getId(), updUser);
        log.info("Пользователь {} обновлен", updUser);
        return updUser;
    }

    @Override
    public void delete(Long id) {
        User delUser = users.remove(id);
        log.info("Пользователь с {} удален", delUser);
    }

    @Override
    public Optional<User> findUserById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }


}
