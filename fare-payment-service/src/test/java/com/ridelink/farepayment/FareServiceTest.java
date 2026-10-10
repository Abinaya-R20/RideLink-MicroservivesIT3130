package com.ridelink.farepayment;

import com.ridelink.farepayment.repository.FareRepository;
import com.ridelink.farepayment.service.FareService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class FareServiceTest {

    private FareService fareService;

    @BeforeEach
    void setUp() {
        FareRepository fareRepository = Mockito.mock(FareRepository.class);
        fareService = new FareService(fareRepository);
    }

    @Test
    void shouldCalculateEstimatedFareCorrectly() {

        double result = fareService.calculateEstimatedFare(10.0);

        assertEquals(1200.0, result);
    }

    @Test
    void shouldCalculateFinalFareCorrectly() {

        double result = fareService.calculateFinalFare(9.0);

        assertEquals(1100.0, result);
    }

    @Test
    void shouldRejectZeroDistance() {

        assertThrows(
                IllegalArgumentException.class,
                () -> fareService.calculateEstimatedFare(0)
        );
    }

    @Test
    void shouldRejectNegativeDistance() {

        assertThrows(
                IllegalArgumentException.class,
                () -> fareService.calculateEstimatedFare(-5)
        );
    }
}