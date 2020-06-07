package com.fsm.repository;

import com.fsm.constant.Status;
import com.fsm.model.RestRequestRetryEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface RestRequestRetryRepository extends CrudRepository<RestRequestRetryEntity, UUID> {

    List<RestRequestRetryEntity> findAllByFailCountLessThanAndStatus(Integer failCount, Status status);
}
