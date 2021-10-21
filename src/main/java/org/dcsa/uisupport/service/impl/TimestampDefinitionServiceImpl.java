package org.dcsa.uisupport.service.impl;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.events.model.TimestampDefinition;
import org.dcsa.core.service.impl.ExtendedBaseServiceImpl;
import org.dcsa.uisupport.repository.TimestampDefinitionRepository;
import org.dcsa.uisupport.service.TimestampDefinitionService;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TimestampDefinitionServiceImpl extends ExtendedBaseServiceImpl<TimestampDefinitionRepository, TimestampDefinition, String> implements TimestampDefinitionService {

    private final TimestampDefinitionRepository timestampDefinitionRepository;

    @Override
    public TimestampDefinitionRepository getRepository() {
        return timestampDefinitionRepository;
    }

}
