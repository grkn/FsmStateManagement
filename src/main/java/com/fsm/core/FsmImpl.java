package com.fsm.core;

import com.fsm.constant.Status;
import com.fsm.core.data.FsmEvent;
import com.fsm.core.data.FsmState;
import com.fsm.converter.FsmStateToRestStateConverter;
import com.fsm.converter.RestStateToFsmStateConverter;
import com.fsm.dto.RestStateDto;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

public class FsmImpl implements Fsm {

    private FsmState currentState;

    private Map<String, FsmState> fsmStates = new HashMap<>();

    private long lastUsed = System.currentTimeMillis();

    @Override
    public FsmImpl create(List<RestStateDto> list) {
        lastUsed = System.currentTimeMillis();
        if (CollectionUtils.isEmpty(list)) {
            throw new IllegalArgumentException("Fsm has empty states");
        }

        validateInputs(list.get(0));
        currentState = RestStateToFsmStateConverter.convert(list.get(0));
        currentState.setStatus(Status.ACTIVE);
        fsmStates.put(currentState.getStateName(), currentState);

        defineStates(list);
        defineEvents(list);
        return this;
    }

    @Override
    public RestStateDto nextState(String eventName) {
        lastUsed = System.currentTimeMillis();
        List<RestStateDto> restStateDtos = new ArrayList<>();
        for (FsmEvent fsmEvent : currentState.getFsmEvents()) {
            if (fsmEvent.getEventName().equals(eventName)) {
                restStateDtos.add(FsmStateToRestStateConverter.convert(fsmEvent.getFsmState()));
            }
        }

        if (CollectionUtils.isEmpty(restStateDtos)) {
            throw new IllegalArgumentException("Fsm machine has no state when triggered by event : " + eventName);
        }
        if (restStateDtos.size() > 1) {
            throw new IllegalArgumentException("Fsm machine has two different state when triggered by event : " + eventName);
        }

        if (!CollectionUtils.isEmpty(currentState.getFsmEvents())) {
            currentState.setStatus(Status.SUCCESS);
            currentState = currentState.getFsmEvents().stream()
                    .filter(fsmEvent -> fsmEvent.getEventName().equals(eventName)).map(FsmEvent::getFsmState).findFirst()
                    .orElseThrow(() -> new NoSuchElementException("Event does not exist with given name in current state. Event Name : " + eventName));
            currentState.setStatus(Status.ACTIVE);
        }
        restStateDtos.get(0).setStatus(Status.ACTIVE);
        return restStateDtos.get(0);
    }

    @Override
    public void destroy() {
        currentState = null;
        fsmStates.clear();
    }

    @Override
    public RestStateDto getCurrentState() {
        return FsmStateToRestStateConverter.convert(currentState);
    }

    @Override
    public void setCurrentState(RestStateDto currentState) {
        lastUsed = System.currentTimeMillis();
        this.currentState = RestStateToFsmStateConverter.convert(currentState);
        if (!CollectionUtils.isEmpty(currentState.getEvents())) {
            List<FsmEvent> list = new ArrayList<>();
            currentState.getEvents().forEach(eventStateDto -> {
                FsmState fsmState = fsmStates.get(eventStateDto.getNextStateName());
                list.add(FsmEvent.builder().fsmState(fsmState).eventName(eventStateDto.getEventName()).build());
            });
            this.currentState.setFsmEvents(list);
        }
    }

    @Override
    public long getLastUsed() {
        return lastUsed;
    }

    @Override
    public void setCurrentStatusFailed() {
        currentState.setStatus(Status.FAIL);
    }

    @Override
    public List<RestStateDto> getSuccessfulStates() {
        List<RestStateDto> restStateDtoList = new ArrayList<>();
        for (Map.Entry<String, FsmState> entry : fsmStates.entrySet()) {
            FsmState fsmState = entry.getValue();
            if (fsmState.getStatus() != null && fsmState.getStatus().equals(Status.SUCCESS))
                restStateDtoList.add(FsmStateToRestStateConverter.convert(entry.getValue()));
        }
        return restStateDtoList;
    }


    private void defineEvents(List<RestStateDto> list) {
        for (RestStateDto restStateDto : list) {
            FsmState fsmState = fsmStates.get(restStateDto.getStateName());
            if (!CollectionUtils.isEmpty(restStateDto.getEvents())) {
                List<FsmEvent> eventList = restStateDto.getEvents().stream().map(eventState -> FsmEvent.builder()
                        .eventName(eventState.getEventName())
                        .fsmState(fsmStates.get(eventState.getNextStateName()))
                        .build()).collect(Collectors.toList());
                fsmState.setFsmEvents(eventList);
            }
        }
    }

    private void defineStates(List<RestStateDto> list) {
        for (int i = 1; i < list.size(); i++) {
            validateInputs(list.get(i));
            FsmState fsmState = RestStateToFsmStateConverter.convert(list.get(i));
            fsmStates.put(fsmState.getStateName(), fsmState);
        }
    }

    private void validateInputs(RestStateDto restStateDto) {
        if (StringUtils.isEmpty(restStateDto.getStateName())
                || StringUtils.isEmpty(restStateDto.getRevertEndpoint())
                || restStateDto.getHttpMethod() == null) {
            throw new IllegalArgumentException("Fsm needs stateName, revertEndpoint, httpMethod attributes to maintain trasaction");
        }
    }

}
