package org.dcsa.uisupport.persistence.repository;

import org.dcsa.uisupport.persistence.entity.TransportCallWithTimestamps;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * NOTE: Since transport_call_with_timestamps is a view we can only read from it, writes will fail.
 */
public interface TransportCallWithTimestampsRepository extends JpaRepository<TransportCallWithTimestamps, UUID> {}
