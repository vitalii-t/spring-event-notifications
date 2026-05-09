package com.event.notifications.config;

import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.notifications")
public class NotificationProperties {

    private String fromAddress;
    private String fromName;
    private String mailtrapToken;
    private boolean sandbox;
    private long inboxId;
    private int digestWindowDays = 7;
    private String digestCron = "0 0 9 * * MON";
    private String digestZone = "Europe/Kiev";
    private Map<String, String> templates = new HashMap<>();

    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    public String getFromName() {
        return fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    public String getMailtrapToken() {
        return mailtrapToken;
    }

    public void setMailtrapToken(String mailtrapToken) {
        this.mailtrapToken = mailtrapToken;
    }

    public boolean isSandbox() {
        return sandbox;
    }

    public void setSandbox(boolean sandbox) {
        this.sandbox = sandbox;
    }

    public long getInboxId() {
        return inboxId;
    }

    public void setInboxId(long inboxId) {
        this.inboxId = inboxId;
    }

    public int getDigestWindowDays() {
        return digestWindowDays;
    }

    public void setDigestWindowDays(int digestWindowDays) {
        this.digestWindowDays = digestWindowDays;
    }

    public String getDigestCron() {
        return digestCron;
    }

    public void setDigestCron(String digestCron) {
        this.digestCron = digestCron;
    }

    public String getDigestZone() {
        return digestZone;
    }

    public void setDigestZone(String digestZone) {
        this.digestZone = digestZone;
    }

    public Map<String, String> getTemplates() {
        return templates;
    }

    public void setTemplates(Map<String, String> templates) {
        this.templates = templates;
    }
}
