package com.hotrodoan.repository;

import com.hotrodoan.model.Position;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PositionRepository extends JpaRepository<Position, Long> {
    Page<Position> findByNameContaining(String name, Pageable pageable);
    Position findByName(String name);
}
