package org.dcsa.uisupport.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dcsa.jit.persistence.entity.OutboxMessage;
import org.dcsa.jit.persistence.entity.TimestampNotificationDead;
import org.dcsa.jit.persistence.repository.OutboxMessageRepository;
import org.dcsa.jit.persistence.repository.TimestampNotificationDeadRepository;
import org.dcsa.skernel.errors.exceptions.ConcreteRequestErrorMessageException;
import org.dcsa.uisupport.mapping.TimestampNotificationDeadMapper;
import org.dcsa.uisupport.transferobjects.TimestampNotificationDeadTO;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TimestampNotificationDeadService {
  private final TimestampNotificationDeadRepository timestampNotificationDeadRepository;
  private final TimestampNotificationDeadMapper timestampNotificationDeadMapper;

  private final OutboxMessageRepository outboxMessageRepository;

  public List<TimestampNotificationDeadTO> findAll() {
    return timestampNotificationDeadRepository.findAll().stream()
      .map(timestampNotificationDeadMapper::toTO)
      .toList();
  }

  @Transactional
  public void retry(UUID deadNotificationID) {
    TimestampNotificationDead timestampNotificationDead = timestampNotificationDeadRepository.findById(deadNotificationID)
      .orElseThrow(() -> ConcreteRequestErrorMessageException.notFound("No dead notification with ID " + deadNotificationID));

    OutboxMessage outboxMessage = OutboxMessage.retry(timestampNotificationDead);
    outboxMessageRepository.save(outboxMessage);
    timestampNotificationDeadRepository.deleteById(deadNotificationID);
    // Note that OutboxMessage.retry preserves the ID so outboxMessage has the same ID as the failed notification it is replacing.
    log.info("Retrying request {} transmitting {} due to API request.", outboxMessage.getId(), timestampNotificationDead);
  }

  public void discardDeadNotification(UUID deadNotificationID) {
    try {
      timestampNotificationDeadRepository.deleteById(deadNotificationID);
    } catch (EmptyResultDataAccessException e) {
     throw ConcreteRequestErrorMessageException.notFound("No dead notification with ID " + deadNotificationID, e);
    }
  }
}
