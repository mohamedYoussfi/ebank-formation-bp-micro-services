package net.youssfi.customerservice.services;

import net.youssfi.customerservice.dtos.CustomerRequestDTO;
import net.youssfi.customerservice.dtos.CustomerResponseDTO;
import net.youssfi.customerservice.entities.Customer;
import net.youssfi.customerservice.exceptions.CustomerNotFoundException;
import net.youssfi.customerservice.exceptions.EmailAlreadyUsedException;

import java.util.List;

public interface CustomerService {
    CustomerResponseDTO save(CustomerRequestDTO request) throws EmailAlreadyUsedException;
    List<CustomerResponseDTO> listCustomers();
    CustomerResponseDTO getCustomerById(Long id) throws CustomerNotFoundException;
    CustomerResponseDTO update(CustomerRequestDTO requestDTO) throws CustomerNotFoundException, EmailAlreadyUsedException;
    void deleteCustomer(Long id) throws CustomerNotFoundException;
    List<CustomerResponseDTO> findCustomers(String keyWord);
}
