package es.uma.Metrics.Specific;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import es.uma.Utils;
import es.uma.Metrics.IMetrics;
import es.uma.Metrics.Utilities;

public class HotelManagement implements IMetrics {

    private int validCheckInDate, validCheckOutDate; 
    private int totalDates;

    public HotelManagement() {
        validCheckInDate = 0;
        validCheckOutDate = 0;
        totalDates = 0;
    }

    private static Map<String, Map<String, String>> getBookings(String instance) {
        Map<String, Map<String, String>> bookings = new HashMap<>();
        Pattern pattern = Pattern.compile("!\\s*(\\w+)\\.(startDate|endDate)\\s*:=\\s*'([^']+)'");
        Matcher matcher = pattern.matcher(instance);

        while (matcher.find()) {
            String reservation = matcher.group(1);
            String dateType = matcher.group(2);
            String date = matcher.group(3);
            bookings.putIfAbsent(reservation, new HashMap<>());
            bookings.get(reservation).put(dateType, date);
        }

        return bookings;
    }

    private static Map<String, Map<String, String>> getReservations(String instance) {
        Map<String, Map<String, String>> reservations = new HashMap<>();
        Pattern pattern = Pattern.compile("!\\s*(\\w+)\\.(checkInDate|checkOutDate)\\s*:=\\s*'([^']+)'");
        Matcher matcher = pattern.matcher(instance);

        while (matcher.find()) {
            String reservation = matcher.group(1);
            String dateType = matcher.group(2);
            String date = matcher.group(3);
            reservations.putIfAbsent(reservation, new HashMap<>());
            reservations.get(reservation).put(dateType, date);
        }

        return reservations;
    }

    private static Map<String, String> getPairs(String instance) {
        Map<String, String> pairs = new HashMap<>();

        Pattern pattern = Pattern.compile("!\\s*insert\\s*\\(\\s*(\\w+)\\s*,\\s*(\\w+)\\s*\\)\\s*into\\s*BookingRoomReservation");
        Matcher matcher = pattern.matcher(instance);

        while (matcher.find()) {
            String bookingId = matcher.group(1);
            String reservationId = matcher.group(2);
            pairs.put(bookingId, reservationId);
        }

        return pairs;
    }
    

    @Override
    public void calculate(String diagramPath, String instancePath) {

        String instance = Utils.readFile(instancePath);
        Map<String, String> pairs = getPairs(instance);
        Map<String, Map<String, String>> bookings = getBookings(instance);
        Map<String, Map<String, String>> reservations = getReservations(instance);

        System.out.println("Bookings: " + bookings);
        System.out.println("Reservations: " + reservations);
        System.out.println("Pairs: " + pairs);

        if (pairs.isEmpty() || bookings.isEmpty() || reservations.isEmpty()) {
            return;
        }

        pairs.forEach((bookingId, reservationId) -> {
            
            Map<String, String> booking = bookings.get(bookingId);
            Map<String, String> reservation = reservations.get(reservationId);
            String startDate = booking.get("startDate");
            String endDate = booking.get("endDate");
            String checkInDate = reservation.get("checkInDate");
            String checkOutDate = reservation.get("checkOutDate");
            
            if (startDate != null && endDate != null && checkInDate != null && checkOutDate != null) {
                totalDates++;
                if (checkInDate.compareTo(startDate) >= 0) {
                    validCheckInDate++;
                }
                if (checkOutDate.compareTo(endDate) <= 0) {
                    validCheckOutDate++;
                }
            }

        });        

    }

    @Override
    public void calculateInvalid(String diagramPath, String instancePath) {
        calculate(diagramPath, instancePath);
    }

    @Override
    public void aggregate(IMetrics otherMetrics) {
        if (!(otherMetrics instanceof HotelManagement)) {
            return;
        }
        
        HotelManagement other = (HotelManagement) otherMetrics;
        this.validCheckInDate += other.validCheckInDate;
        this.validCheckOutDate += other.validCheckOutDate;
        this.totalDates += other.totalDates;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("| HotelManagement | Valid | Total | Success (%) | \n");
        sb.append("|---|---|---|---| \n");
        sb.append(Utilities.formatMetricRow("checkInDate >= startDate", validCheckInDate, totalDates))
          .append(Utilities.formatMetricRow("checkOutDate <= endDate", validCheckOutDate, totalDates));
        return sb.toString();
    }

    // Main for testing purposes
    public static void main(String[] args) {
        String instancePath = "./src/main/resources/instances/CoT/hotelmanagement/GEMINI_2_FLASH_LITE/17-03-2025--23-52-07/gen1/baseline.soil";
        HotelManagement hotelManagement = new HotelManagement();
        hotelManagement.calculate("diagramPath", instancePath);
        System.out.println(hotelManagement);
    }
    
}
