package org.jumpmind.pos.javapos.sim.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DecimalFormatter {

    private static Map<String, Locale> localeMap;

    public static String formatCurrency(BigDecimal currencyValue, Locale locale) {
        return formatCurrency(currencyValue, locale, false, true);
    }

    private synchronized static Map<String, Locale> getLocaleMap() {

        if (localeMap == null) {
            localeMap = new HashMap<String, Locale>();
            Locale[] locales = NumberFormat.getAvailableLocales();
            for (int i = 0; i < locales.length; i++) {
                localeMap.put(locales[i].getCountry(), locales[i]);
            }
        }

        return localeMap;
    }

    public static String formatCurrency(BigDecimal currencyValue,
            Locale locale, boolean includeCurrencySymbol) {
        return formatCurrency(currencyValue, locale, includeCurrencySymbol,
                true);
    }

    public static String formatCurrency(BigDecimal currencyValue,
            String countryCode, boolean includeCurrencySymbol) {
        Locale locale = getLocaleMap().get(countryCode);

        return formatCurrency(currencyValue, locale, includeCurrencySymbol,
                true);
    }

    public static String formatCurrency(BigDecimal currencyValue,
            Locale locale, boolean includeCurrencySymbol,
            boolean includeGroupingSeparator) {

        DecimalFormat currencyFormatter = (DecimalFormat) NumberFormat
                .getCurrencyInstance(locale);

        currencyFormatter.setGroupingUsed(includeGroupingSeparator);

        if (!includeCurrencySymbol) {
            DecimalFormatSymbols symbols = currencyFormatter
                    .getDecimalFormatSymbols();
            symbols.setCurrencySymbol("");
            currencyFormatter.setDecimalFormatSymbols(symbols);
        }

        String formattedValue = currencyFormatter.format(
                currencyValue.doubleValue()).trim();

        if (currencyValue.signum() == -1 && !includeCurrencySymbol
                && locale.getISO3Country().equals("ITA")) {
            formattedValue = formattedValue.replace(" ", "");
        }

        // For U.S. currency, we don't want the default parentheses, we want a
        // minus sign
        if (currencyValue.signum() == -1
                && locale.getISO3Country().equals("USA")) {
            formattedValue = "-" + currencyValue.abs();
        }

        return formattedValue;

    }

    public static String formatDecimal(BigDecimal decimalValue, Locale locale) {

        NumberFormat decimalFormatter = NumberFormat.getNumberInstance(locale);
        String formattedValue = decimalFormatter.format(decimalValue
                .doubleValue());

        return formattedValue;

    }

    public static String formatCurrencyAsTicketReprint(
            BigDecimal currencyValue, Locale locale) {

        String formattedValue = null;

        if (locale.getISO3Country().equals("USA")
                || locale.getISO3Country().equals("CAN")) {
            formattedValue = formatCurrency(currencyValue, locale, true);
        } else {
            formattedValue = Currency.getInstance(locale).getSymbol(Locale.US)
                    + " " + formatCurrency(currencyValue, locale, false);
        }
        DecimalFormat decimalFormat = (DecimalFormat) NumberFormat
                .getCurrencyInstance(locale);
        DecimalFormatSymbols symbols = decimalFormat.getDecimalFormatSymbols();

        // Remove mantissa if it is 0
        if (currencyValue.toString().indexOf(
                symbols.getMonetaryDecimalSeparator()) != -1) {
            double mantissa = Double.parseDouble(currencyValue.toString()
                    .substring(
                            currencyValue.toString().indexOf(
                                    symbols.getMonetaryDecimalSeparator())));
            if (mantissa == 0.0d) {
                int decimalLocation = formattedValue.lastIndexOf(symbols
                        .getMonetaryDecimalSeparator());
                if (decimalLocation != -1) {
                    formattedValue = formattedValue.substring(0,
                            decimalLocation);
                }
            }
        }

        if (locale.getISO3Country().equals("CAN")) {
            formattedValue += " " + locale.getISO3Country();
        }

        return formattedValue;
    }
}
