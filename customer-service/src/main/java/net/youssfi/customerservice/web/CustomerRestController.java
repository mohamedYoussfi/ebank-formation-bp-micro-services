package net.youssfi.customerservice.web;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.youssfi.customerservice.dtos.CustomerRequestDTO;
import net.youssfi.customerservice.dtos.CustomerResponseDTO;
import net.youssfi.customerservice.exceptions.CustomerNotFoundException;
import net.youssfi.customerservice.exceptions.EmailAlreadyUsedException;
import net.youssfi.customerservice.exceptions.ErrorMessage;
import net.youssfi.customerservice.services.CustomerService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@Slf4j @AllArgsConstructor
public class CustomerRestController {
    private CustomerService customerService;
    @GetMapping("/customers")
    public List<CustomerResponseDTO> listCustomers(){
        return customerService.listCustomers();
    }
    @GetMapping("/customers/search")
    public List<CustomerResponseDTO> searchCustomers(@RequestParam(name = "keyword", defaultValue = "") String keyword){
        return customerService.findCustomers("%"+keyword+"%");
    }
    @GetMapping("/customers/{id}")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = CustomerResponseDTO.class))),
            @ApiResponse(responseCode = "500", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
    })
    public ResponseEntity<?> getCustomerById(@PathVariable Long id){
        try {
            CustomerResponseDTO customerById = customerService.getCustomerById(id);
            return ResponseEntity.ok(customerById);
        } catch (CustomerNotFoundException e) {
            return ResponseEntity.internalServerError().body(new ErrorMessage(e.getMessage()));
        }
    }
    @PostMapping("/customers")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = CustomerResponseDTO.class))),
            @ApiResponse(responseCode = "500", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
    })
    public ResponseEntity<?> saveNewCustomer(@RequestBody CustomerRequestDTO request){
        try {
            CustomerResponseDTO customerResponseDTO = customerService.save(request);
            return ResponseEntity.ok(customerResponseDTO);
        } catch (EmailAlreadyUsedException e) {
            return ResponseEntity.internalServerError().body(new ErrorMessage(e.getMessage()));
        }
    }
    @PutMapping("/customers/{id}")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = CustomerResponseDTO.class))),
            @ApiResponse(responseCode = "500", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
    })
    public ResponseEntity<?> updateCustomer(@RequestBody CustomerRequestDTO request, @PathVariable Long id){
        try {
            CustomerResponseDTO customerResponseDTO = customerService.update(request);
            return ResponseEntity.ok(customerResponseDTO);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new ErrorMessage(e.getMessage()));
        }
    }
    @DeleteMapping("/customers/{id}")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = Void.class))),
            @ApiResponse(responseCode = "500", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
    })
    public ResponseEntity<?> deleteCustomer(@PathVariable Long id){
        try {
            customerService.deleteCustomer(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new ErrorMessage(e.getMessage()));
        }
    }

}
