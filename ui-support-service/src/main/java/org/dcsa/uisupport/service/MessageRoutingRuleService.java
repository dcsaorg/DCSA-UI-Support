package org.dcsa.uisupport.service;

import lombok.RequiredArgsConstructor;
import org.dcsa.jit.persistence.entity.MessageRoutingRule;
import org.dcsa.jit.persistence.repository.MessageRoutingRuleRepository;
import org.dcsa.jit.persistence.repository.specification.MessageRoutingRuleSpecification;
import org.dcsa.skernel.errors.exceptions.ConcreteRequestErrorMessageException;
import org.dcsa.skernel.infrastructure.pagination.Cursor;
import org.dcsa.skernel.infrastructure.pagination.PagedResult;
import org.dcsa.uisupport.mapping.MessageRouteRuleMapper;
import org.dcsa.uisupport.transferobjects.MessageRoutingRuleTO;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class MessageRoutingRuleService {

  private final MessageRoutingRuleRepository messageRoutingRuleRepository;
  private final MessageRouteRuleMapper messageRouteRuleMapper;

  public PagedResult<MessageRoutingRuleTO> findAll(final MessageRoutingRuleSpecification.MessageRoutingRuleFilter requestFilters, Cursor cursor) {
    return new PagedResult<>(
      messageRoutingRuleRepository.findAll(
        MessageRoutingRuleSpecification.withFilters(requestFilters),
        cursor.toPageRequest()
      ),
      messageRouteRuleMapper::toTO
    );
  }

  public MessageRoutingRuleTO findById(final UUID id) {
    Optional<MessageRoutingRule> foundRule = messageRoutingRuleRepository.findById(id);
    return messageRouteRuleMapper.toTO(
      foundRule.orElseThrow(
        () -> ConcreteRequestErrorMessageException.notFound("MessageRouteRule not found.")));
  }


  public MessageRoutingRuleTO create(final MessageRoutingRuleTO messageRoutingRuleTO) {
    MessageRoutingRule messageRoutingRule = messageRouteRuleMapper.toDAO(messageRoutingRuleTO);
    return messageRouteRuleMapper.toTO(messageRoutingRuleRepository.save(messageRoutingRule));
  }

  public MessageRoutingRuleTO updateMessageRule(final MessageRoutingRuleTO updateTO) {
    MessageRoutingRule original = messageRoutingRuleRepository.findById(updateTO.id())
      .orElseThrow(
        () -> ConcreteRequestErrorMessageException.notFound("MessageRouteRule not found."));
    MessageRoutingRule update = messageRouteRuleMapper.toDAO(updateTO);
    String patchedValue = patchValue(original.getLoginInformation().clientSecret(), update.getLoginInformation().clientSecret());
    MessageRoutingRule.LoginInformation newLoginInformation = update.getLoginInformation().toBuilder()
        .clientSecret(patchedValue)
        .build();
    MessageRoutingRule updateWithSecret = update.toBuilder().loginInformation(newLoginInformation).build();
    messageRoutingRuleRepository.save(updateWithSecret);
    return messageRouteRuleMapper.toTO(updateWithSecret);
  }

  public void deleteById(final UUID id) {
    messageRoutingRuleRepository.deleteById(id);
  }

  // Almost Objects.requireNonNullElse except we allow both values to be null
  private static String patchValue(String originalValue, String patchValue) {
    if (patchValue == null) {
      return originalValue;
    }
    return patchValue;
  }
}
