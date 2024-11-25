package com.backend.visitor.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Visitor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long visitorCount;
    private String targetRange;

    @Builder
    public Visitor(Long visitorCount, String targetRange) {
        this.visitorCount = visitorCount;
        this.targetRange = targetRange;
    }

    public void updateVisitorCount(Long visitorCount) {
        this.visitorCount = visitorCount;
    }
}
