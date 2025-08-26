package com.alacriti.inbound.constants;

import java.util.Set;

import org.springframework.stereotype.Component;

@Component
public class TransactionCodes {

	// All Credit Transaction Codes
	public static final Set<String> CREDIT = Set.of("22", "23", "24", // Checking
			"32", "33", "34", // Savings
			"42", // General Ledger
			"52" // Loan
	);

	// All Debit Transaction Codes
	public static final Set<String> DEBIT = Set.of("27", "28", "29", // Checking
			"37", "38", "39", // Savings
			"47", // General Ledger
			"55" // Loan
	);
}
