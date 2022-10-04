package org.dcsa.uisupport.persistence.repository;

import org.dcsa.uisupport.persistence.entity.PortCallPart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PortCallPartRepository extends JpaRepository<PortCallPart, String> {

  List<PortCallPart> findAllByOrderByDisplayOrderAsc();
}
