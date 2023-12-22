package com.allmagen.testtask.model.metrics;

import java.time.LocalDateTime;

/**
 * Represents the CTR for a specific MmDma.
 */
public interface MmDmaCTR {
    int getMmDma();

    LocalDateTime getRegTime();

    float getCtr();
}
