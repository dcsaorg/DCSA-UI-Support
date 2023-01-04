package org.dcsa.uisupport.persistence.entity;

import lombok.*;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter(AccessLevel.PRIVATE)
@Entity
@Table(name = "port_call_part")
public class PortCallPart {

  @Id
  @Column(name = "port_call_part", nullable = false)
  String portCallPart;

  @Column(name = "display_order", nullable = false)
  int displayOrder;
}
