package genxsolution.vms.vmsbackend.role_permission_system.user_data_scopee.controller;

import genxsolution.vms.vmsbackend.role_permission_system.user_data_scopee.dto.UserDataScopeResponse;
import genxsolution.vms.vmsbackend.role_permission_system.user_data_scopee.dto.UserDataScopeUpsertRequest;
import genxsolution.vms.vmsbackend.role_permission_system.user_data_scopee.service.UserDataScopeService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/access/user-data-scopes")
public class UserDataScopeController {
    private final UserDataScopeService service;
    public UserDataScopeController(UserDataScopeService service) { this.service = service; }
    @GetMapping public List<UserDataScopeResponse> list() { return service.list(); }
    @GetMapping("/{id}") public UserDataScopeResponse getById(@PathVariable UUID id) { return service.getById(id); }
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public UserDataScopeResponse create(@RequestBody UserDataScopeUpsertRequest request) { return service.create(request); }
    @PutMapping("/{id}") public UserDataScopeResponse update(@PathVariable UUID id, @RequestBody UserDataScopeUpsertRequest request) { return service.update(id, request); }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable UUID id) { service.delete(id); }
}









