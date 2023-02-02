package net.youssfi.paymentservice;

import net.youssfi.paymentservice.config.DataSourceParams;
import net.youssfi.paymentservice.config.PaymentServiceConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.support.Versioned;

import java.util.Map;

@SpringBootApplication
@EnableConfigurationProperties({PaymentServiceConfig.class, DataSourceParams.class})
public class PaymentServiceApplication {
	@Autowired
	private VaultTemplate vaultTemplate;
	public static void main(String[] args) {
		SpringApplication.run(PaymentServiceApplication.class, args);
	}
	@Bean
	public CommandLineRunner start(){
		return args -> {
			Versioned.Metadata response = vaultTemplate.opsForVersionedKeyValue("secret")
					.put("keypair", Map.of("privateKey", "myPrivateSecret", "publicKey", "PKC"));
			System.out.println(response.toString());
		};
	}

}
