package net.youssfi.gatewayservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.ReactiveDiscoveryClient;
import org.springframework.cloud.gateway.discovery.DiscoveryClientRouteDefinitionLocator;
import org.springframework.cloud.gateway.discovery.DiscoveryLocatorProperties;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class GatewayServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayServiceApplication.class, args);
    }

    //@Bean
    RouteLocator staticRoutes(RouteLocatorBuilder builder){
        return builder.routes()
                .route(r->r.path("/customers/**").uri("http://localhost:8089/"))
                .route(r->r.path("/accounts/**").uri("http://localhost:8087/"))
                .build();
    }
    //@Bean
    RouteLocator staticRoutesWithDiscovery(RouteLocatorBuilder builder){
        return builder.routes()
                .route(r->r.path("/customers/**").uri("lb://CUSTOMER-SERVICE"))
                .route(r->r.path("/accounts/**").uri("lb://ACCOUNT-SERVICE"))
                .build();
    }
    @Bean
    DiscoveryClientRouteDefinitionLocator dynamicRoutes(ReactiveDiscoveryClient rdc, DiscoveryLocatorProperties dlp){
        return new DiscoveryClientRouteDefinitionLocator(rdc,dlp);
    }

}
