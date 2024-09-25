package ru.practicum.shareit.user;

import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.common.Marker;
import ru.practicum.shareit.user.dto.UserRequestDto;

@FieldDefaults(level = AccessLevel.PRIVATE)
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping(path = "/users")
public class UserController {
    final UserClient userClient;

    @GetMapping("/{id}")
    public ResponseEntity<Object> findUserById(@PathVariable @Min(1) Long id) {
        return userClient.getUserById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> create(@Validated({Marker.OnCreate.class}) @RequestBody UserRequestDto newUserRequestDto) {
        return userClient.createUser(newUserRequestDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(@Validated({Marker.OnUpdate.class}) @RequestBody UserRequestDto updUserRequestDto, @PathVariable @Min(1) Long id) {
        return userClient.updateUser(id, updUserRequestDto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable @Min(1) Long id) {
        userClient.deleteUser(id);
    }
}
