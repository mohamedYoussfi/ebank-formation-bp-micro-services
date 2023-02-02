package net.youssfi.paymentservice.web;

import net.youssfi.paymentservice.config.DataSourceParams;
import net.youssfi.paymentservice.config.PaymentServiceConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RefreshScope
public class ConfigRestController {
    @Value("${token.accessTokenTimeout}")
    private long accessTokenTimeout;
    @Value("${token.refreshTokenTimeout}")
    private long refreshTokenTimeout;
    @Autowired
    private PaymentServiceConfig paymentServiceConfig;

    @Autowired
    private DataSourceParams dataSourceParams;
    @GetMapping("/consulConfig")
    public Map<String, Object> consulConfig(){
        return Map.of("accessTokenTimeout",accessTokenTimeout,"refreshTokenTimeout",refreshTokenTimeout);
    }
    @GetMapping("/consulConfig2")
    public PaymentServiceConfig consulConfig2(){
        return paymentServiceConfig;
    }

    @GetMapping("/datasourceParams")
    public DataSourceParams dataSourceParams(){
        return dataSourceParams;
    }
}
