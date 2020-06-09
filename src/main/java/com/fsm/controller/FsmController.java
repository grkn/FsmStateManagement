package com.fsm.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fsm.dto.RestStateDto;
import com.fsm.resource.FsmCreateResource;
import com.fsm.service.FsmService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/rest/v1/fsm")
public class FsmController {

    private final FsmService fsmService;

    public FsmController(FsmService fsmService) {
        this.fsmService = fsmService;
    }

    @PostMapping("/create")
    public ResponseEntity<FsmCreateResource> createFsmStates(@RequestBody List<RestStateDto> restStateDto) throws JsonProcessingException {
        return ResponseEntity.status(HttpStatus.CREATED).body(FsmCreateResource.builder()
                .description("Created")
                .transactionId(fsmService.create(restStateDto))
                .build());
    }

    @GetMapping("/{transactionId}/next/{stateName}")
    public ResponseEntity<RestStateDto> nextFsmState(@PathVariable("stateName") String stateName, @PathVariable("transactionId") String transactionId) throws JsonProcessingException {
        return ResponseEntity.ok(fsmService.nextState(transactionId, stateName));
    }

    @DeleteMapping("/{transactionId}")
    public ResponseEntity<Void> deleteFsmState(@PathVariable String transactionId) {
        fsmService.deleteFsm(transactionId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{transactionId}/current/state")
    public ResponseEntity<RestStateDto> currentState(@PathVariable("transactionId") String transactionId) {
        return ResponseEntity.ok(fsmService.getCurrentState(transactionId));
    }

    @GetMapping("/current/state/active")
    public ResponseEntity<Map<String, RestStateDto>> findAllActiveFsm() {
        return ResponseEntity.ok(fsmService.findAllActiveFsm());
    }

    @GetMapping("/current/state/idle")
    public ResponseEntity<Map<String, RestStateDto>> findAllPassiveFsms() {
        return ResponseEntity.ok(fsmService.findAllPassiveFsm());
    }

    @PutMapping("/{transactionId}/state/fail")
    public ResponseEntity<Void> fail(@PathVariable("transactionId") String transactionId) {
        fsmService.fsmFailed(transactionId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{transactionId}/current/data")
    public ResponseEntity<Void> persistData(@PathVariable("transactionId") String transactionId, @RequestBody RestStateDto restStateDto) {
        fsmService.setData(transactionId, restStateDto);
        return ResponseEntity.noContent().build();
    }
}
