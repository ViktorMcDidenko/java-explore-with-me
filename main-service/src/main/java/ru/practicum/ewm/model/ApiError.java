package ru.practicum.ewm.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ApiError {
    private StackTraceElement[] stacktrace;
    private String message;
    private String reason;
    private String status;
    private String timestamp;
}