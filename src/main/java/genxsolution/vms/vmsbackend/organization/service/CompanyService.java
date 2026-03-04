package genxsolution.vms.vmsbackend.organization.service;

import genxsolution.vms.vmsbackend.common.cache.CacheNames;
import genxsolution.vms.vmsbackend.organization.dto.company.CompanyResponse;
import genxsolution.vms.vmsbackend.organization.dto.company.CreateCompanyRequest;
import genxsolution.vms.vmsbackend.organization.dto.company.UpdateCompanyRequest;
import genxsolution.vms.vmsbackend.organization.exception.ResourceNotFoundException;
import genxsolution.vms.vmsbackend.organization.mapper.CompanyMapper;
import genxsolution.vms.vmsbackend.organization.repository.CompanyRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CompanyService {

    private final CompanyRepository repository;
    private final CompanyMapper mapper;

    public CompanyService(CompanyRepository repository, CompanyMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Cacheable(cacheNames = CacheNames.COMPANY_LIST, key = "#activeOnly")
    public List<CompanyResponse> list(boolean activeOnly) {
        return repository.findAll(activeOnly).stream().map(mapper::toResponse).toList();
    }

    @Cacheable(cacheNames = CacheNames.COMPANY_BY_ID, key = "#companyId")
    public CompanyResponse getById(UUID companyId) {
        return repository.findById(companyId)
                .map(mapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Company", companyId.toString()));
    }

    @Caching(
            put = @CachePut(cacheNames = CacheNames.COMPANY_BY_ID, key = "#result.companyId"),
            evict = {
                    @CacheEvict(cacheNames = CacheNames.COMPANY_LIST, allEntries = true),
                    @CacheEvict(cacheNames = CacheNames.ORG_DROPDOWN_CORE, allEntries = true),
                    @CacheEvict(cacheNames = CacheNames.ORG_DROPDOWN_ME, allEntries = true)
            }
    )
    public CompanyResponse create(CreateCompanyRequest request) {
        return mapper.toResponse(repository.create(request));
    }

    @Caching(
            put = @CachePut(cacheNames = CacheNames.COMPANY_BY_ID, key = "#companyId"),
            evict = {
                    @CacheEvict(cacheNames = CacheNames.COMPANY_LIST, allEntries = true),
                    @CacheEvict(cacheNames = CacheNames.ORG_DROPDOWN_CORE, allEntries = true),
                    @CacheEvict(cacheNames = CacheNames.ORG_DROPDOWN_ME, allEntries = true),
                    @CacheEvict(cacheNames = CacheNames.BRANCH_LIST, allEntries = true)
            }
    )
    public CompanyResponse update(UUID companyId, UpdateCompanyRequest request) {
        return repository.update(companyId, request)
                .map(mapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Company", companyId.toString()));
    }

    @Caching(
            evict = {
                    @CacheEvict(cacheNames = CacheNames.COMPANY_BY_ID, key = "#companyId"),
                    @CacheEvict(cacheNames = CacheNames.COMPANY_LIST, allEntries = true),
                    @CacheEvict(cacheNames = CacheNames.ORG_DROPDOWN_CORE, allEntries = true),
                    @CacheEvict(cacheNames = CacheNames.ORG_DROPDOWN_ME, allEntries = true),
                    @CacheEvict(cacheNames = CacheNames.BRANCH_LIST, allEntries = true)
            }
    )
    public void delete(UUID companyId) {
        if (!repository.delete(companyId)) {
            throw new ResourceNotFoundException("Company", companyId.toString());
        }
    }
}






