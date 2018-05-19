package ro.polak.http.protocol.parser.impl;

import org.junit.Test;

import java.util.List;
import java.util.Locale;

import ro.polak.http.protocol.parser.MalformedInputException;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class LocaleParserTest {

    private static final LocaleParser LOCALE_PARSER = new LocaleParser();

    @Test
    public void shouldParseLocales() throws MalformedInputException {
        String localeString = "pl-PL,pl;q=0.8,en;q=0.4,ro;q=0.2,ru;q=0.2";
        List<Locale> locales = LOCALE_PARSER.parse(localeString);

        assertThat(locales.get(0), is(new Locale("pl")));
        assertThat(locales.get(1), is(new Locale("en")));
        assertThat(locales.get(2), is(new Locale("ro")));
        assertThat(locales.get(3), is(new Locale("ru")));
    }
}