# Database I/O Optimization Plan (Spring Boot + PostgreSQL + Redis + React)

## 1) Core Architecture
- PostgreSQL is the source of truth.
- Redis is a read-through/cache-aside acceleration layer.
- Backend caches list/detail/dropdown/enum reads with TTL + explicit eviction on writes.
- Frontend caches GET responses (short TTL) and deduplicates in-flight requests.

## 2) Multi-tenant Index Strategy (PostgreSQL)
- Always lead indexes with tenant scope (`company_id` or `company_code`).
- Add query pattern columns next: status, date, searchable code fields.
- Use lowercased functional index for case-insensitive search.
- Use partial indexes for active records when most traffic is active-only.

Example indexes:

```sql
-- Vehicles list/search by tenant + status + date
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_vehicle_tenant_status_date
  ON vehicle (company_id, operational_status_id, created_at DESC);

-- Plate search (ILIKE/LIKE lower)
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_vehicle_tenant_plate_lower
  ON vehicle (company_id, lower(plate_no));

-- Active-only fast path
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_vehicle_tenant_active
  ON vehicle (company_id, created_at DESC)
  WHERE is_active = true;

-- Branch list filters
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_branch_tenant_active
  ON company_branch (company_id, is_active, branch_name);
```

## 3) Redis Cache Strategy
- Pattern: cache-aside (`@Cacheable` on reads, `@CacheEvict/@CachePut` on writes).
- Tenant-aware keys for user/tenant-scoped data.
- Short TTL for list pages, medium TTL for detail, long TTL for stable enums.

Implemented cache groups:
- `lookup:enum-values`
- `org:dropdown:core`
- `org:dropdown:me`
- `org:company:list`
- `org:company:by-id`
- `org:branch:list`
- `org:branch:by-id`

## 4) Redis Key Patterns
- `vms::org:company:list::<activeOnly>`
- `vms::org:company:by-id::<companyId>`
- `vms::org:branch:list::<companyId|all>:<activeOnly>`
- `vms::org:branch:by-id::<branchId>`
- `vms::org:dropdown:me::<tenantCode>:<activeOnly>`
- `vms::lookup:enum-values::<enumKey>:<activeOnly>`

## 5) Recommended TTL Values
- Enums / stable lookup: 6h
- Core dropdowns: 30m
- User-context dropdowns: 5m
- Detail DTO: 5m
- List DTO: 20s
- Fallback/default: 30s

## 6) API Design (List vs Detail DTO)
- List endpoint:
  - Return lightweight columns needed for table view.
  - Include pagination metadata (`page`, `size`, `total`, `items`).
- Detail endpoint:
  - Return full object for view/edit dialog.

Example:
- `GET /api/v1/vehicles?page=1&size=25&status=active&plate_like=...` -> `VehicleListItemDto[] + meta`
- `GET /api/v1/vehicles/{id}` -> `VehicleDetailDto`

## 7) Cache Invalidation After Writes
- On create/update/delete:
  - Evict list cache(s) for entity.
  - Update or evict detail cache for entity ID.
  - Evict dependent dropdown caches if they include the changed entity.

Implemented examples:
- Company write => evict company lists + organization dropdown caches.
- Branch write => evict branch lists + user-context dropdown caches.

## 8) Frontend Request Optimization
- GET response cache with TTL.
- In-flight deduplication (same request/key shares one Promise).
- Mutation auto-invalidates related cached GET groups by API domain tags.
- Query key includes params + token to avoid tenant/user data leaks.

## 9) Optional Search Optimization
- Use `pg_trgm` GIN for fuzzy plate/code/name search:

```sql
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_vehicle_plate_trgm
  ON vehicle USING gin (plate_no gin_trgm_ops);
```

- For global text search, consider PostgreSQL FTS (`tsvector`) first.
- Move to OpenSearch/Elasticsearch only when cross-entity search becomes heavy.

## 10) Save/Update I/O Best Practices
- Do partial updates (`PATCH` semantics) to avoid writing unchanged fields.
- Use optimistic locking/version column for high-concurrency rows.
- Batch writes (`jdbcTemplate.batchUpdate`) for bulk operations.
- Avoid immediate read-after-write when response can be composed from input + generated ID.
- Keep transactions short and scoped.
- Use `INSERT ... ON CONFLICT DO UPDATE` for idempotent upserts.

## 11) Spring Cache Annotation Patterns

```java
@Cacheable(cacheNames = "org:company:list", key = "#activeOnly")
public List<CompanyResponse> list(boolean activeOnly) { ... }

@Cacheable(cacheNames = "org:branch:list", key = "(#companyId == null ? 'all' : #companyId) + ':' + #activeOnly")
public List<BranchResponse> list(UUID companyId, boolean activeOnly) { ... }

@Caching(
  put = @CachePut(cacheNames = "org:company:by-id", key = "#result.companyId"),
  evict = @CacheEvict(cacheNames = "org:company:list", allEntries = true)
)
public CompanyResponse create(CreateCompanyRequest req) { ... }
```

