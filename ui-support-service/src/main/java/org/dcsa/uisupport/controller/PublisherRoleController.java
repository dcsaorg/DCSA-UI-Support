package org.dcsa.uisupport.controller;

import lombok.RequiredArgsConstructor;
import org.dcsa.uisupport.service.PublisherRoleService;
import org.dcsa.uisupport.transferobjects.PublisherRoleDetailTO;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "unofficial/publisher-roles", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class PublisherRoleController {

  private final PublisherRoleService publisherRoleService;


  @GetMapping
  public List<PublisherRoleDetailTO> getPublisherRoles() {
    return publisherRoleService.getPublisherRoleDetails();
  }
}
