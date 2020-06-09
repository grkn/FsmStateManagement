package com.fsm.core.tupple;

import com.fsm.dto.RestStateDto;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TransactionIdRestStateDtoTupple {
    private String transactionId;
    private RestStateDto restStateDto;
}
