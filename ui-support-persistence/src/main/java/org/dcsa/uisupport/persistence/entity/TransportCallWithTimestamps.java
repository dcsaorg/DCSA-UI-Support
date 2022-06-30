package org.dcsa.uisupport.persistence.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.dcsa.jit.persistence.entity.TransportCall;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.OffsetDateTime;

@Data
@Builder(builderMethodName = "withTimestampsBuilder")
@NoArgsConstructor
@AllArgsConstructor
@Setter(AccessLevel.PRIVATE)
@Entity
@Table(name = "transport_call_with_timestamps")
public class TransportCallWithTimestamps extends TransportCall {
  @Column(name = "latest_event_created_date_time")
  private OffsetDateTime latestEventCreatedDateTime;

  @Column(name = "eta_berth_date_time")
  private OffsetDateTime etaBerthDateTime;

  @Column(name = "atd_berth_date_time")
  private OffsetDateTime atdBerthDateTime;

  @Column(name = "vessel_draft")
  private Integer vesselDraft;

  @Column(name = "miles_remaining_to_destination")
  private Integer milesRemainingToDestination;
}
