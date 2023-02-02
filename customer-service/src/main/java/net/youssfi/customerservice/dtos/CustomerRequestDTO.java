package net.youssfi.customerservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class CustomerRequestDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
}
