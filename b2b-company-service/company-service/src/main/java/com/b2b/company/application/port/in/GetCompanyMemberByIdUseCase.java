package com.b2b.company.application.port.in;

import com.b2b.company.application.query.dto.GetCompanyMemberByIdQuery;
import com.b2b.company.application.query.dto.GetCompanyMemberByIdResult;

public interface GetCompanyMemberByIdUseCase {

    GetCompanyMemberByIdResult handle(GetCompanyMemberByIdQuery query);
}