package com.fsm.core.tupple;

import com.fsm.core.Fsm;

public class FcmTransactionIdTupple {
    private String transactionId;
    private Fsm fsm;

    public FcmTransactionIdTupple(java.lang.String transactionId, Fsm fsm) {
        this.transactionId = transactionId;
        this.fsm = fsm;
    }

    public java.lang.String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(java.lang.String transactionId) {
        this.transactionId = transactionId;
    }

    public Fsm getFsm() {
        return fsm;
    }

    public void setFsm(Fsm fsm) {
        this.fsm = fsm;
    }
}
