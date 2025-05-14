package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Activity {
    private long eventId;
    @NotNull
    private long userId;
    @NotNull
    private long entityId;
    @NotNull
    private String eventType;
    @NotNull
    private String operation;
    @NotNull
    private long timestamp;
}
