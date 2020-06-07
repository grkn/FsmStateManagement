package com.fsm.repository;

import com.fsm.model.FsmEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface FsmRepository extends CrudRepository<FsmEntity, UUID> {

    FsmEntity findByTransactionId(String transactionId);

    List<FsmEntity> findAllByActive(Boolean active);

}
