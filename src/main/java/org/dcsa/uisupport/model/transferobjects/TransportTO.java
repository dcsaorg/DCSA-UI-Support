package org.dcsa.uisupport.model.transferobjects;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dcsa.core.events.model.Transport;
import org.dcsa.core.events.model.Vessel;
import org.dcsa.core.model.ForeignKey;
import org.springframework.data.annotation.Transient;

// Unofficial Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class TransportTO extends Transport {

  @Transient
  @ForeignKey(
      fromFieldName = "loadTransportCallID",
      foreignFieldName = "transportCallID",
      viaJoinAlias = "ltc")
  private ShallowTransportCallTO loadTransportCall;

  @Transient
  @ForeignKey(
      fromFieldName = "dischargeTransportCallID",
      foreignFieldName = "transportCallID",
      viaJoinAlias = "dtc")
  private ShallowTransportCallTO dischargeTransportCall;

  @Transient
  @ForeignKey(fromFieldName = "vesselIMONumber", foreignFieldName = "vesselIMONumber")
  private Vessel vessel;

}
