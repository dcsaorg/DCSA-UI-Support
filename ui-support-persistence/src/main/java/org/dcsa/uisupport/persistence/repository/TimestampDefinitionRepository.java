package org.dcsa.uisupport.persistence.repository;

import org.dcsa.uisupport.persistence.entity.TimestampDefinition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TimestampDefinitionRepository extends JpaRepository<TimestampDefinition, String> {}
