package com.app.BugBee.repository;

import com.app.BugBee.entity.Resource;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface ResourceRepository extends R2dbcRepository<Resource, Long> {

}
