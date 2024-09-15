package com.techzo.cambiazo.exchanges.domain.services;

import com.techzo.cambiazo.exchanges.domain.model.entities.Plan;
import com.techzo.cambiazo.exchanges.domain.model.queries.GetAllPlansQuery;
import com.techzo.cambiazo.exchanges.domain.model.queries.GetPlanByIdQuery;

import java.util.List;
import java.util.Optional;

public interface IPlanQueryService {

    Optional<Plan>handle(GetPlanByIdQuery query);

    List<Plan>handle(GetAllPlansQuery query);
}
