package org.dcsa.uisupport.controller;

import lombok.RequiredArgsConstructor;
import org.dcsa.jit.persistence.repository.specification.MessageRoutingRuleSpecification;
import org.dcsa.skernel.errors.exceptions.ConcreteRequestErrorMessageException;
import org.dcsa.skernel.infrastructure.pagination.Cursor;
import org.dcsa.skernel.infrastructure.pagination.CursorDefaults;
import org.dcsa.skernel.infrastructure.pagination.PagedResult;
import org.dcsa.skernel.infrastructure.pagination.Paginator;
import org.dcsa.uisupport.service.MessageRoutingRuleService;
import org.dcsa.uisupport.transferobjects.MessageRoutingRuleTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "unofficial/message-routing-rules", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class MessageRoutingRuleController {

  private final MessageRoutingRuleService service;
  private final Paginator paginator;

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  public List<MessageRoutingRuleTO> getAll(@RequestParam(required = false, defaultValue = "100") Integer limit,
                                           @RequestParam(value = "publisherRole", required = false) String publisherRole,
                                           @RequestParam(value = "vesselIMONumber", required = false) String vesselIMONumber,
                                           HttpServletRequest request,
                                           HttpServletResponse response
  ) {
    Cursor c =
      paginator.parseRequest(
        request,
        new CursorDefaults(limit));


    PagedResult<MessageRoutingRuleTO> result = service.findAll(
      MessageRoutingRuleSpecification.MessageRoutingRuleFilter.builder()
        .publisherRole(publisherRole)
        .vesselIMONumber(vesselIMONumber)
        .build(),
      c
    );
    paginator.setPageHeaders(request, response, c, result);
    return result.content();
  }

  @GetMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public MessageRoutingRuleTO get(@PathVariable UUID id) {
    return service.findById(id);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public MessageRoutingRuleTO post(@Valid @RequestBody MessageRoutingRuleTO request) {
    return service.create(request);
  }

  @PutMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public MessageRoutingRuleTO update(@PathVariable UUID id, @Valid @RequestBody MessageRoutingRuleTO request) {
    if (!id.equals(request.id())) {
      throw ConcreteRequestErrorMessageException.invalidInput("ID in the body must match the URL");
    }
    return service.updateMessageRule(request);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable UUID id) {
    service.deleteById(id);
  }
}
