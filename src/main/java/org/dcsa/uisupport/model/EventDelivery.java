package org.dcsa.uisupport.model;

import lombok.Data;
import org.dcsa.uisupport.model.enums.EventDeliveryStatus;

@Data
public class EventDelivery {

    private EventDeliveryStatus eventDeliveryStatus;
}
