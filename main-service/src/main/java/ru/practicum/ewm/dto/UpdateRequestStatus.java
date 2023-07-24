package ru.practicum.ewm.dto;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.ewm.model.Status;

import java.util.List;

@Getter
@Setter
public class UpdateRequestStatus {
    private List<Long> requestIds;
    private String status;
}