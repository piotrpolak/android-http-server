package ro.polak.webserver;

import org.junit.BeforeClass;

import ro.polak.webserver.gui.ServerCliUi;

/**
 * https://zeroturnaround.com/rebellabs/the-correct-way-to-use-integration-tests-in-your-build-process/
 */
public class AbstractIT {

    @BeforeClass
    public static void setup() {
        ServerCliUi.main(new String[]{});
    }
}
