package net.youssfi.paymentservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "token")
@Data
public class PaymentServiceConfig {
    private long accessTokenTimeout;
    private long refreshTokenTimeout;
}
