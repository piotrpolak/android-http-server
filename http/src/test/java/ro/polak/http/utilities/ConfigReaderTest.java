package ro.polak.http.utilities;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class ConfigReaderTest {

    @Test
    public void shouldReadConfig() throws IOException {
        String configInput = "Key Value\n#ThisIsCommend CValue\nAnotherKey AValue\ns \n Ignored me\n";

        ConfigReader configReader = new ConfigReader();
        Map<String, String> config = configReader.read(getStreamOutOfString(configInput));
        assertThat(config.size(), is(2));
        assertThat(config.get("Key"), is("Value"));
        assertThat(config.get("AnotherKey"), is("AValue"));
    }

    private InputStream getStreamOutOfString(String data) {
        return new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
    }
}