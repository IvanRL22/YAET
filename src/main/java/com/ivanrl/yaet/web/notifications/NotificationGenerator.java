package com.ivanrl.yaet.web.notifications;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;
import java.util.Random;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NotificationGenerator {

    private static final Random RANDOM_GENERATOR = new Random();
    private static final String NOTIFICATION_HMTL_ID_PREFIX = "notif_";

    public static ModelAndView error(String message) {
        NotificationTO errorNotification = new NotificationTO(NOTIFICATION_HMTL_ID_PREFIX.concat(getRandomId()),
                                                              message,
                                                              NotificationSeverity.ERROR);
        return new ModelAndView("notification", Map.of("notification", errorNotification));
    }

    public static ModelAndView info(String message) {
        NotificationTO infoNotification = new NotificationTO(NOTIFICATION_HMTL_ID_PREFIX.concat(getRandomId()),
                                                             message,
                                                             NotificationSeverity.INFO);
        return new ModelAndView("notification", Map.of("notification", infoNotification));
    }

    // 97 = a, 122 = z
    private static String getRandomId() {
        return RANDOM_GENERATOR.ints(97, 123)
                               .limit(6)
                               .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                               .toString();
    }
}
