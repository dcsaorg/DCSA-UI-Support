package org.dcsa.uisupport.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public class UnmappedEvent {
    @Id
    @Column("event_id")
    private UUID eventId;

    @Column("enqueued_at_date_time")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private OffsetDateTime equeuedAtDateTime;
}
