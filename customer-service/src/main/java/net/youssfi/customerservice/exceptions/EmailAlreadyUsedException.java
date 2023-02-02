package net.youssfi.customerservice.exceptions;

public class EmailAlreadyUsedException extends Exception {
    public EmailAlreadyUsedException(String message) {
        super(message);
    }
}
