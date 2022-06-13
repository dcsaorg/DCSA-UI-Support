package org.dcsa.uisupport.persistence.entity;

import lombok.*;
import org.dcsa.jit.persistence.entity.OperationsEvent;
import org.dcsa.jit.persistence.entity.TimestampDefinition;

import javax.persistence.*;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter(AccessLevel.PRIVATE)
@Entity
@Table(name = "ops_event_timestamp_definition")
public class OpsEventTimestampDefinition {

  @Id
  private UUID eventID;

  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "event_id")
  @MapsId
  OperationsEvent operationsEvent;


  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "timestamp_definition")
  TimestampDefinition timestampDefinition;

  // Declare it so there are no surprises, but we do not need it.
  @Column(name = "payload_id")
  private UUID payloadID;

}
