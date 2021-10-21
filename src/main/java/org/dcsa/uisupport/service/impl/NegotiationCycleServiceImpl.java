package org.dcsa.uisupport.service.impl;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.events.model.NegotiationCycle;
import org.dcsa.core.service.impl.ExtendedBaseServiceImpl;
import org.dcsa.uisupport.repository.NegotiationCycleRepository;
import org.dcsa.uisupport.service.NegotiationCycleService;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class NegotiationCycleServiceImpl extends ExtendedBaseServiceImpl<NegotiationCycleRepository, NegotiationCycle, String> implements NegotiationCycleService{

        private final NegotiationCycleRepository negotiationCycleRepository;

        @Override
        public NegotiationCycleRepository getRepository() {
            return negotiationCycleRepository;
        }
}
