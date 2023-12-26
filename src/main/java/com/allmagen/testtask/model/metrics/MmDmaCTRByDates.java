package com.allmagen.testtask.model.metrics;


import java.time.LocalDateTime;

public interface MmDmaCTRByDates {
    LocalDateTime getIntervalStart();

    float getCtr();
}
