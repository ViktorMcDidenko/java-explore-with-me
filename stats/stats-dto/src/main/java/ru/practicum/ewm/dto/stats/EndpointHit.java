package ru.practicum.ewm.dto.stats;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
//@Jacksonized
//@JsonIgnoreProperties(ignoreUnknown = true)
public class EndpointHit {
   //private Long id;
   private String app;
   private String uri;
   private String ip;
   @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
   private LocalDateTime timestamp;
}