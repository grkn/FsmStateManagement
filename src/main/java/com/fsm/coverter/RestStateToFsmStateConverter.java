package com.fsm.coverter;

import com.fsm.core.data.FsmState;
import com.fsm.dto.RestStateDto;

public class RestStateToFsmStateConverter {

    public static FsmState convert(RestStateDto restStateDto) {
        return FsmState.builder()
                .data(restStateDto.getData())
                .httpMethod(restStateDto.getHttpMethod())
                .revertEndpoint(restStateDto.getRevertEndpoint())
                .stateName(restStateDto.getStateName())
                .status(restStateDto.getStatus())
                .build();
    }
}
