package org.dcsa.uisupport.persistence.entity;

import lombok.*;
import org.dcsa.jit.persistence.entity.TransportCall;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter(AccessLevel.PRIVATE)
@Entity
@Table(name = "jit_port_visit_ui_context")
public class JITPortVisitUIContext {

  @Id
  @Column(name = "port_visit_id")
  private UUID portVisitID;

  @Column(name = "latest_event_created_date_time")
  private OffsetDateTime latestEventCreatedDateTime;

  @Column(name = "best_berth_estimate_date_time")
  private OffsetDateTime bestBerthArrivalEstimateDateTime;

  @Column(name = "atd_berth_date_time")
  private OffsetDateTime atdBerthDateTime;

  @Column(name = "omit_created_date_time")
  private OffsetDateTime omitCreatedDateTime;

  @Column(name = "vessel_draft")
  private Float vesselDraft;

  @Column(name = "miles_to_destination_port")
  private Float milesToDestinationPort;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "port_visit_id")
  @MapsId
  private TransportCall jitPortVisit;
}
