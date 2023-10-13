package org.wallet.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import lombok.Data;
import org.wallet.log.LogAction;

@Data
public class Log {
    private final LocalDateTime timestamp;
    private final LogAction action;
    private final String username;
    private final String details;

    public Log(LogAction action, String username, String details) {
        timestamp = LocalDateTime.now();
        this.action = action;
        this.username = username;
        this.details = details;
    }

    public String toString() {
        return timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                + " " + action + ": " + "User " + username + " " + details+"\n";
    }
}
