package com.fsm.resource;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FsmCreateResource {
    private String transactionId;
    private String description;
}
