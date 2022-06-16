package org.dcsa.uisupport.persistence.repository;

import org.dcsa.jit.persistence.entity.OpsEventTimestampDefinition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OpsEventTimestampDefinitionRepositoryForUI
    extends JpaRepository<OpsEventTimestampDefinition, UUID>,
        JpaSpecificationExecutor<OpsEventTimestampDefinition> {}
