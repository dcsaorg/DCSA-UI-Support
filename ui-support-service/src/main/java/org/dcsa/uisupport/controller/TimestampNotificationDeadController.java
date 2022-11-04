package org.dcsa.uisupport.controller;

import lombok.RequiredArgsConstructor;
import org.dcsa.uisupport.service.TimestampNotificationDeadService;
import org.dcsa.uisupport.transferobjects.TimestampNotificationDeadTO;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "unofficial/dead-timestamp-notifications", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class TimestampNotificationDeadController {
  private final TimestampNotificationDeadService timestampNotificationDeadService;

  @GetMapping
  public List<TimestampNotificationDeadTO> findAll() {
    return timestampNotificationDeadService.findAll();
  }

  @PostMapping("/{id}/retry")
  public void retryFailedNotification(@PathVariable UUID id) {
    this.timestampNotificationDeadService.retry(id);
  }

  @DeleteMapping("/{id}")
  public void discardFailedNotification(@PathVariable UUID id) {
    this.timestampNotificationDeadService.discardDeadNotification(id);
  }
}
