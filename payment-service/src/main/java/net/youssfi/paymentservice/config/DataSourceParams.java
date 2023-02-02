package net.youssfi.paymentservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.datasource")
@Data
public class DataSourceParams {
    private String url;
    private String username;
    private String password;
}
