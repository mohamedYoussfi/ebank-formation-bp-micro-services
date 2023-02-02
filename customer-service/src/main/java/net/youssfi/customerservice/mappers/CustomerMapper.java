package net.youssfi.customerservice.mappers;

import net.youssfi.customerservice.dtos.CustomerRequestDTO;
import net.youssfi.customerservice.dtos.CustomerResponseDTO;
import net.youssfi.customerservice.entities.Customer;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CustomerMapper {
    private ModelMapper modelMapper=new ModelMapper();
    public CustomerResponseDTO from(Customer customer){
        return modelMapper.map(customer,CustomerResponseDTO.class);
    }
    public Customer from(CustomerRequestDTO customerRequestDTO){
        return modelMapper.map(customerRequestDTO,Customer.class);
    }
}
