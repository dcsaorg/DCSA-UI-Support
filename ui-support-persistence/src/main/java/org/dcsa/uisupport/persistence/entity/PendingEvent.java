package org.dcsa.uisupport.persistence.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

/**
 * Only need the id to check if the entry exists
 */
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Setter(AccessLevel.PRIVATE)
@Entity
@Table(name = "unmapped_event_queue")
public class PendingEvent {
  @Id
  @Column(name = "event_id", nullable = false)
  private UUID eventID;
}
