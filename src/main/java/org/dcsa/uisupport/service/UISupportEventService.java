package org.dcsa.uisupport.service;

import org.dcsa.core.events.model.Event;
import org.dcsa.core.events.service.GenericEventService;
import org.dcsa.core.extendedrequest.ExtendedRequest;
import reactor.core.publisher.Mono;

public interface UISupportEventService extends GenericEventService {

    Mono<Integer> countAllExtended(ExtendedRequest<Event> extendedRequest);
}