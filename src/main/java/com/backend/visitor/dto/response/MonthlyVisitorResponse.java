package com.backend.visitor.dto.response;

public record MonthlyVisitorResponse(
    Long visitorCount,
    String targetRange
) {
}
