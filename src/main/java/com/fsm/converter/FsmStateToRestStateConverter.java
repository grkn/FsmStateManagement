package com.fsm.converter;

import com.fsm.dto.EventStateDto;
import com.fsm.core.data.FsmState;
import com.fsm.dto.RestStateDto;

import java.util.stream.Collectors;

public class FsmStateToRestStateConverter {

    public static RestStateDto convert(FsmState fsmState) {
        return RestStateDto.builder()
                .stateName(fsmState.getStateName())
                .data(fsmState.getData())
                .revertEndpoint(fsmState.getRevertEndpoint())
                .httpMethod(fsmState.getHttpMethod())
                .events(fsmState.getFsmEvents() != null ? fsmState.getFsmEvents().stream()
                        .map(fsmEvent -> EventStateDto.builder()
                                .eventName(fsmEvent.getEventName())
                                .nextStateName(fsmEvent.getFsmState() != null ? fsmEvent.getFsmState().getStateName() : null)
                                .build())
                        .collect(Collectors.toList()) : null)
                .status(fsmState.getStatus())
                .build();
    }
}
