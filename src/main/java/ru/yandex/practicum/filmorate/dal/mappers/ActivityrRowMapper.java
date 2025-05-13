package ru.yandex.practicum.filmorate.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Activity;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class ActivityrRowMapper implements RowMapper<Activity> {
    @Override
    public Activity mapRow(ResultSet rs, int rowNum) throws SQLException {
        Activity activity = new Activity();
        activity.setEventId(rs.getLong("eventId"));
        activity.setUserId(rs.getLong("userId"));
        activity.setEntityId(rs.getLong("entityId"));
        activity.setEventType(rs.getString("eventType"));
        activity.setOperation(rs.getString("operation"));
        activity.setTimestamp(rs.getLong("timestamp"));
        return activity;
    }
}
