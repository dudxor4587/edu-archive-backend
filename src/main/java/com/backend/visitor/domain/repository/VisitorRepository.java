package com.backend.visitor.domain.repository;

import com.backend.visitor.domain.Visitor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VisitorRepository extends JpaRepository<Visitor, Long> {
    Visitor findByTargetRange(String range);
}
