package com.ivanrl.yaet;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that the annotated element is unused in code but used in templates.
 * An IntelliJ rule exists that avoids marking elements annotated with this as unused.
 */
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface UsedInTemplate {
}
