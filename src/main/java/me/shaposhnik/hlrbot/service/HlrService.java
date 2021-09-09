package me.shaposhnik.hlrbot.service;

import me.shaposhnik.hlrbot.model.Hlr;
import me.shaposhnik.hlrbot.model.Phone;
import me.shaposhnik.hlrbot.model.SentHlr;

import java.util.Collection;
import java.util.List;

public interface HlrService {

    SentHlr sendHlr(Phone phone, String token);

    <T extends Collection<Phone>> List<SentHlr> sendHlrs(T phones, String token);

    Hlr getHlrInfo(SentHlr sentHlr, String token);

    Hlr getHlrInfoByProviderId(String providerId, String token);

}
