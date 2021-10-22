package org.dcsa.uisupport.controller;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.controller.ExtendedBaseController;
import org.dcsa.uisupport.model.UITimestampInfo;
import org.dcsa.uisupport.service.UITimestampInfoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping(
        value = "unofficial/timestamp-info",
        produces = {MediaType.APPLICATION_JSON_VALUE})
@RequiredArgsConstructor
public class UITimestampInfoController extends ExtendedBaseController<UITimestampInfoService, UITimestampInfo, UUID> {
    private final UITimestampInfoService UITimestampInfoService;

    @Override
    public UITimestampInfoService getService() {
        return UITimestampInfoService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Mono<UITimestampInfo> create(@Valid @RequestBody UITimestampInfo UITimestampInfo) {
        return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN));
    }

    @PutMapping(path = "{eventID}")
    public Mono<UITimestampInfo> update(@PathVariable UUID eventID, @Valid @RequestBody UITimestampInfo UITimestampInfo) {
        return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN));
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Mono<Void> delete(@RequestBody UITimestampInfo UITimestampInfo) {
        return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN));
    }

    @DeleteMapping(path = "{eventID}")
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Mono<Void> deleteById(@PathVariable UUID eventID) {
        return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN));
    }
}
