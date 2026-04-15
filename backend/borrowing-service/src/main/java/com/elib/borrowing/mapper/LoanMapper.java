package com.elib.borrowing.mapper;

import com.elib.borrowing.dto.LoanResponse;
import com.elib.borrowing.entity.Loan;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LoanMapper {

    LoanResponse toResponse(Loan loan);

    List<LoanResponse> toResponseList(List<Loan> loans);
}
