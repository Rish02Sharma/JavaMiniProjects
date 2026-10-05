package org.example.SystemDesign.CarRental.entity;

import lombok.Getter;
import lombok.Setter;
import org.example.SystemDesign.CarRental.managers.BillManager;
import org.example.SystemDesign.CarRental.managers.PaymentManager;
import org.example.SystemDesign.CarRental.managers.ReservationManager;
import org.example.SystemDesign.CarRental.managers.VehicleInventoryManager;
import org.example.SystemDesign.CarRental.strategy.BillingStrategy;
import org.example.SystemDesign.CarRental.strategy.DailyBillingStrategy;
import org.example.SystemDesign.CarRental.strategy.PaymentStrategy;
import org.example.SystemDesign.CarRental.strategy.UPIPaymentStrategy;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class Store {
    final int id;
    final Location storeLocation;
    final VehicleInventoryManager inventory;
    private final ReservationManager reservationManager;

    private final BillManager billManager;
    private final PaymentManager paymentManager;

    public Store(int storeId, Location location) {
        this.id = storeId;
        this.storeLocation = location;
        this.inventory = new VehicleInventoryManager();
        this.billManager = new BillManager(new DailyBillingStrategy(inventory)); //default
        this.paymentManager = new PaymentManager(new UPIPaymentStrategy()); //default
        this.reservationManager = new ReservationManager(inventory);

    }

    public List<Vehicle> getVehicles(Vehicle.VehicleType type, LocalDate from, LocalDate to) {
        return inventory.getAvailableVehicles(type, from, to);
    }

    // ----------------- Create Reservation -----------------
    public Reservation createReservation(int vehicleId, User user, LocalDate from, LocalDate to,
                                         Reservation.ReservationType type) throws Exception {
        return reservationManager.createReservation(vehicleId, user, from, to, type);
    }

    // ----------------- Update Reservation -----------------

    public void cancelReservation(int reservationId) {
        reservationManager.cancelReservation(reservationId);
    }

    public void startTrip(int reservationId) {
        reservationManager.startTrip(reservationId);
    }

    public void submitVehicle(int reservationId) {
        reservationManager.submitVehicle(reservationId);
    }

    // ----------------- Billing & Payment ------------------

    public Bill generateBill(int reservationId, BillingStrategy billingStrategy) {
        Reservation r = reservationManager.findByID(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        billManager.setBillingStrategy(billingStrategy);
        return billManager.generateBill(r);
    }

    public Payment makePayment(Bill bill, PaymentStrategy paymentStrategy, double paymentAmount) {

        paymentManager.setPaymentStrategy(paymentStrategy);
        Payment payment = paymentManager.makePayment(bill, paymentAmount);

        if (!bill.isBillPaid()) {
            throw new RuntimeException("Payment failed");
        }

        // NOW we can safely remove the reservation from the repo
        reservationManager.remove(bill.getReservationId());
        return payment;
    }


    public VehicleInventoryManager getInventory() {
        return inventory;
    }

    public int getStoreId() {
        return id;
    }
}
