package net.youssfi.bankaccountservice.dto.request;

public record TransferRequestDTO(
        String accountIdSource,String accountIdDestination, double amount
){
}
