package com.juvarya.nivaas.customer.annotations;

import com.juvarya.nivaas.customer.validator.MinDateValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = MinDateValidator.class)
@Target({ FIELD })
@Retention(RUNTIME)
public @interface MinDate {

    String message() default "Date must be after {value}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String value(); // The minimum date in "yyyy-MM-dd" format
}
