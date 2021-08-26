package org.dcsa.uisupport.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public class PendingEvent {
    @Id
    @Column("delivery_id")
    private UUID deliveryId;

    @Column("subscription_id")
    private UUID subscriptionId;

    @Column("event_id")
    private UUID eventId;

    @Column("payload")
    private String payload;

    @Column("enqueued_at_date_time")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private OffsetDateTime equeuedAtDateTime;

    @Column("last_attempt_date_time")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private OffsetDateTime lastAttemptDateTime;

    @Column("last_error_message")
    private String lastErrorMessage;

    @Column("retry_count")
    private int retryCount;
}
