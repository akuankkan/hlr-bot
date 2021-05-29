package me.shaposhnik.hlrbot.integration.bsg.converter;

import me.shaposhnik.hlrbot.integration.bsg.dto.HlrInfo;
import me.shaposhnik.hlrbot.model.Hlr;
import me.shaposhnik.hlrbot.model.enums.Ported;
import me.shaposhnik.hlrbot.model.enums.Roaming;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

@Component
public class HlrInfoToHlrConverter implements Converter<HlrInfo, Hlr> {

    @Override
    public Hlr convert(@NonNull HlrInfo hlrInfo) {
        final Ported ported = Optional.ofNullable(hlrInfo.getDetails())
            .map(HlrInfo.Details::getPorted)
            .map(status -> status.equals("1") ? Ported.YES : Ported.NO)
            .orElse(Ported.UNKNOWN);

        final Roaming roaming = Optional.ofNullable(hlrInfo.getDetails())
            .map(HlrInfo.Details::getRoaming)
            .map(status -> status.equals("1") ? Roaming.YES : Roaming.NO)
            .orElse(Roaming.UNKNOWN);

        final Map<String, String> details = Optional.ofNullable(hlrInfo.getDetails())
            .map(HlrInfo.Details::getOtherDetails)
            .orElseGet(Map::of);

        return Hlr.builder()
            .providerId(hlrInfo.getId())
            .number(hlrInfo.getMsisdn())
            .network(hlrInfo.getNetwork())
            .status(hlrInfo.getStatus())
            .ported(ported)
            .roaming(roaming)
            .details(details)
            .createdAt(hlrInfo.getCreatedDatetime())
            .statusReceivedAt(hlrInfo.getStatusDatetime())
            .otherProperties(hlrInfo.getOtherProperties())
            .build();
    }
}
