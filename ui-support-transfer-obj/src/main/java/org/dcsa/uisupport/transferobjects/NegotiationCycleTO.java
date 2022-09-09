package org.dcsa.uisupport.transferobjects;

public record NegotiationCycleTO(
    String cycleKey,
    String cycleName,
    int displayOrder
) {
}
