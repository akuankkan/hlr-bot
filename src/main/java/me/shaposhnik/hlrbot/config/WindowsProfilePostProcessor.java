package me.shaposhnik.hlrbot.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class WindowsProfilePostProcessor implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        final String os = environment.getProperty("os.name");
        final Set<String> activeProfiles = Arrays.stream(environment.getActiveProfiles()).collect(Collectors.toSet());

        if (os != null && os.matches(".*indow.*") && !activeProfiles.contains(ProfileConstants.WINDOWS)) {
            environment.addActiveProfile(ProfileConstants.WINDOWS);
            log.info("Windows OS detected. Windows profile has been set automatically.");
        }
    }

}
