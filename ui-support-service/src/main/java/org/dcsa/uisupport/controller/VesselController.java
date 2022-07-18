package org.dcsa.uisupport.controller;

import lombok.AllArgsConstructor;
import org.dcsa.jit.persistence.entity.Vessel_;
import org.dcsa.jit.transferobjects.VesselTO;
import org.dcsa.uisupport.service.UiVesselService;
import org.dcsa.skernel.infrastructure.pagination.Cursor;
import org.dcsa.skernel.infrastructure.pagination.CursorDefaults;
import org.dcsa.skernel.infrastructure.pagination.PagedResult;
import org.dcsa.skernel.infrastructure.pagination.Paginator;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "/unofficial/vessels", produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
public class VesselController {

  private final Paginator paginator;
  private final UiVesselService uiVesselService;

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  public List<VesselTO> getAll(@RequestParam(required = false, defaultValue = "100") Integer limit,
                               HttpServletRequest request,
                               HttpServletResponse response
                               ) {
    Cursor c =
      paginator.parseRequest(
        request,
        // VESSEL_IMO_NUMBER is auto-generated and sadly correct because the plugin misreads
        // the property name
        new CursorDefaults(limit, new Cursor.SortBy(Sort.Direction.DESC, Vessel_.VESSEL_IM_ONUMBER)));


    PagedResult<VesselTO> result = uiVesselService.findAll(c);
    paginator.setPageHeaders(request, response, c, result);
    return result.content();
  }

  @GetMapping("/{vesselIMONumber}")
  @ResponseStatus(HttpStatus.OK)
  public VesselTO get(@PathVariable String vesselIMONumber) {
    return uiVesselService.fetchVesselByIMONumber(vesselIMONumber);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public VesselTO post(@Valid @RequestBody VesselTO request) {
    return uiVesselService.createVessel(request);
  }

  @PutMapping("/{vesselIMONumber}")
  @ResponseStatus(HttpStatus.OK)
  public VesselTO update(@PathVariable String vesselIMONumber, @Valid @RequestBody VesselTO request) {
    return uiVesselService.updateVessel(vesselIMONumber, request);
  }
}
