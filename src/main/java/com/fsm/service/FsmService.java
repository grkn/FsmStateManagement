package com.fsm.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fsm.client.RedisClient;
import com.fsm.client.RestClient;
import com.fsm.constant.Status;
import com.fsm.core.Fsm;
import com.fsm.core.FsmImpl;
import com.fsm.dto.RestClientRequestDto;
import com.fsm.dto.RestStateDto;
import com.fsm.model.FsmEntity;
import com.fsm.repository.FsmRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@AllArgsConstructor
public class FsmService {

    private static final String CURRENT_STATE = "_current_state_";
    private Map<String, Fsm> fsmMap;
    private final RedisClient redisClient;
    private final ObjectMapper objectMapper;
    private final FsmRepository fsmRepository;
    private final RestClient restClient;

    @PostConstruct
    public void initialize() {
        fsmMap = new ConcurrentHashMap<>();
        fsmRepository.findAllByActive(true)
                .forEach(fsmEntity -> {
                    Fsm fsm = createFsmAndSetCurrentStateOfFsm(fsmEntity);
                    if (Objects.nonNull(fsm)) {
                        fsmMap.put(fsmEntity.getTransactionId(), fsm);
                    }
                });
    }

    public String create(List<RestStateDto> restStateDtos) throws JsonProcessingException {
        String transactionId = UUID.randomUUID().toString();
        Fsm fsm = new FsmImpl();
        fsmMap.put(transactionId, fsm.create(restStateDtos));
        redisClient.setJsonValue(transactionId, objectMapper.writeValueAsString(restStateDtos));
        redisClient.setJsonValue(transactionId + CURRENT_STATE, objectMapper.writeValueAsString(fsm.getCurrentState()));
        fsmRepository.save(FsmEntity.builder().transactionId(transactionId).active(true).build());
        return transactionId;
    }

    public RestStateDto nextState(String transactionId, String stateName) throws JsonProcessingException {
        Fsm fsm = fsmMap.get(transactionId);
        if (fsm == null) {
            fsm = restoreFromRedisAndDatabase(transactionId);
        }
        RestStateDto restStateDto = fsm.nextState(stateName);

        redisClient.setJsonValue(transactionId + CURRENT_STATE, objectMapper.writeValueAsString(fsm.getCurrentState()));
        return restStateDto;
    }

    public void removeInMemoryFsm(String transactionId) {
        Fsm fsm = fsmMap.get(transactionId);
        if (Objects.nonNull(fsm)) {
            fsm.destroy();
            fsmMap.remove(transactionId);
        }
        updateFsmActiveState(transactionId);
    }

    private void updateFsmActiveState(String transactionId) {
        FsmEntity fsmEntity = fsmRepository.findByTransactionId(transactionId);
        if (Objects.nonNull(fsmEntity)) {
            fsmEntity.setActive(false);
            fsmRepository.save(fsmEntity);
        }
    }

    public void deleteFsm(String transactionId) {
        Fsm fsm = fsmMap.get(transactionId);
        if (fsm == null) {
            fsm = restoreFromRedisAndDatabase(transactionId);
        }
        fsm.destroy();
        fsmMap.remove(transactionId);
        fsmRepository.deleteByTransactionId(transactionId);
        redisClient.clearJsonValue(transactionId);
        redisClient.clearJsonValue(transactionId + CURRENT_STATE);
    }

    public RestStateDto getCurrentState(String transactionId) {
        Fsm fsm = fsmMap.get(transactionId);
        if (fsm == null) {
            fsm = restoreFromRedisAndDatabase(transactionId);
        }
        return fsm.getCurrentState();
    }

    private Fsm restoreFromRedisAndDatabase(String transactionId) {
        FsmEntity fsmEntity = fsmRepository.findByTransactionId(transactionId);
        if (fsmEntity == null) {
            throw new NoSuchElementException("FSM can not be found by given transaction id : " + transactionId);
        }

        Fsm fsm = createFsmAndSetCurrentStateOfFsm(fsmEntity);

        fsmEntity.setActive(true);
        fsmRepository.save(fsmEntity);
        fsmMap.put(transactionId, fsm);
        return fsm;
    }

    public Map<String, RestStateDto> findAllActiveFsm() {
        Map<String, RestStateDto> fsmList = new HashMap<>();
        for (Map.Entry<String, Fsm> entry : fsmMap.entrySet()) {
            fsmList.put(entry.getKey(), entry.getValue().getCurrentState());
        }
        return fsmList;
    }

    public Map<String, RestStateDto> findAllPassiveFsm() {
        Map<String, RestStateDto> passiveFsmList = new HashMap<>();
        fsmRepository.findAllByActive(false).forEach(fsmEntity -> {
            passiveFsmList.put(fsmEntity.getTransactionId(), createFsmAndSetCurrentStateOfFsm(fsmEntity).getCurrentState());
        });

        return passiveFsmList;
    }

    public Map<String, Fsm> getFsmMap() {
        return fsmMap;
    }

    public void fsmFailed(String transactionId) {
        Fsm fsm = fsmMap.get(transactionId);
        if (fsm == null) {
            fsm = restoreFromRedisAndDatabase(transactionId);
        }
        fsm.setCurrentStatusFailed();
        List<RestStateDto> restStateDtoList = fsm.getSuccessfulStates();
        for (RestStateDto restStateDto : restStateDtoList) {

            restClient.sendRequest(RestClientRequestDto.builder()
                    .status(Status.ACTIVE)
                    .failCount(0)
                    .restStateDto(restStateDto)
                    .build());
        }
        deleteFsm(transactionId);
    }

    private Fsm createFsmAndSetCurrentStateOfFsm(FsmEntity fsmEntity) {
        List<RestStateDto> restStateDtoList = redisClient
                .getJsonValueAsCollection(fsmEntity.getTransactionId(), new TypeReference<List<RestStateDto>>() {
                });
        if (!CollectionUtils.isEmpty(restStateDtoList)) {
            Fsm fsm = new FsmImpl();
            fsm.create(restStateDtoList);
            RestStateDto currentState = redisClient.getJsonValue(fsmEntity.getTransactionId() + CURRENT_STATE, RestStateDto.class);
            fsm.setCurrentState(currentState);
            return fsm;
        }

        return null;
    }

}

