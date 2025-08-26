package com.alacriti.inbound.constants;

import java.util.Set;

import org.springframework.stereotype.Component;

@Component
public class SecCodes {
	public static final Set<String> VALID = Set.of("PPD", "CCD", "CTX", "WEB", "TEL", "POP", "ARC", "BOC", "RCK");
}