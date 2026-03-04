package genxsolution.vms.vmsbackend.lookup.controller;

import genxsolution.vms.vmsbackend.lookup.dto.LookupEnumDefinitionDto;
import genxsolution.vms.vmsbackend.lookup.dto.LookupEnumAdminRecordDto;
import genxsolution.vms.vmsbackend.lookup.dto.LookupEnumAdminUpsertRequest;
import genxsolution.vms.vmsbackend.lookup.dto.LookupOptionDto;
import genxsolution.vms.vmsbackend.common.query.ListQueryEngine;
import jakarta.validation.Valid;
import genxsolution.vms.vmsbackend.lookup.service.LookupEnumService;
import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Validated
@RestController
@RequestMapping("/api/v1/enums")
public class LookupEnumController {

    private final LookupEnumService service;

    public LookupEnumController(LookupEnumService service) {
        this.service = service;
    }

    @GetMapping
    public List<LookupEnumDefinitionDto> listSupportedEnums() {
        return service.getSupportedEnums();
    }

    @GetMapping("/{enumKey}")
    public List<LookupOptionDto> getEnumOptions(
            @PathVariable @NotBlank String enumKey,
            @RequestParam(defaultValue = "true") boolean activeOnly
    ) {
        return service.getEnumValues(enumKey, activeOnly);
    }

    @GetMapping("/{enumKey}/records")
    public List<LookupEnumAdminRecordDto> listEnumRecords(
            @PathVariable @NotBlank String enumKey,
            @RequestParam Map<String, String> filters
    ) {
        return ListQueryEngine.apply(
                service.getAdminRecords(enumKey),
                filters,
                Set.of()
        );
    }

    @GetMapping("/{enumKey}/records/{id}")
    public LookupEnumAdminRecordDto getEnumRecordById(
            @PathVariable @NotBlank String enumKey,
            @PathVariable Integer id
    ) {
        return service.getAdminRecordById(enumKey, id);
    }

    @PostMapping("/{enumKey}/records")
    @ResponseStatus(HttpStatus.CREATED)
    public LookupEnumAdminRecordDto createEnumRecord(
            @PathVariable @NotBlank String enumKey,
            @Valid @RequestBody LookupEnumAdminUpsertRequest request
    ) {
        return service.createAdminRecord(enumKey, request);
    }

    @PutMapping("/{enumKey}/records/{id}")
    public LookupEnumAdminRecordDto updateEnumRecord(
            @PathVariable @NotBlank String enumKey,
            @PathVariable Integer id,
            @Valid @RequestBody LookupEnumAdminUpsertRequest request
    ) {
        return service.updateAdminRecord(enumKey, id, request);
    }

    @DeleteMapping("/{enumKey}/records/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEnumRecord(
            @PathVariable @NotBlank String enumKey,
            @PathVariable Integer id
    ) {
        service.deleteAdminRecord(enumKey, id);
    }
}






