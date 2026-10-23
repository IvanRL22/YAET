package com.ivanrl.yaet.web.notifications;

import lombok.Getter;

@Getter
//@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class NotificationTO {

    private final String htmlId;
    private final String htmlIdSelector;
    private final String message;
    private final NotificationSeverity severity;

    protected NotificationTO(String htmlId, String message, NotificationSeverity severity) {
        this.htmlId = htmlId;
        this.htmlIdSelector = '#' + this.htmlId;
        this.message = message;
        this.severity = severity;
    }

}
