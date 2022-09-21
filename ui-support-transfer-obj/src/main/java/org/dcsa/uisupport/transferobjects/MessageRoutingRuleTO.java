package org.dcsa.uisupport.transferobjects;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import org.dcsa.jit.transferobjects.enums.PublisherRole;
import org.dcsa.skernel.infrastructure.validation.ValidVesselIMONumber;

import java.util.UUID;

public record MessageRoutingRuleTO(
  UUID id,
  String apiUrl,
  LoginType loginType,
  LoginInformationTO loginInformation,
  @ValidVesselIMONumber(allowNull = true)
  String vesselIMONumber,
  PublisherRole publisherRole
) {

  public enum LoginType {
    OIDC
  }

  public record LoginInformationTO( // Login credentials
                                  String clientID,
                                  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
                                  String clientSecret,
                                  String tokenURL
  ) {
    @Builder(toBuilder = true) // workaround for intellij issue
    public LoginInformationTO {}
  }

  @Builder(toBuilder = true) // workaround for intellij issue
  public MessageRoutingRuleTO {}
}
