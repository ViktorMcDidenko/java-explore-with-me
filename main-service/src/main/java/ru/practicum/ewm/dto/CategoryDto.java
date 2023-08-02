package ru.practicum.ewm.dto;

import lombok.Value;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Value
public class CategoryDto {
    long id;
    @NotBlank
    @Size(min = 1, max = 50)
    String name;
}