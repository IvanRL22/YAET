package com.ivanrl.yaet.web.notifications;

import lombok.experimental.UtilityClass;

import java.util.Random;

@UtilityClass
public class NotificationGenerator {

    private static final Random RANDOM_GENERATOR = new Random();
    private static final String NOTIFICATION_HMTL_ID_PREFIX = "notif_";

    public NotificationTO error(String message) {
        return new NotificationTO(NOTIFICATION_HMTL_ID_PREFIX.concat(getRandomId()),
                                  message,
                                  NotificationSeverity.ERROR);
    }
    // 97 = a, 122 = z
    private static String getRandomId() {
        return RANDOM_GENERATOR.ints(97, 123)
                               .limit(6)
                               .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                               .toString();
    }
}
