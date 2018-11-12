package io.ruck.clop;

/**
 *
 * @author ruckc
 */
@FunctionalInterface
public interface Validator<T> {
    boolean isValid(T value);
}
