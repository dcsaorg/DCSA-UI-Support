package org.dcsa.uisupport.service.impl;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.service.impl.ExtendedBaseServiceImpl;
import org.dcsa.uisupport.model.PortWithTimezone;
import org.dcsa.uisupport.repository.PortWithTimezoneRepository;
import org.dcsa.uisupport.service.PortWithTimezoneService;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PortWithTimezoneServiceImpl extends ExtendedBaseServiceImpl<PortWithTimezoneRepository, PortWithTimezone, String> implements PortWithTimezoneService {

    private final PortWithTimezoneRepository portWithTimezoneRepository;

    @Override
    public PortWithTimezoneRepository getRepository() {
        return portWithTimezoneRepository;
    }
}
