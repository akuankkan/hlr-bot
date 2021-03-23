package me.shaposhnik.hlrbot.integration.bsg;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.shaposhnik.hlrbot.integration.bsg.dto.*;
import me.shaposhnik.hlrbot.integration.bsg.exception.BsgException;
import me.shaposhnik.hlrbot.integration.bsg.exception.UnknownHlrInfoResponseException;
import okhttp3.*;
import org.springframework.stereotype.Component;

import java.util.*;


@Slf4j
@Component
@RequiredArgsConstructor
public class BsgApiClient {
    private static final String ROOT_HLR_LINK = "https://app.bsg.hk/rest/hlr/";
    private static final String CREATE_HLR_LINK = ROOT_HLR_LINK + "create";
    private static final MediaType JSON = MediaType.get("application/json");
    private static final String X_API_KEY = "X-API-KEY";

    private final OkHttpClient client;
    private final ObjectMapper objectMapper;


    public <T extends Collection<HrlRequest>> MultipleHlrResponse sendHlrs(T hrlRequests, ApiKey apiKey) {
        final Request request = createHlrOkHttpRequest(hrlRequests, apiKey);

        try (Response response = client.newCall(request).execute()) {
            return Optional.ofNullable(response.body())
                .map(this::mapOkHttpResponseBodyToMultipleHlrResponseOrNull)
                .orElseThrow(BsgException::new);

        } catch (BsgException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to send HLR request!");
            throw new BsgException(e);
        }
    }

    public HlrResponse sendHlr(HrlRequest hrlRequest, ApiKey apiKey) {
        final MultipleHlrResponse multipleHlrResponse = sendHlrs(List.of(hrlRequest), apiKey);

        return Optional.ofNullable(multipleHlrResponse.getResult())
            .orElseGet(ArrayList::new)
            .stream()
            .findFirst()
            .orElseThrow(BsgException::new);
    }

    public HlrInfo getHlrInfo(String id, ApiKey apiKey) {
        Request request = new Request.Builder()
            .url(ROOT_HLR_LINK + id)
            .header(X_API_KEY, apiKey.getKey())
            .get()
            .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful() || response.body() == null) {
                final String message = String.format("Response was unsuccessful or body was null! Code: %d", response.code());
                throw new BsgException(message);
            }

            return mapOkHttpResponseBodyToHlrInfo(response.body());

        } catch (BsgException e) {
            throw e;
        } catch (Exception e) {
            log.error("Something unexpected happened! Message: {}", e.getMessage());
            throw new BsgException(e);
        }
    }

    private <T extends Collection<HrlRequest>> Request createHlrOkHttpRequest(T hrlRequests, ApiKey apiKey) {
        try {
            final String payload = objectMapper.writeValueAsString(hrlRequests);
            final RequestBody requestBody = RequestBody.create(payload, JSON);

            return new Request.Builder()
                .url(CREATE_HLR_LINK)
                .header(X_API_KEY, apiKey.getKey())
                .post(requestBody)
                .build();

        } catch (Exception e) {
            log.error("Failed to create request for HLR!");
            throw new BsgException(e);
        }
    }

    private MultipleHlrResponse mapOkHttpResponseBodyToMultipleHlrResponseOrNull(ResponseBody responseBody) {
        try {
            return objectMapper.readValue(responseBody.string(), MultipleHlrResponse.class);
        } catch (Exception e) {
            log.error("Failed to read MultipleHlrResponse from string!", e);
            return null;
        }
    }

    private HlrInfo mapOkHttpResponseBodyToHlrInfo(ResponseBody responseBody) {
        try {
            return objectMapper.readValue(responseBody.string(), HlrInfo.class);
        } catch (JsonProcessingException e) {
            final String originalResponseBody = (String) e.getLocation().getSourceRef();
            log.error("Can't deserialize to HlrInfo: ({})", originalResponseBody);

            throw new UnknownHlrInfoResponseException(originalResponseBody, e);
        } catch (Exception e) {
            log.error("Something went wrong when deserialize to HlrInfo!");
            throw new BsgException(e);
        }
    }

}
