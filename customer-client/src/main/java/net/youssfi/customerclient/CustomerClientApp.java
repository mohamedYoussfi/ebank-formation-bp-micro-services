package net.youssfi.customerclient;

import com.google.gson.Gson;
import org.openapitools.client.ApiException;
import org.openapitools.client.api.CustomerRestControllerApi;
import org.openapitools.client.model.CustomerRequestDTO;
import org.openapitools.client.model.CustomerResponseDTO;
import org.openapitools.client.model.ErrorMessage;
import java.util.List;
public class CustomerClientApp {
    public static void main(String[] args)  {
        Gson gson=new Gson();
        try {
            CustomerRestControllerApi api=new CustomerRestControllerApi();
            List<CustomerResponseDTO> customerResponseDTOS = api.listCustomers();
            customerResponseDTOS.forEach(c->{
                System.out.println(c.getFirstName());
            });
            System.out.println("*******************");
            List<CustomerResponseDTO> customerResponseDTOS1 = api.searchCustomers("@gmail");
            System.out.println(customerResponseDTOS1);
            System.out.println("---------------");
            CustomerResponseDTO customerById = api.getCustomerById(1L);
            System.out.println(customerById);
            System.out.println("--------------");
            CustomerRequestDTO requestDTO=new CustomerRequestDTO();
            requestDTO.setFirstName("Yassine");
            requestDTO.setLastName("Nassimi");
            requestDTO.setEmail("yassine@gmail.com");
            CustomerResponseDTO customerResponseDTO = api.saveNewCustomer(requestDTO);
            System.out.println(customerResponseDTO);
            System.out.println("--------------");
            api.deleteCustomer(1L);
        } catch (ApiException e) {
            String responseBody = e.getResponseBody();
            ErrorMessage errorMessage = gson.fromJson(responseBody, ErrorMessage.class);
            System.out.println(errorMessage.getErrorMessage());
        }


    }
}
