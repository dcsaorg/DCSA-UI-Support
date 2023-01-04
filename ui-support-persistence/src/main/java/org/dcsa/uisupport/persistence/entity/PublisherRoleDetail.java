package org.dcsa.uisupport.persistence.entity;

import lombok.*;
import org.dcsa.jit.persistence.entity.enums.PublisherRole;

import jakarta.persistence.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter(AccessLevel.PRIVATE)
@Entity
@Table(name = "party_function")
public class PublisherRoleDetail {

  // Technically, this is a PartyFunction - but the repository will handle
  // only pulling out the publisherRole subset.
  @Id
  @Enumerated(EnumType.STRING)
  @Column(name = "party_function_code")
  private PublisherRole publisherRole;

  @Column(name = "party_function_name", nullable = false)
  private String publisherRoleName;
}
