package com.fsm.model;

import lombok.*;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "fsm")
public class FsmEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    UUID id;

    String transactionId;

    Boolean active;

}
