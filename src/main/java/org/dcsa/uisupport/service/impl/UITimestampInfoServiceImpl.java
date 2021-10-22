package org.dcsa.uisupport.service.impl;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.service.impl.ExtendedBaseServiceImpl;
import org.dcsa.uisupport.model.UITimestampInfo;
import org.dcsa.uisupport.repository.UITimestampInfoRepository;
import org.dcsa.uisupport.service.UITimestampInfoService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class UITimestampInfoServiceImpl extends ExtendedBaseServiceImpl<UITimestampInfoRepository, UITimestampInfo, UUID>
        implements UITimestampInfoService {
    private final UITimestampInfoRepository UITimestampInfoRepository;

    @Override
    public UITimestampInfoRepository getRepository() {
        return UITimestampInfoRepository;
    }

}
