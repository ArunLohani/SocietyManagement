// ==================== UPDATED SCHEDULER ====================

package com.project.societyManagement.scheduler;

import com.project.societyManagement.entity.Flat;
import com.project.societyManagement.entity.FlatMember;
import com.project.societyManagement.entity.MaintenancePayment;
import com.project.societyManagement.entity.types.FlatMembershipType;
import com.project.societyManagement.queryBuilder.flat.FlatFilter;
import com.project.societyManagement.queryBuilder.maintenancePayment.MaintenancePaymentFilter;
import com.project.societyManagement.service.FlatService;
import com.project.societyManagement.service.MaintenancePaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentReminderScheduler {

    private final MaintenancePaymentService maintenancePaymentService;
    private final FlatService flatService;


    @Scheduled(cron = "0 0 9 1 * ?")
    public void sendMonthlyPaymentReminders() {
        log.info("Starting monthly payment reminder process");

        List<Flat> allFlats = flatService.searchFlat(new FlatFilter());
        LocalDate today = LocalDate.now();
        int remindersSent = 0;

        for (Flat flat : allFlats) {
            try {
                // Search for active payments (payments that haven't expired yet)
                MaintenancePaymentFilter filter = new MaintenancePaymentFilter();
                filter.setFlat(flat.getId());
                filter.setStatus("COMPLETED");
                filter.setActivePaymentDate(today); // This checks billingEndDate >= today

                List<MaintenancePayment> activePayments = maintenancePaymentService
                        .searchMaintenancePayment(filter);


                if (activePayments.isEmpty()) {
                    log.info("No active payment found for flat: {} (Block: {}, Number: {})",
                            flat.getId(), flat.getBlock(), flat.getNumber());
                    sendPaymentReminderToFlatOwners(flat);
                    remindersSent++;
                } else {
                    log.debug("Active payment exists for flat: {} until {}",
                            flat.getId(), activePayments.get(0).getBillingEndDate());
                }
            } catch (Exception e) {
                log.error("Error processing payment reminder for flat: {}", flat.getId(), e);
            }
        }

        log.info("Monthly payment reminder process completed. Reminders sent: {}", remindersSent);
    }

    @Scheduled(cron = "0 0 9 * * ?")
    public void sendExpiringPaymentReminders() {
        log.info("Checking for expiring payments");


        MaintenancePaymentFilter filter = new MaintenancePaymentFilter();
        filter.setStatus("COMPLETED");
        filter.setIsExpiringSoon(true);
        filter.setDaysUntilExpiry(7); // Payments expiring in next 7 days

        List<MaintenancePayment> expiringPayments = maintenancePaymentService
                .searchMaintenancePayment(filter);

        log.info("Found {} payments expiring in next 7 days", expiringPayments.size());

        for (MaintenancePayment payment : expiringPayments) {
            try {
                log.info("Sending expiration reminder for payment: {} (Flat: {}, Expires: {})",
                        payment.getId(), payment.getFlat().getId(), payment.getBillingEndDate());
                sendExpirationReminderToFlatOwners(payment.getFlat(), payment);
            } catch (Exception e) {
                log.error("Error sending expiration reminder for payment: {}",
                        payment.getId(), e);
            }
        }

        log.info("Expiring payment reminders sent for {} payments", expiringPayments.size());
    }

    /**
     * Send payment reminder to all owners of a flat
     */
    private void sendPaymentReminderToFlatOwners(Flat flat) {
        List<FlatMember> owners = flat.getMembers().stream()
                .filter(m -> m.getType() == FlatMembershipType.OWNER && m.getIsActive())
                .toList();

        if (owners.isEmpty()) {
            log.warn("No active owners found for flat: {} (Block: {}, Number: {})",
                    flat.getId(), flat.getBlock(), flat.getNumber());
            return;
        }

        for (FlatMember owner : owners) {
            try {
                log.info("Sending payment reminder to user: {} for flat: {}",
                        owner.getUser().getId(), flat.getId());
                maintenancePaymentService.sendPaymentReminder(owner.getUser());
            } catch (Exception e) {
                log.error("Error sending payment reminder to user: {} for flat: {}",
                        owner.getUser().getId(), flat.getId(), e);
            }
        }
    }

    /**
     * Send expiration reminder to all owners of a flat
     */
    private void sendExpirationReminderToFlatOwners(Flat flat, MaintenancePayment payment) {
        List<FlatMember> owners = flat.getMembers().stream()
                .filter(m -> m.getType() == FlatMembershipType.OWNER && m.getIsActive())
                .toList();

        if (owners.isEmpty()) {
            log.warn("No active owners found for flat: {} (Block: {}, Number: {})",
                    flat.getId(), flat.getBlock(), flat.getNumber());
            return;
        }

        for (FlatMember owner : owners) {
            try {
                log.info("Sending expiration reminder to user: {} for payment: {} (Expires: {})",
                        owner.getUser().getId(), payment.getId(), payment.getBillingEndDate());
                maintenancePaymentService.sendPaymentExpirationReminder(owner.getUser());
            } catch (Exception e) {
                log.error("Error sending expiration reminder to user: {} for payment: {}",
                        owner.getUser().getId(), payment.getId(), e);
            }
        }
    }


}