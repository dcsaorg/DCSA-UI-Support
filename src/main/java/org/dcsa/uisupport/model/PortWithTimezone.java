package org.dcsa.uisupport.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.dcsa.core.model.ForeignKey;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.sql.Join;

@Data
@Table("un_location")
public class PortWithTimezone {

    @Id
    @Column("un_location_code")
    private String unLocationCode;

    @Column("un_location_name")
    private String unLocationName;

    @Column("location_code")
    private String locationCode;

    @Column("country_code")
    private String countryCode;

    public String getTimezone() {
        if (portTimezone == null) {
            return null;
        }
        return portTimezone.getIanaTimezone();
    }

    @JsonIgnore
    @Transient
    @ForeignKey(fromFieldName = "unLocationCode", foreignFieldName = "unLocationCode", joinType = Join.JoinType.LEFT_OUTER_JOIN)
    private PortTimezone portTimezone;

}
