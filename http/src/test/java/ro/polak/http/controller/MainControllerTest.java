package ro.polak.http.controller;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ro.polak.http.ServerConfig;
import ro.polak.http.ServerConfigFactory;
import ro.polak.http.gui.ServerGui;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

public class MainControllerTest {

    private static ServerConfigFactory serverConfigFactory;
    private static ServerConfig serverConfig;
    private static ServerGui serverGui;

    @Before
    public void setUp() {
        serverConfig = mock(ServerConfig.class);
        serverConfigFactory = mock(ServerConfigFactory.class);
        when(serverConfigFactory.getServerConfig()).thenReturn(serverConfig);
        when(serverConfig.getMaxServerThreads()).thenReturn(1);
        when(serverConfig.getDocumentRootPath()).thenReturn("/somepath");
        when(serverConfig.getTempPath()).thenReturn("/tmp");
        serverGui = mock(ServerGui.class);
    }

    @After
    public void shutDown() {
        Thread.currentThread().setDefaultUncaughtExceptionHandler(null);
    }

    @Test
    public void shouldAssignDefaultUncaughtExceptionHandler() {
        ServerGui serverGui = mock(ServerGui.class);
        assertThat(Thread.currentThread().getDefaultUncaughtExceptionHandler(), is(nullValue()));
        MainController mainController = new MainController(serverConfigFactory, serverGui);
        assertThat(Thread.currentThread().getDefaultUncaughtExceptionHandler(), is(not(nullValue())));
    }

    @Test
    public void shouldGetWebServerAfterServerStart() {
        MainController mainController = new MainController(serverConfigFactory, serverGui);
        assertThat(mainController.getWebServer(), is(nullValue()));
        mainController.start();
        assertThat(mainController, is(not(nullValue())));
    }

    @Test
    public void shouldStopProperly() {
        MainController mainController = new MainController(serverConfigFactory, serverGui);
        mainController.start();
        assertThat(mainController.getWebServer(), is(not(nullValue())));
        mainController.stop();
        verify(serverGui, times(1)).stop();
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowExceptionOnIllegalStart() {
        MainController mainController = new MainController(serverConfigFactory, serverGui);
        mainController.start();
        mainController.start();
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowExceptionOnIllegalStop() {
        MainController mainController = new MainController(serverConfigFactory, serverGui);
        mainController.stop();
    }
}