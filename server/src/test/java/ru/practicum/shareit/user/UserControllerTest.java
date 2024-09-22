package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserControllerTest {

    private final ObjectMapper mapper;
    private final MockMvc mockMvc;

    @MockBean
    private UserService userService;


    @Test
    @SneakyThrows
    void findUserByIdTest() {
        Long id = 1L;
        String email = "test@test.com";
        String name = "test";
        UserDto userToGetDto = UserDto.builder().id(id).email(email).name(name).build();
        Mockito.when(userService.findUserById(id)).thenReturn(userToGetDto);
        UserDto expectedUserDto = UserDto.builder()
                .id(id)
                .email(email)
                .name(name)
                .build();
        MvcResult mvcResult = mockMvc.perform(get("/users/{id}", id)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = mvcResult.getResponse().getContentAsString();

        UserDto actualUserDto = mapper.readValue(responseBody, UserDto.class);
        Assertions.assertEquals(expectedUserDto, actualUserDto);
    }

    @Test
    @SneakyThrows
    void createTest() {
        Long id = 1L;
        String email = "test@test.com";
        String name = "test";
        UserDto userToSaveDto = UserDto.builder().email(email).name(name).build();
        UserDto expectedUserDto = UserDto.builder().id(id).email(email).name(name).build();
        Mockito.when(userService.create(userToSaveDto)).thenReturn(expectedUserDto);

        MvcResult mvcResult = mockMvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userToSaveDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        String responseBody = mvcResult.getResponse().getContentAsString();
        UserDto actualUserDto = mapper.readValue(responseBody, UserDto.class);
        Assertions.assertNotNull(actualUserDto);
        Assertions.assertEquals(expectedUserDto, actualUserDto);
    }

    @Test
    @SneakyThrows
    void updateTest() {
        Long id = 1L;
        String updEmail = "update@update.com";
        String updName = "update";
        UserDto userToUpdateDto = UserDto.builder().name(updName).email(updEmail).build();
        UserDto expectedUpdateUserDto = UserDto.builder().id(id).email(updEmail).name(updName).build();
        Mockito.when(userService.update(id, userToUpdateDto)).thenReturn(expectedUpdateUserDto);

        MvcResult mvcResult = mockMvc.perform(patch("/users/" + id)
                        .content(mapper.writeValueAsString(userToUpdateDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = mvcResult.getResponse().getContentAsString();
        UserDto actualUpdateUserDto = mapper.readValue(responseBody, UserDto.class);
        Assertions.assertNotNull(actualUpdateUserDto);
        Assertions.assertEquals(expectedUpdateUserDto, actualUpdateUserDto);
        Assertions.assertEquals(expectedUpdateUserDto.getEmail(), actualUpdateUserDto.getEmail());
        Assertions.assertEquals(expectedUpdateUserDto.getName(), actualUpdateUserDto.getName());
    }

    @Test
    @SneakyThrows
    void deleteTest() {
        long id = 1L;
        Mockito.doNothing().when(userService).delete(id);

        mockMvc.perform(delete("/users/" + id)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Mockito.verify(userService, Mockito.times(1)).delete(id);
    }

    @Test
    @SneakyThrows
    void deleteTest_ThrowNotFoundException() {
        long id = 1L;
        Mockito.doThrow(NotFoundException.class).when(userService).delete(id);

        mockMvc.perform(delete("/users/" + id)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

}