package com.fsm.core;

import com.fsm.core.data.FsmState;
import com.fsm.dto.RestStateDto;

import java.util.List;

public interface Fsm {
    FsmImpl create(List<RestStateDto> list);

    RestStateDto nextState(String eventName);

    void destroy();

    RestStateDto getCurrentState();

    void setCurrentState(RestStateDto convert);

    long getLastUsed();

    void setCurrentStatusFailed();

    List<RestStateDto> getSuccessfulStates();
}
