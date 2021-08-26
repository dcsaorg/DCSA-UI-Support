package org.dcsa.uisupport.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.relational.core.mapping.Column;

import java.time.OffsetDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
public class AttemptedEventDelivery extends EventDelivery {

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
