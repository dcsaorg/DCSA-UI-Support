package org.dcsa.uisupport.persistence.repository;

import org.dcsa.uisupport.persistence.entity.JITPortVisitUIContext;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * NOTE: Since jit_port_visit_ui_context is a view we can only read from it, writes will fail.
 */
public interface JITPortVisitUIContextRepository extends JpaRepository<JITPortVisitUIContext, UUID> {}
