package com.ivanrl.yaet.web.notifications;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;
import java.util.Random;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NotificationGenerator {

    private static final String VIEW_NAME = "notification";
    private static final String NOTIFICATION_INSTANCE = "notification";
    private static final Random RANDOM_GENERATOR = new Random();
    private static final String NOTIFICATION_HMTL_ID_PREFIX = "notif_";

    public static ModelAndView error(String message) {
        NotificationTO errorNotification = new NotificationTO(NOTIFICATION_HMTL_ID_PREFIX.concat(getRandomId()),
                                                              message,
                                                              NotificationSeverity.ERROR);
        return new ModelAndView(VIEW_NAME, Map.of(NOTIFICATION_INSTANCE, errorNotification));
    }

    public static ModelAndView warning(String message) {
        NotificationTO infoNotification = new NotificationTO(NOTIFICATION_HMTL_ID_PREFIX.concat(getRandomId()),
                                                             message,
                                                             NotificationSeverity.WARNING);
        return new ModelAndView(VIEW_NAME, Map.of(NOTIFICATION_INSTANCE, infoNotification));
    }

    public static ModelAndView info(String message) {
        NotificationTO infoNotification = new NotificationTO(NOTIFICATION_HMTL_ID_PREFIX.concat(getRandomId()),
                                                             message,
                                                             NotificationSeverity.INFO);
        return new ModelAndView(VIEW_NAME, Map.of(NOTIFICATION_INSTANCE, infoNotification));
    }

    // 97 = a, 122 = z
    private static String getRandomId() {
        return RANDOM_GENERATOR.ints(97, 123)
                               .limit(6)
                               .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                               .toString();
    }
}
