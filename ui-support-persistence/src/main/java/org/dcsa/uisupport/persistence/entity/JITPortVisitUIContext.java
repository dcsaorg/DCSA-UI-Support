package org.dcsa.uisupport.persistence.entity;

import lombok.*;
import org.dcsa.jit.persistence.entity.TransportCall;

import javax.persistence.*;
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

  @Column(name = "eta_berth_date_time")
  private OffsetDateTime etaBerthDateTime;

  @Column(name = "atd_berth_date_time")
  private OffsetDateTime atdBerthDateTime;

  @Column(name = "vessel_draft")
  private Float vesselDraft;

  @Column(name = "miles_remaining_to_destination")
  private Float milesRemainingToDestination;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "port_visit_id")
  @MapsId
  private TransportCall jitPortVisit;
}
