package org.dcsa.uisupport.controller;

import lombok.RequiredArgsConstructor;
import org.dcsa.jit.transferobjects.TransportCallTO;
import org.dcsa.skernel.infrastructure.pagination.Cursor;
import org.dcsa.skernel.infrastructure.pagination.CursorDefaults;
import org.dcsa.skernel.infrastructure.pagination.PagedResult;
import org.dcsa.skernel.infrastructure.pagination.Paginator;
import org.dcsa.skernel.infrastructure.validation.ValidVesselIMONumber;
import org.dcsa.uisupport.service.UiTransportCallService;
import org.dcsa.uisupport.transferobjects.TransportCallWithTimestampsTO;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.Size;
import java.util.List;

@RestController
@RequestMapping(value = "unofficial/transport-calls", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class TransportCallController {
  private final UiTransportCallService uiTransportCallService;
  private final Paginator paginator;

  @GetMapping
  public List<TransportCallWithTimestampsTO> findAll(
      @ValidVesselIMONumber(allowNull = true) @RequestParam(value = "vessel.vesselIMONumber", required = false)  String vesselIMONumber,
      @Size(max = 5) @RequestParam(value = "facility.UNLocationCode", required = false) String unLocationCode,
      @Size(max = 5) @RequestParam(value = "facility.facilitySMGDCode", required = false) String facilitySMDGCode,
      @RequestParam(required = false, defaultValue = "100") Integer limit,
      HttpServletRequest request,
      HttpServletResponse response) {

    Cursor c =
      paginator.parseRequest(
        request,
        new CursorDefaults(limit, new Cursor.SortBy(Sort.Direction.DESC, "latestEventCreatedDateTime")));
    PagedResult<TransportCallWithTimestampsTO> result = uiTransportCallService.findAll(vesselIMONumber, unLocationCode, facilitySMDGCode, c);

    paginator.setPageHeaders(request, response, c, result);
    return result.content();
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public TransportCallWithTimestampsTO create(@RequestBody TransportCallTO transportCallTO) {
    return uiTransportCallService.create(transportCallTO);
  }
}
