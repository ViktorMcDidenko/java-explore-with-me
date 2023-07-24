package ru.practicum.ewm.dto.stats;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ViewStatsList {
        private List<ViewStats> viewStatsList;

        public ViewStatsList() {
            viewStatsList = new ArrayList<>();
        }
}