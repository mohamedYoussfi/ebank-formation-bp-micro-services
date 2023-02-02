package net.youssfi.bankaccountservice.repository;

public record AccountStats(
        long accountsNumber,
        double totalBalance,
        double avgBalance,
        double minBalance,
        double maxBalance
) {
}
