package com.fsm.dto;

import com.fsm.constant.Status;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RestClientRequestDto {
    private UUID id;
    private RestStateDto restStateDto;
    private Integer failCount;
    private Status status;
    private String message;
}
