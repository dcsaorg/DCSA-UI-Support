package org.dcsa.uisupport.persistence.repository;

import org.dcsa.uisupport.persistence.entity.OpsEventTimestampDefinition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface OpsEventTimestampDefinitionRepository extends JpaRepository<OpsEventTimestampDefinition, UUID>, JpaSpecificationExecutor<OpsEventTimestampDefinition> {}
