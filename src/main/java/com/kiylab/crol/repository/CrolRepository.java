package com.kiylab.crol.repository;

import com.kiylab.crol.entity.Crol;
import org.springframework.data.jpa.repository.JpaRepository;

// JpaRepository<엔티티 클래스, PK 타입>
// -> 자동으로 CRUD 메서드를 사용할 수 있음 (save, findAll 등)
public interface CrolRepository extends JpaRepository<Crol, Long> {
}