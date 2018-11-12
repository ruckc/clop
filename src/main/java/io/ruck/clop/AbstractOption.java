package io.ruck.clop;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author ruckc
 */
public abstract class AbstractOption<T> implements Option<T> {

    private final List<Validator<T>> validators = new ArrayList<>();
    private final OptionType ot;
    private final String description;
    private final Class<T> cls;
    private T defaultValue = null;
    private T value = null;
    private final Optional<String> shortName;
    private final Optional<String> longName;

    public AbstractOption(Class<T> cls, OptionType ot, String description, String shortName, String longName) {
        this.description = description;
        this.ot = ot;
        this.cls = cls;
        this.shortName = Optional.ofNullable(shortName);
        this.longName = Optional.ofNullable(longName);
    }

    @Override
    public OptionType getType() {
        return ot;
    }

    @Override
    public Optional<String> getShortName() {
        return shortName;
    }

    @Override
    public Optional<String> getLongName() {
        return longName;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public Class<T> getValuetype() {
        return cls;
    }

    @Override
    public T getValue() {
        if (value == null) {
            return defaultValue;
        } else {
            return value;
        }
    }

    @Override
    public void setValue(T value) {
        this.value = value;
    }

    @Override
    public void setDefaultValue(T defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Override
    public void addValidator(Validator<T> v) {
        this.validators.add(v);
    }

    @Override
    public void parseAndSetValue(String value) {
        T parsed = parseValue(value);
        if (validators.stream().allMatch(v -> v.isValid(parsed))) {
            setValue(parsed);
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + shortName.orElse("") + " " + longName.orElse("") + " " + value + " " + defaultValue + "]";
    }

}
