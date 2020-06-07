package com.fsm.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fsm.constant.Status;
import lombok.*;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "rest_request_retry")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestRequestRetryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Lob
    private String requestData;

    private Integer failCount;

    @Enumerated(EnumType.STRING)
    private Status status;

    private String message;

}
