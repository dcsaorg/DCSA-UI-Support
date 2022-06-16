package org.dcsa.uisupport.persistence.repository;

import org.dcsa.uisupport.persistence.entity.OpsEventTimestampDefinitionForUI;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OpsEventTimestampDefinitionRepositoryForUI
    extends JpaRepository<OpsEventTimestampDefinitionForUI, UUID>,
        JpaSpecificationExecutor<OpsEventTimestampDefinitionForUI> {}
