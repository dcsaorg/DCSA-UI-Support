package org.dcsa.uisupport.repository;

import org.dcsa.core.repository.ExtendedRepository;
import org.dcsa.uisupport.model.UITimestampInfo;
import org.dcsa.uisupport.model.PendingEvent;
import org.dcsa.uisupport.model.UnmappedEvent;
import org.springframework.data.r2dbc.repository.Query;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface UITimestampInfoRepository extends ExtendedRepository<UITimestampInfo, UUID> {

}
