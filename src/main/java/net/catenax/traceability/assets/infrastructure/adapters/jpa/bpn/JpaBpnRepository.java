package net.catenax.traceability.assets.infrastructure.adapters.jpa.bpn;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaBpnRepository extends JpaRepository<BpnEntity, String> {
}
