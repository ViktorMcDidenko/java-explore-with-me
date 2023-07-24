package ru.practicum.ewm.dto;

import lombok.Value;

import javax.validation.constraints.NotNull;

@Value
public class UserShortDto {
    @NotNull
    long id;
    @NotNull
    String name;
}