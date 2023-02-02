package net.youssfi.customerservice.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.youssfi.customerservice.dtos.CustomerRequestDTO;
import net.youssfi.customerservice.dtos.CustomerResponseDTO;
import net.youssfi.customerservice.entities.Customer;
import net.youssfi.customerservice.exceptions.CustomerNotFoundException;
import net.youssfi.customerservice.exceptions.EmailAlreadyUsedException;
import net.youssfi.customerservice.mappers.CustomerMapper;
import net.youssfi.customerservice.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;

@Service @Transactional
@Slf4j @AllArgsConstructor
public class CustomerServiceImpl implements CustomerService {
    private CustomerRepository customerRepository;
    private CustomerMapper customerMapper;
    @Override
    public CustomerResponseDTO save(CustomerRequestDTO request) throws EmailAlreadyUsedException {
        if (customerRepository.checkIfEmailExists(request.getEmail()))
            throw new EmailAlreadyUsedException(String.format("This email %s is already used",request.getEmail()));
        Customer customer=customerMapper.from(request);
        Customer savedCustomer = customerRepository.save(customer);
        return customerMapper.from(savedCustomer);
    }

    @Override
    public List<CustomerResponseDTO> listCustomers() {
        return customerRepository.findAll().stream().map(customerMapper::from).collect(Collectors.toList());
    }

    @Override
    public CustomerResponseDTO getCustomerById(Long id) throws CustomerNotFoundException {
        Customer customer = customerRepository.findById(id).orElse(null);
        if (customer==null) throw new CustomerNotFoundException(String.format("Customer Not Found :%s",id));
        return customerMapper.from(customer);
    }

    @Override
    public CustomerResponseDTO update(CustomerRequestDTO requestDTO) throws CustomerNotFoundException, EmailAlreadyUsedException {
        if (customerRepository.checkIfEmailExists(requestDTO.getEmail()))
            throw new EmailAlreadyUsedException(String.format("This email %s is already used",requestDTO.getEmail()));
        Customer customer = customerRepository.findById(requestDTO.getId()).orElse(null);
        if (customer==null) throw new CustomerNotFoundException(String.format("Customer Not Found :%s",requestDTO.getId()));
        if(requestDTO.getEmail()!=null) customer.setFirstName(requestDTO.getFirstName());
        if(requestDTO.getLastName()!=null) customer.setLastName(requestDTO.getLastName());
        if(requestDTO.getEmail()!=null) customer.setEmail(requestDTO.getEmail());
        Customer savedCustomer = customerRepository.save(customer);
        return customerMapper.from(savedCustomer);
    }

    @Override
    public void deleteCustomer(Long id) throws CustomerNotFoundException {
        Customer customer = customerRepository.findById(id).orElse(null);
        if (customer==null) throw new CustomerNotFoundException(String.format("Customer Not Found :%s",id));
        customerRepository.deleteById(id);
    }

    @Override
    public List<CustomerResponseDTO> findCustomers(String keyWord) {
        List<Customer> customers = customerRepository.searchCustomers(keyWord);
        return customers.stream().map(customerMapper::from).collect(Collectors.toList());
    }
    @PostConstruct
    public void populateData() throws EmailAlreadyUsedException {
        for (int i = 1; i <= 5; i++) {
            CustomerRequestDTO customerRequestDTO=new CustomerRequestDTO(null,"First Name "+i,"Last Name "+i,"email"+i+"@gmail.com");
            CustomerResponseDTO customerResponseDTO = save(customerRequestDTO);
        }
    }
}
