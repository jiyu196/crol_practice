package com.kiylab.crol.repository;

import com.kiylab.crol.entity.Attach;
import org.springframework.data.jpa.repository.JpaRepository;

// Attach 엔티티용 Repository
public interface AttachRepository extends JpaRepository<Attach, Long> {
}
