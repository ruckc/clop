package io.ruck.clop;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 *
 * @author ruckc
 */
public class InetAddressOption extends AbstractOption<InetAddress> {

    public InetAddressOption(String description, String shortName, String longName) {
        super(InetAddress.class, OptionType.SINGLE_VALUE, description, shortName, longName);
    }

    @Override
    public InetAddress parseValue(String value) {
        try {
            return InetAddress.getByName(value);
        } catch (UnknownHostException ex) {
            throw new RuntimeException("Cannot parse value: " + value, ex);
        }
    }
}
