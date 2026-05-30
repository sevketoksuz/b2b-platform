package com.b2b.company.application.port.in;

import com.b2b.company.application.query.dto.GetCompanyMembersQuery;
import com.b2b.company.application.query.dto.GetCompanyMembersResult;

public interface GetCompanyMembersUseCase {

    GetCompanyMembersResult handle(GetCompanyMembersQuery query);
}