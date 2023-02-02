package net.youssfi.bankaccountservice.feign;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import net.youssfi.bankaccountservice.dto.response.CustomerResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "CUSTOMER-SERVICE")
public interface CustomerRestClient {
    @GetMapping(path = "/customers/{id}")
    @CircuitBreaker(name = "customerService",fallbackMethod = "getDefaultCustomer")
    CustomerResponseDTO findCustomerById(@PathVariable Long id);
    @GetMapping("/customers/search")
    @Retry(name = "retrySearchCustomers", fallbackMethod = "getDefaultCustomers")
    List<CustomerResponseDTO> searchCustomers(@RequestParam(name = "keyword") String keyword);

    default CustomerResponseDTO getDefaultCustomer(Long id,Exception e){
        return CustomerResponseDTO.builder()
                .id(id).email("noname@gmail.com")
                .firstName("defaultFirstName for => "+id)
                .lastName("defaultLast for => "+id)
                .build();
    }
    default List<CustomerResponseDTO> getDefaultCustomers(){
        return List.of();
    }
}
