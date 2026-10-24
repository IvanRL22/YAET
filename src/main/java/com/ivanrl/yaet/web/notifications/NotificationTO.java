package com.ivanrl.yaet.web.notifications;

import lombok.Getter;

@Getter
//@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class NotificationTO {

    private final String htmlId;
    private final String htmlIdSelector;
    private final String message;
    private final NotificationSeverity severity;
    private final String cssStyle;

    protected NotificationTO(String htmlId, String message, NotificationSeverity severity) {
        this.htmlId = htmlId;
        this.htmlIdSelector = '#' + this.htmlId;
        this.message = message;
        this.severity = severity;

        this.cssStyle = switch (severity) {
            case ERROR -> "bg-red-500";
            case WARNING -> "bg-yellow-500";
            case INFO -> "bg-cyan-500";
        };
    }

}
