package com.b2b.company.application.port.in;

import com.b2b.company.application.query.dto.GetCompanyByIdQuery;
import com.b2b.company.application.query.dto.GetCompanyByIdResult;

public interface GetCompanyByIdUseCase {

    GetCompanyByIdResult handle(GetCompanyByIdQuery query);
}