package ru.practicum.ewm.model;

import java.util.Optional;

public enum Status {
    PENDING, CANCELED, CONFIRMED, REJECTED;

    public static Optional<Status> from(String stringStatus) {
        for (Status status : values()) {
            if (status.name().equalsIgnoreCase(stringStatus)) {
                return Optional.of(status);
            }
        }
        return Optional.empty();
    }
}