package com.fsm.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fsm.constant.Status;
import com.fsm.dto.RestClientRequestDto;
import com.fsm.dto.RestStateDto;
import com.fsm.model.RestRequestRetryEntity;
import com.fsm.repository.RestRequestRetryRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

@Service
@AllArgsConstructor
public class RestClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestClient.class);
    private final RestTemplate restTemplate;
    private final RestRequestRetryRepository restRequestRetryRepository;
    private final ObjectMapper objectMapper;
    private final Queue<RestClientRequestDto> queue = new LinkedBlockingQueue<>();
    private final Executor executor = Executors.newFixedThreadPool(20);
    private static final Integer RETRY_COUNT = 3;


    @PostConstruct
    public void initialize() {
        List<RestRequestRetryEntity> restRequestRetryEntities = restRequestRetryRepository
                .findAllByFailCountLessThanAndStatus(RETRY_COUNT, Status.FAIL);
        restRequestRetryEntities.stream().map(this::convert).forEach(queue::offer);
    }

    public ResponseEntity<String> sendRequest(RestClientRequestDto restClientRequestDto) {
        HttpEntity<JsonNode> httpEntity = new HttpEntity<>(restClientRequestDto.getRestStateDto().getData());

        try {
            return restTemplate.exchange(restClientRequestDto.getRestStateDto().getRevertEndpoint(),
                    restClientRequestDto.getRestStateDto().getHttpMethod(), httpEntity, new ParameterizedTypeReference<String>() {
                    });
        } catch (Exception ex) {
            LOGGER.error("Rest Client send request error. Message : {}", ex.getMessage());
            restClientRequestDto.setStatus(Status.FAIL);
            restClientRequestDto.setFailCount(restClientRequestDto.getFailCount() + 1);
            restClientRequestDto.setMessage(ex.getMessage());
            RestRequestRetryEntity restRequestRetryEntity = convertAndPersist(restClientRequestDto);
            if (Objects.nonNull(restRequestRetryEntity)) {
                restClientRequestDto.setId(restRequestRetryEntity.getId());
            }
            queue.offer(restClientRequestDto);
        }
        return null;
    }

    @Scheduled(fixedRate = 1000 * 60)
    public void retryFailedRequests() {
        while (!queue.isEmpty()) {
            RestClientRequestDto restClientRequestDto = queue.poll();
            if (restClientRequestDto.getFailCount() < RETRY_COUNT) {
                executor.execute(() -> sendRequest(restClientRequestDto));
            }
        }
    }

    private RestRequestRetryEntity convertAndPersist(RestClientRequestDto restClientRequestDto) {
        RestRequestRetryEntity restRequestRetryEntity = convert(restClientRequestDto);
        if (Objects.nonNull(restRequestRetryEntity)) {
            return restRequestRetryRepository.save(restRequestRetryEntity);
        }
        return null;
    }

    private RestRequestRetryEntity convert(RestClientRequestDto restClientRequestDto) {
        try {
            String requestData = objectMapper.writeValueAsString(restClientRequestDto.getRestStateDto());
            return RestRequestRetryEntity.builder()
                    .failCount(restClientRequestDto.getFailCount())
                    .status(restClientRequestDto.getStatus())
                    .requestData(requestData)
                    .id(restClientRequestDto.getId())
                    .message(restClientRequestDto.getMessage())
                    .build();
        } catch (JsonProcessingException e) {
            LOGGER.error("rest state dto can not be converted to string");
        }
        return null;
    }

    private RestClientRequestDto convert(RestRequestRetryEntity restRequestRetryEntity) {
        try {
            return RestClientRequestDto.builder()
                    .restStateDto(objectMapper.readValue(restRequestRetryEntity.getRequestData(), RestStateDto.class))
                    .failCount(restRequestRetryEntity.getFailCount())
                    .status(restRequestRetryEntity.getStatus())
                    .id(restRequestRetryEntity.getId())
                    .message(restRequestRetryEntity.getMessage())
                    .build();
        } catch (JsonProcessingException e) {
            LOGGER.error("string can not be converted to rest state dto");
        }
        return null;
    }
}
