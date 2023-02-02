package net.youssfi.bankaccountservice.exception;

public class TransferRejectedException extends Exception {
    public TransferRejectedException(String message) {
        super(message);
    }
}
