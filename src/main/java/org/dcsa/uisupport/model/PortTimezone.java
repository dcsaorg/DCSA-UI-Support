package org.dcsa.uisupport.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("port_timezone")
public class PortTimezone {

    @Id
    @Column("un_location_code")
    private String unLocationCode;

    @Column("iana_timezone")
    private String ianaTimezone;
}
