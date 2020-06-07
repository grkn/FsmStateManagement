package com.fsm.core.tupple;

import com.fsm.dto.RestStateDto;

public class TransactionIdRestStateDtoTupple {
    private String transactionId;
    private RestStateDto restStateDto;

    public TransactionIdRestStateDtoTupple(String transactionId, RestStateDto restStateDto) {
        this.transactionId = transactionId;
        this.restStateDto = restStateDto;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setFsm(String transactionId) {
        this.transactionId = transactionId;
    }

    public RestStateDto getRestStateDto() {
        return restStateDto;
    }

    public void setRestStateDto(RestStateDto restStateDto) {
        this.restStateDto = restStateDto;
    }
}
