package genxsolution.vms.vmsbackend.organization.service;

import genxsolution.vms.vmsbackend.common.cache.CacheNames;
import genxsolution.vms.vmsbackend.organization.dto.companybranch.CompanyBranchResponse;
import genxsolution.vms.vmsbackend.organization.dto.companybranch.CreateCompanyBranchRequest;
import genxsolution.vms.vmsbackend.organization.dto.companybranch.UpdateCompanyBranchRequest;
import genxsolution.vms.vmsbackend.organization.exception.ResourceNotFoundException;
import genxsolution.vms.vmsbackend.organization.mapper.CompanyBranchMapper;
import genxsolution.vms.vmsbackend.organization.repository.CompanyBranchRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CompanyBranchService {

    private final CompanyBranchRepository repository;
    private final CompanyBranchMapper mapper;

    public CompanyBranchService(CompanyBranchRepository repository, CompanyBranchMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Cacheable(cacheNames = CacheNames.BRANCH_LIST, key = "(#companyId == null ? 'all' : #companyId) + ':' + #activeOnly")
    public List<CompanyBranchResponse> list(UUID companyId, boolean activeOnly) {
        if (companyId == null) {
            return repository.findAll(activeOnly).stream().map(mapper::toResponse).toList();
        }
        return repository.findAllByCompany(companyId, activeOnly).stream().map(mapper::toResponse).toList();
    }

    @Cacheable(cacheNames = CacheNames.BRANCH_BY_ID, key = "#branchId")
    public CompanyBranchResponse getById(UUID branchId) {
        return repository.findById(branchId)
                .map(mapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("CompanyBranch", branchId.toString()));
    }

    @Caching(
            put = @CachePut(cacheNames = CacheNames.BRANCH_BY_ID, key = "#result.branchId"),
            evict = {
                    @CacheEvict(cacheNames = CacheNames.BRANCH_LIST, allEntries = true),
                    @CacheEvict(cacheNames = CacheNames.ORG_DROPDOWN_ME, allEntries = true)
            }
    )
    public CompanyBranchResponse create(CreateCompanyBranchRequest request) {
        return mapper.toResponse(repository.create(request));
    }

    @Caching(
            put = @CachePut(cacheNames = CacheNames.BRANCH_BY_ID, key = "#branchId"),
            evict = {
                    @CacheEvict(cacheNames = CacheNames.BRANCH_LIST, allEntries = true),
                    @CacheEvict(cacheNames = CacheNames.ORG_DROPDOWN_ME, allEntries = true)
            }
    )
    public CompanyBranchResponse update(UUID branchId, UpdateCompanyBranchRequest request) {
        return repository.update(branchId, request)
                .map(mapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("CompanyBranch", branchId.toString()));
    }

    @Caching(
            evict = {
                    @CacheEvict(cacheNames = CacheNames.BRANCH_BY_ID, key = "#branchId"),
                    @CacheEvict(cacheNames = CacheNames.BRANCH_LIST, allEntries = true),
                    @CacheEvict(cacheNames = CacheNames.ORG_DROPDOWN_ME, allEntries = true)
            }
    )
    public void delete(UUID branchId) {
        if (!repository.delete(branchId)) {
            throw new ResourceNotFoundException("CompanyBranch", branchId.toString());
        }
    }
}






