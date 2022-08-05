package org.dcsa.uisupport.persistence.repository;

import lombok.NonNull;
import org.dcsa.uisupport.persistence.entity.TransportCallWithTimestamps;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

/**
 * NOTE: Since transport_call_with_timestamps is a view we can only read from it, writes will fail.
 */
public interface TransportCallWithTimestampsRepository extends JpaRepository<TransportCallWithTimestamps, UUID>, JpaSpecificationExecutor<TransportCallWithTimestamps> {
  @Override
  Page<TransportCallWithTimestamps> findAll(Specification<TransportCallWithTimestamps> spec, @NonNull Pageable pageable);
}
