package me.shaposhnik.hlrbot.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import me.shaposhnik.hlrbot.model.enums.Ported;
import me.shaposhnik.hlrbot.model.enums.Roaming;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Map;

@Getter
@Setter
@Builder
public class Hlr {
    private String providerId;
    private String number;
    private String network;
    private String status;

    private Ported ported;
    private Roaming roaming;

    private LocalDateTime createdAt;
    private LocalDateTime statusReceivedAt;

    private Map<String, String> otherProperties;

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder()
            .append("***ProviderId:*** ").append(providerId).append('\n')
            .append("***Number:*** ").append(number).append('\n')
            .append("***Network:*** ").append(network).append('\n')
            .append("***Status:*** ").append(status).append('\n')
            .append("***Ported:*** ").append(ported).append('\n')
            .append("***Roaming:*** ").append(roaming).append('\n')
            .append("***Created at:*** ").append(createdAt).append('\n')
            .append("***Status received at:*** ").append(statusReceivedAt).append('\n')
            .append("***Other properties:*** ").append('\n');

        otherProperties.forEach((k, v) ->
            stringBuilder.append("    ***").append(k).append("***").append(": ").append(v).append('\n'));

        return stringBuilder.toString();
    }
}
