package com.fsm.dto;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class EventStateDto {
    private String eventName;
    private String nextStateName;
}
