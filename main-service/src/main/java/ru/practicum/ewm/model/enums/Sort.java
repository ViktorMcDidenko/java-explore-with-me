package ru.practicum.ewm.model.enums;

public enum Sort {
    EVENT_DATE, VIEWS;

    public static Sort from(String stringSort) {
        for (Sort sort : values()) {
            if (sort.name().equalsIgnoreCase(stringSort)) {
                return Sort.valueOf(stringSort);
            }
        }
        return null;
    }
}