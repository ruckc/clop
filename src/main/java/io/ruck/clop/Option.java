package io.ruck.clop;

import java.util.Optional;

/**
 *
 * @author ruckc
 */
public interface Option<T> {

    default Optional<String> getShortName() {
        return Optional.empty();
    }

    default Optional<String> getLongName() {
        return Optional.empty();
    }
    
    OptionType getType();

    String getDescription();

    Class<T> getValuetype();

    T getValue();

    void setValue(T value);
    
    void setDefaultValue(T defaultValue);
    
    T parseValue(String value);
    
    void parseAndSetValue(String value);
    
    void addValidator(Validator<T> v);
}
