package org.dcsa.uisupport.controller;

import lombok.RequiredArgsConstructor;
import org.dcsa.skernel.infrastructure.pagination.Pagination;
import org.dcsa.skernel.infrastructure.sorting.Sorter;
import org.dcsa.uisupport.service.TimestampInfoService;
import org.dcsa.uisupport.transferobjects.TimestampInfoTO;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping(value = "unofficial/timestamp-info", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class TimestampInfoController {

  private final List<Sort.Order> DEFAULT_SORT = List.of(Sort.Order.desc("operationsEvent.eventCreatedDateTime"));
  private final Sorter.SortableFields SORTABLE_FIELDS = Sorter.SortableFields.of("eventCreatedDateTime")
    .addMapping("eventCreatedDateTime", "operationsEvent.eventCreatedDateTime");
  private final TimestampInfoService timestampInfoService;

  @GetMapping
  public List<TimestampInfoTO> getTimestampInfos(
    @RequestParam(value = "portVisitID", required = false) String portVisitID,
    @RequestParam(value = "negotiationCycle", required = false) String negotiationCycle,
    @RequestParam(value = "facilitySMDGCode", required = false) String facilitySMDGCode,
    @RequestParam(value = "portCallPart", required = false) String portCallPart,
    @RequestParam(value = Pagination.DCSA_PAGE_PARAM_NAME, defaultValue = "0", required = false) @Min(0)
      int page,
    @RequestParam(value = Pagination.DCSA_PAGESIZE_PARAM_NAME, defaultValue = "100", required = false) @Min(1)
      int pageSize,
    @RequestParam(value = Pagination.DCSA_SORT_PARAM_NAME, required = false)
      String sort,

    HttpServletRequest request,
    HttpServletResponse response) {

    return Pagination.with(request, response, page, pageSize)
      .sortBy(sort, DEFAULT_SORT, SORTABLE_FIELDS)
      .paginate(pageRequest -> timestampInfoService.findAll(pageRequest, portVisitID, negotiationCycle, facilitySMDGCode, portCallPart));
  }
}
