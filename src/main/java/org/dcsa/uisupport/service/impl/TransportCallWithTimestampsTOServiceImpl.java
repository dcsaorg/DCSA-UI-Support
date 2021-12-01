package org.dcsa.uisupport.service.impl;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.events.service.impl.AbstractTransportCallTOServiceImpl;
import org.dcsa.uisupport.model.TransportCallWithTimestampsTO;
import org.dcsa.uisupport.repository.TransportCallWithTimestampsRepository;
import org.dcsa.uisupport.service.TransportCallWithTimestampsTOService;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TransportCallWithTimestampsTOServiceImpl extends AbstractTransportCallTOServiceImpl<TransportCallWithTimestampsRepository, TransportCallWithTimestampsTO> implements TransportCallWithTimestampsTOService {

    private final TransportCallWithTimestampsRepository transportCallWithTimestampsRepository;

    @Override
    public TransportCallWithTimestampsRepository getRepository() {
        return transportCallWithTimestampsRepository;
    }
}
