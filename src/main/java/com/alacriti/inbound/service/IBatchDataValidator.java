package com.alacriti.inbound.service;

import com.alacriti.inbound.exceptions.ACHValidationException;
import com.alacriti.inbound.util.ACHFile;

public interface IBatchDataValidator {
	void validate(ACHFile file) throws ACHValidationException;
}
