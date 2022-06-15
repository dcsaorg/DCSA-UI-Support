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
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.SecondaryTable;
import javax.persistence.Table;

/**
 * Joins un_location and port_timezone.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter(AccessLevel.PRIVATE)
@Entity
@Table(name = "un_location")
@SecondaryTable(name = "port_timezone", pkJoinColumns = @PrimaryKeyJoinColumn(name = "un_location_code"))
public class PortWithTimezone {
  @Id
  @Column(name = "un_location_code", length = 5, nullable = false, columnDefinition = "bpchar")
  private String UNLocationCode;

  @Column(name = "un_location_name", length = 100)
  private String UNLocationName;

  @Column(name = "location_code", length = 3, columnDefinition = "bpchar")
  private String locationCode;

  @Column(name = "country_code", length = 2, columnDefinition = "bpchar")
  private String countryCode;

  @Column(name = "iana_timezone", table = "port_timezone")
  private String ianaTimezone;
}
