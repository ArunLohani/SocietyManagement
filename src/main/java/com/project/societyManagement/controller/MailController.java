package com.project.societyManagement.controller;

import com.project.societyManagement.dto.Api.ApiResponse;
import com.project.societyManagement.dto.MailDTO.CustomMailDTO;
import com.project.societyManagement.entity.Flat;
import com.project.societyManagement.entity.FlatMember;
import com.project.societyManagement.entity.User;
import com.project.societyManagement.entity.types.FlatMembershipType;
import com.project.societyManagement.service.EmailService;
import com.project.societyManagement.service.FlatService;
import com.project.societyManagement.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/mail")
@RequiredArgsConstructor
public class MailController {

    private final EmailService emailService;
    private final NotificationService notificationService;
    private final FlatService flatService;


    @GetMapping("/maintenance/pending/{flatId}")
    public ResponseEntity<ApiResponse<String>> sendPendingMaintenanceReminder(
            @PathVariable Long flatId) {

        Flat flat = flatService.getFlatById(flatId);

        flat.getMembers().stream()
                .filter(m -> m.getType() == FlatMembershipType.OWNER)
                .forEach(owner -> {

                    String subject =
                            "Pending Maintenance Payment – Flat " +
                                    flat.getBlock() + "-" + flat.getNumber();

                    String body =
                            "Dear " + owner.getUser().getName() + ",\n\n" +
                                    "This is a reminder that the maintenance payment for your flat " +
                                    "(Block " + flat.getBlock() + ", Flat " + flat.getNumber() + ") " +
                                    "is currently pending.\n\n" +
                                    "Please complete the payment at the earliest to avoid late fees.\n\n" +
                                    "Regards,\nSociety Management";

                    emailService.sendSimpleEmail(
                            owner.getUser().getEmail(),
                            subject,
                            body
                    );

                    notificationService.notifyUser(
                            owner.getUser().getId(),
                            "Maintenance payment pending",
                            "Please pay your pending maintenance dues."
                    );
                });

        return ResponseEntity.ok(
                new ApiResponse<>(true,
                        "Pending maintenance reminders sent successfully",
                        "SUCCESS")
        );
    }


    @PostMapping("/notice/{flatId}")
    public ResponseEntity<ApiResponse<String>> sendSocietyNotice(
            @PathVariable Long flatId,
            @RequestBody String message) {

        Flat flat = flatService.getFlatById(flatId);

        flat.getMembers().stream()
//                .filter(m -> m.getType() == FlatMembershipType.OWNER)
                .forEach(owner -> {

                    String subject = "Important Notice from Society Management";

                    String body =
                            "Dear " + owner.getUser().getName() + ",\n\n" +
                                    message + "\n\n" +
                                    "Regards,\nSociety Management";

                    emailService.sendSimpleEmail(
                            owner.getUser().getEmail(),
                            subject,
                            body
                    );

                    notificationService.notifyUser(
                            owner.getUser().getId(),
                            "New society notice",
                            "You have received a new notice from management."
                    );
                });

        return ResponseEntity.ok(
                new ApiResponse<>(true,
                        "Notice sent successfully",
                        "SUCCESS")
        );
    }


    @PostMapping("/emergency/{flatId}")
    public ResponseEntity<ApiResponse<String>> sendEmergencyAlert(
            @PathVariable Long flatId,
                @RequestBody String emergencyMessage) {

        Flat flat = flatService.getFlatById(flatId);

        flat.getMembers().forEach(member -> {

            User user = member.getUser();

            String subject = "🚨 Emergency Alert – Immediate Attention Required";

            String body =
                    "Dear " + user.getName() + ",\n\n" +
                            emergencyMessage + "\n\n" +
                            "Please take necessary precautions.\n\n" +
                            "Society Management";

            emailService.sendSimpleEmail(
                    user.getEmail(),
                    subject,
                    body
            );

            notificationService.notifyUser(
                    user.getId(),
                    "Emergency alert",
                    emergencyMessage
            );
        });

        return ResponseEntity.ok(
                new ApiResponse<>(true,
                        "Emergency alert sent successfully",
                        "SUCCESS")
        );
    }
}
