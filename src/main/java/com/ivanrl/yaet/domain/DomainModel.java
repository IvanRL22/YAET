package com.ivanrl.yaet.domain;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Marker for domain objects
 */
@Target({ElementType.TYPE})
public @interface DomainModel {
}
