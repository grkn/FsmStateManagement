package com.fsm.core.data;

import com.fasterxml.jackson.databind.JsonNode;
import com.fsm.constant.Status;
import lombok.*;
import org.springframework.http.HttpMethod;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FsmState {
    private String stateName;
    private HttpMethod httpMethod;
    private String revertEndpoint;
    private JsonNode data;
    private List<FsmEvent> fsmEvents;
    private Status status;
}
