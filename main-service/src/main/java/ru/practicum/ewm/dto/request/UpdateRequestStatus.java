package ru.practicum.ewm.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UpdateRequestStatus {
    private List<Long> requestIds;
    private String status;
}