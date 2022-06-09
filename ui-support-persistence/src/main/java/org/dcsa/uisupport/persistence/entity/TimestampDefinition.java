package org.dcsa.uisupport.persistence.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter(AccessLevel.PRIVATE)
@Entity
@Table(name = "timestamp_definition")
public class TimestampDefinition {
  @Id
  @Column(name = "id", nullable = false)
  private String id;

  @Column(name = "timestamp_type_name", nullable = false, unique = true)
  private String typeName;

  @Column(name = "publisher_role", length = 3, nullable = false)
  private String publisherRole;

  @Column(name = "primary_receiver", length = 3, nullable = false)
  private String primaryReceiver;

  @Column(name = "event_classifier_code", length = 3, nullable = false)
  private String eventClassifierCode;

  @Column(name = "operations_event_type_code", length = 4, nullable = false)
  private String operationsEventTypeCode;

  @Column(name = "port_call_phase_type_code", length = 4)
  private String portCallPhaseTypeCode;

  @Column(name = "port_call_service_type_code", length = 4)
  private String portCallServiceTypeCode;

  @Column(name = "facility_type_code", length = 4)
  private String facilityTypeCode;

  @Column(name = "is_berth_location_needed", nullable = false)
  private Boolean isBerthLocationNeeded;

  @Column(name = "is_pbp_location_needed", nullable = false)
  private Boolean isPbpLocationNeeded;

  @Column(name = "is_terminal_needed", nullable = false)
  private Boolean isTerminalNeeded;

  @Column(name = "is_vessel_position_needed", nullable = false)
  private Boolean isVesselPositionNeeded;

  @Column(name = "negotiation_cycle", nullable = false)
  private String negotiationCycle;

  @Column(name = "provided_in_standard", nullable = false)
  private String providedInStandard;

  @Column(name = "accept_timestamp_definition", nullable = false)
  private String acceptTimestampDefinition;

  @Column(name = "reject_timestamp_definition", nullable = false)
  private String rejectTimestampDefinition;
}
