package com.event.notifications.service.mail;

import com.event.notifications.config.NotificationProperties;
import io.mailtrap.client.MailtrapClient;
import io.mailtrap.config.MailtrapConfig;
import io.mailtrap.factory.MailtrapClientFactory;
import io.mailtrap.model.request.emails.Address;
import io.mailtrap.model.request.emails.MailtrapMail;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class MailtrapNotificationMailer implements NotificationMailer {

    private static final Logger log = LoggerFactory.getLogger(MailtrapNotificationMailer.class);

    private final NotificationProperties properties;

    public MailtrapNotificationMailer(NotificationProperties properties) {
        this.properties = properties;
    }

    @Override
    public boolean send(NotificationCommand command) {
        String templateUuid = properties.getTemplates().get(command.notificationKey());
        if (!StringUtils.hasText(templateUuid)) {
            log.warn("Mailtrap template UUID not configured for notification {}", command.notificationKey());
            return false;
        }
        if (!StringUtils.hasText(properties.getMailtrapToken())) {
            log.warn("Mailtrap API token missing, skipping notification {}", command.notificationKey());
            return false;
        }

        try {
            MailtrapClient client = MailtrapClientFactory.createMailtrapClient(buildConfig());
            MailtrapMail mail = MailtrapMail.builder()
                    .from(new Address(properties.getFromAddress(), properties.getFromName()))
                    .to(List.of(new Address(command.recipientEmail(), command.recipientName())))
                    .templateUuid(templateUuid)
                    .templateVariables(command.variables())
                    .build();
            client.send(mail);
            log.info("Notification {} sent to {}", command.notificationKey(), command.recipientEmail());
            return true;
        } catch (Exception ex) {
            log.error("Failed to send notification {}", command.notificationKey(), ex);
            return false;
        }
    }

    private MailtrapConfig buildConfig() {
        MailtrapConfig.Builder builder = new MailtrapConfig.Builder().token(properties.getMailtrapToken());
        if (properties.isSandbox()) {
            builder.sandbox(true).inboxId(properties.getInboxId());
        }
        return builder.build();
    }
}
