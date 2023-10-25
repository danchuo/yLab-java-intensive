package org.wallet.domain.dto.request;

/**
 * The {@code Validator} interface defines a contract for validating objects. Classes implementing this interface
 * must provide an {@code isValid} method that checks the validity of an object.
 */
@FunctionalInterface
public interface Validator {
    /**
     * Checks the validity of an object.
     *
     * @return {@code true} if the object is considered valid; otherwise, {@code false}.
     */
    boolean isValid();
}
