package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.validation.StartBeforeEndDateValidator;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@StartBeforeEndDateValidator
public class BookItemRequestDto {
	@NotNull
	private long itemId;
	@FutureOrPresent
	private LocalDateTime start;
	@Future
	private LocalDateTime end;
}
