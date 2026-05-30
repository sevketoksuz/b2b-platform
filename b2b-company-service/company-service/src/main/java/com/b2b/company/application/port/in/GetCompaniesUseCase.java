package com.b2b.company.application.port.in;

import com.b2b.company.application.query.dto.GetCompaniesQuery;
import com.b2b.company.application.query.dto.GetCompaniesResult;

public interface GetCompaniesUseCase {

    GetCompaniesResult handle(GetCompaniesQuery query);
}