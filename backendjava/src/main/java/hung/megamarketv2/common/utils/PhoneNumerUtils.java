package hung.megamarketv2.common.utils;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;

import hung.megamarketv2.common.generic.enums.GenericEnums.CountryCode;

public class PhoneNumerUtils {
    PhoneNumberUtil phoneNumberUtil;

    public PhoneNumerUtils() {
        phoneNumberUtil = PhoneNumberUtil.getInstance();
    }

    public boolean validatePhoneNumber(String phoneNumber, CountryCode countryCode) {
        try {
            PhoneNumber number = phoneNumberUtil.parse(phoneNumber, countryCode.name());

            return phoneNumberUtil.isValidNumber(number);

        } catch (NumberParseException e) {
            return false;
        }

    }

}
