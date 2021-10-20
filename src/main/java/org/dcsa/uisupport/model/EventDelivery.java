package org.dcsa.uisupport.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.dcsa.uisupport.model.enums.EventDeliveryStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.OffsetDateTime;
import java.util.UUID;

@Table("event_delivery_status")
@Data
public class EventDelivery {

    @Id
    @Column("event_id")
    private UUID eventID;

    @Column("event_delivery_status")
    private EventDeliveryStatus eventDeliveryStatus;

    @Column("enqueued_at_date_time")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private OffsetDateTime enqueuedAtDateTime;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Column("last_attempt_date_time")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private OffsetDateTime lastAttemptDateTime;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Column("last_error_message")
    private String lastErrorMessage;

    @Column("retry_count")
    private int retryCount;

    @Column("transport_call_id")
    private String transportCallID;
}
