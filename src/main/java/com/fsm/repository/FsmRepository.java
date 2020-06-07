package com.fsm.repository;

import com.fsm.model.FsmEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface FsmRepository extends CrudRepository<FsmEntity, UUID> {

    FsmEntity findByTransactionId(String transactionId);

    List<FsmEntity> findAllByActive(Boolean active);

    @Transactional
    void deleteByTransactionId(String transactionId);
}
