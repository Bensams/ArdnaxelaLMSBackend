package com.ardnaxela.library_management_system.Services.Impl;

import com.ardnaxela.library_management_system.Borrowing.Borrowing;
import com.ardnaxela.library_management_system.Borrowing.BorrowingRepository;
import com.ardnaxela.library_management_system.DTO.BorrowingDetailsDTO;
import com.ardnaxela.library_management_system.Member.Member;
import com.ardnaxela.library_management_system.Member.MemberRepository;
import com.ardnaxela.library_management_system.Notification.NotificationEvent;
import com.ardnaxela.library_management_system.Services.EmailNotificationConsumerServices;
import com.ardnaxela.library_management_system.User.User;
import com.ardnaxela.library_management_system.User.UserRepository;
import com.ardnaxela.library_management_system.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailNotificationConsumerImpl implements EmailNotificationConsumerServices {

    private final JavaMailSender mailSender;
    private final MemberRepository memberRepository;
    private final UserRepository userRepository;
    private final BorrowingRepository borrowingRepository;

    @Override
    @RabbitListener(queues = RabbitMQConfig.EMAIL_QUEUE, containerFactory = "rabbitListenerContainerFactory")
    public void consumeEmailNotification(NotificationEvent event) {
        try {
            if ("NEW_BOOK_ARRIVAL".equals(event.getType())) {
                // Get all members who have email notifications enabled
                Iterable<Member> members = memberRepository.findAll();

                // Create a base message template
                SimpleMailMessage baseMessage = new SimpleMailMessage();
                baseMessage.setSubject("New Book Arrival: " + event.getMessage());
                baseMessage.setText(
                        "Dear Member,\n\n" +
                                "We're excited to inform you about a new book in our collection:\n\n" +
                                event.getMessage() + "\n\n" +
                                "Visit our library or website to check it out!\n\n" +
                                "Best regards,\n" +
                                "The Library Team"
                );

                // Send to each member
                for (Member member : members) {
                    try {
                        User user = member.getUser();
                        if (member.getEmail() != null && !member.getEmail().isEmpty() && user.isEmailNotificationsEnabled()) {
                            // Create a new message instance for each recipient
                            SimpleMailMessage individualMessage = new SimpleMailMessage(baseMessage);
                            individualMessage.setTo(member.getEmail());
                            mailSender.send(individualMessage);
                        }
                    } catch (Exception e) {
                        // Log error but continue with other members
                        System.err.println("Failed to send email to " + member.getEmail() + ": " + e.getMessage());
                    }
                }
                return;
            }

            Borrowing borrowing = borrowingRepository.findById(event.getBorrowingId())
                    .orElseThrow(() -> new RuntimeException("Borrowing not found"));

            if (event.isEmailPreferred() && borrowing.getMember() != null) { // For registered users
                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(borrowing.getMember().getEmail());
                message.setSubject("Library Notification -" + event.getType());
                message.setText(event.getMessage());
                mailSender.send(message);
            } else if (borrowing.getMember() != null) { // For guest users
                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(borrowing.getGuestEmail());
                message.setSubject("Library Notification -" + event.getType());
                message.setText(event.getMessage());
                mailSender.send(message);
            } else {
                System.out.println("No email sent, user has not opted for email notifications.");
            }
        } catch (Exception e) {
            System.err.println("Failed to send email: " + e.getMessage());
            throw new AmqpRejectAndDontRequeueException(e);
        }
    }

    private String getUserEmail(Long userId) {
        return memberRepository.findByUserId(userId)
                .map(Member::getEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
