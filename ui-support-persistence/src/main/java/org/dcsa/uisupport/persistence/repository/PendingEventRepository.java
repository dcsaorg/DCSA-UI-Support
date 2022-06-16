package org.dcsa.uisupport.persistence.repository;

import org.dcsa.uisupport.persistence.entity.PendingEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PendingEventRepository extends JpaRepository<PendingEvent, UUID> {}
