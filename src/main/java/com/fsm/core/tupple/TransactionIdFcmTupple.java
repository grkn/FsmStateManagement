package com.fsm.core.tupple;

import com.fsm.core.Fsm;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TransactionIdFcmTupple {
    private String transactionId;
    private Fsm fsm;
}
