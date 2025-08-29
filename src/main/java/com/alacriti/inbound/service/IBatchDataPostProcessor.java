package com.alacriti.inbound.service;

import com.alacriti.inbound.util.ACHFile;

public interface IBatchDataPostProcessor {
 void postProcess(ACHFile achFile);
}
