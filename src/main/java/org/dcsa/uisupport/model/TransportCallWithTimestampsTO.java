package org.dcsa.uisupport.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dcsa.core.events.model.transferobjects.TransportCallTO;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.OffsetDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Table("transport_call_with_timestamps")
public class TransportCallWithTimestampsTO extends TransportCallTO {

    @Column("latest_event_created_date_time")
    private OffsetDateTime latestEventCreatedDateTime;

    @Column("eta_berth_date_time")
    private OffsetDateTime etaBerthDateTime;

    @Column("atd_berth_date_time")
    private OffsetDateTime atdBerthDateTime;
}
