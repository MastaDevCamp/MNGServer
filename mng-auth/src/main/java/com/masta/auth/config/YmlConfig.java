package com.masta.auth.config;

import com.masta.auth.config.configClasses.JwtYmlConfig;
import com.masta.auth.config.configClasses.SocialYmlConfig;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
//@ConfigurationProperties(prefix="jwt")
@ConfigurationProperties("env")
@Getter
@Setter
public class YmlConfig {

    private SocialYmlConfig facebook;
    private SocialYmlConfig google;
    private JwtYmlConfig jwt;


}
