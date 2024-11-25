package com.backend.visitor.mapper;

import com.backend.visitor.domain.Visitor;
import com.backend.visitor.dto.response.MonthlyVisitorResponse;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface VisitorMapper {

    List<MonthlyVisitorResponse> toMonthlyVisitorResponses(List<Visitor> visitors);
}
