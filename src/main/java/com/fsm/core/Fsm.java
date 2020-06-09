package com.fsm.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.fsm.core.data.FsmState;
import com.fsm.dto.RestStateDto;
import org.springframework.http.HttpMethod;

import java.util.List;

public interface Fsm {
    FsmImpl create(List<RestStateDto> list);

    RestStateDto nextState(String eventName);

    void destroy();

    RestStateDto getCurrentState();

    void setDataAndEndpointAndMethod(JsonNode data, String endpoint, HttpMethod httpMethod);

    void setCurrentState(RestStateDto convert);

    long getLastUsed();

    void setCurrentStatusFailed();

    List<RestStateDto> getSuccessfulStates();
}
