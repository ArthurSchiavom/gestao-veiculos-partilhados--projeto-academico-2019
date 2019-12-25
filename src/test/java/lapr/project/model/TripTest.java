/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lapr.project.model;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Jose
 */
public class TripTest {

    /**
     * Test of getStartTime method, of class Trip.
     */
    @Test
    public void testGetStartTime() {
        System.out.println("getStartTime");

        LocalDateTime startTime = LocalDateTime.of(2019,10,9,12,10);
        LocalDateTime endTime = LocalDateTime.of(2019,10,9,12,10);

        String clientEmail = "email@email.com";
        String startParkId = "1";
        String endParkId = "2";
        String vehicleId = "1";
        Trip instance = new Trip(startTime, endTime, clientEmail, startParkId, endParkId, vehicleId);

        LocalDateTime expResult = LocalDateTime.of(2019,10,9,12,10);
        LocalDateTime result = instance.getStartTime();
        assertEquals(expResult, result);

        instance = new Trip(startTime,clientEmail,startParkId,endParkId,vehicleId);
        result = instance.getStartTime();
        assertEquals(expResult, result);
    }

    /**
     * Test of getEndTime method, of class Trip.
     */
    @Test
    public void testGetEndTime() {
        LocalDateTime startTime = LocalDateTime.of(2019,10,9,12,10);
        LocalDateTime endTime = LocalDateTime.of(2019,10,9,12,10);

        String clientEmail = "email@email.com";
        String startParkId = "1";
        String endParkId = "2";
        String vehicleId = "1";
        Trip instance = new Trip(startTime, endTime, clientEmail, startParkId, endParkId, vehicleId);

        LocalDateTime expResult = LocalDateTime.of(2019,10,9,12,10);
        LocalDateTime result = instance.getEndTime();
        assertEquals(expResult, result);
    }

    /**
     * Test of getEndTime method, of class Trip.
     */
    @Test
    public void testGetEndTime2() {
        LocalDateTime startTime = LocalDateTime.of(2019,10,9,12,10);

        String clientEmail = "email@email.com";
        String startParkId = "1";
        String endParkId = "2";
        String vehicleId = "1";
        Trip instance = new Trip(startTime, clientEmail, startParkId, vehicleId);

        LocalDateTime result = instance.getEndTime();
        assertNull(result);

    }

    /**
     * Test of getClient method, of class Trip.
     */
    @Test
    public void testGetClientId() {
        LocalDateTime startTime = LocalDateTime.of(2019,10,9,12,10);
        LocalDateTime endTime = LocalDateTime.of(2019,10,9,12,10);

        String clientEmail = "email@email.com";
        String startParkId = "1";
        String endParkId = "2";
        String vehicleId = "1";
        Trip instance = new Trip(startTime, endTime, clientEmail, startParkId, endParkId, vehicleId);

        String expResult = "email@email.com";

        String result = instance.getClientEmail();
        assertEquals(expResult, result);
    }

    /**
     * Test of getStartPark method, of class Trip.
     */
    @Test
    public void testGetStartParkId() {
        LocalDateTime startTime = LocalDateTime.of(2019,10,9,12,10);
        LocalDateTime endTime = LocalDateTime.of(2019,10,9,12,10);

        String clientEmail = "email@email.com";
        String startParkId = "1";
        String endParkId = "2";
        String vehicleId = "1";
        Trip instance = new Trip(startTime, endTime, clientEmail, startParkId, endParkId, vehicleId);

        String expResult = "1";

        String result = instance.getStartParkId();
        assertEquals(expResult, result);
    }

    /**
     * Test of getEndPark method, of class Trip.
     */
    @Test
    public void testGetEndParkId() {
        LocalDateTime startTime = LocalDateTime.of(2019,10,9,12,10);
        LocalDateTime endTime = LocalDateTime.of(2019,10,9,12,10);

        String clientEmail = "email@email.com";
        String startParkId = "1";
        String endParkId = "2";
        String vehicleId = "1";
        Trip instance = new Trip(startTime, endTime, clientEmail, startParkId, endParkId, vehicleId);

        String expResult = "2";

        String result = instance.getEndParkId();
        assertEquals(expResult, result);
    }

    @Test
    public void testGetEndParkId2() {
        LocalDateTime startTime = LocalDateTime.of(2019,10,9,12,10);

        String clientEmail = "email@email.com";
        String startParkId = "1";
        String vehicleId = "1";
        Trip instance = new Trip(startTime, clientEmail, startParkId, vehicleId);

        assertNull(instance.getEndParkId());
    }

    /**
     * Test of getVehicle method, of class Trip.
     */
    @Test
    public void testGetVehicleDescription() {
        LocalDateTime startTime = LocalDateTime.of(2019,10,9,12,10);
        LocalDateTime endTime = LocalDateTime.of(2019,10,9,12,10);

        String clientEmail = "email@email.com";
        String startParkId = "1";
        String endParkId = "2";
        String vehicleId = "1";
        Trip instance = new Trip(startTime, endTime, clientEmail, startParkId, endParkId, vehicleId);

        String expResult = "1";

        String result = instance.getVehicleDescription();
        assertEquals(expResult, result);
    }

    @Test
    void testEquals() {
        LocalDateTime startTime = LocalDateTime.of(2019,10,9,12,10);

        Object trip1 = new Trip(startTime, "email@email.com", "0","1");
        assertEquals(trip1, trip1);

        Trip trip2 = new Trip(startTime, "email@email.com", "0","1");
        assertEquals(trip1, trip2);

        trip1 = new Trip(startTime, "email2@email.com", "0","1");
        assertNotEquals(trip1, trip2);

        trip1 = null;
        assertNotEquals(trip2, trip1);
    }

    @Test
    void testHashCode() {
        LocalDateTime startTime = LocalDateTime.of(2019,10,9,12,10);

        Trip trip1 = new Trip(startTime, "email@email.com", "0","1");
        Trip trip2 = new Trip(startTime, "email@email.com","0","1");
        int expResult = trip1.hashCode();
        assertEquals(expResult, trip2.hashCode());

        trip1 = new Trip(startTime, "email2@email.com", "0","1");
        assertNotEquals(expResult, trip1.hashCode());
    }
}
