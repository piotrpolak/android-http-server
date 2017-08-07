package ro.polak.http.controller;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.ServerSocket;

import javax.net.ServerSocketFactory;

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
    private static ServerSocketFactory serverSocketFactory;
    private static ServerConfig serverConfig;
    private static ServerGui serverGui;

    @Before
    public void setUp() throws IOException {
        serverConfig = mock(ServerConfig.class);
        serverConfigFactory = mock(ServerConfigFactory.class);
        serverSocketFactory = mock(ServerSocketFactory.class);

        ServerSocket serverSocket = mock(ServerSocket.class);
        when(serverSocket.accept()).thenCallRealMethod(); // Avoid NPE

        when(serverConfigFactory.getServerConfig()).thenReturn(serverConfig);
        when(serverConfig.getMaxServerThreads()).thenReturn(1);
        when(serverConfig.getDocumentRootPath()).thenReturn("/somepath");
        when(serverConfig.getTempPath()).thenReturn("/tmp");
        when(serverSocketFactory.createServerSocket()).thenReturn(serverSocket);
        serverGui = mock(ServerGui.class);
    }

    @After
    public void shutDown() {
        Thread.currentThread().setDefaultUncaughtExceptionHandler(null);
    }

    @Test
    public void shouldAssignDefaultUncaughtExceptionHandler() {
        assertThat(Thread.currentThread().getDefaultUncaughtExceptionHandler(), is(nullValue()));
        new MainController(serverConfigFactory, serverSocketFactory, serverGui);

        assertThat(Thread.currentThread().getDefaultUncaughtExceptionHandler(), is(not(nullValue())));
    }

    @Test
    public void shouldGetWebServerAfterServerStart() {
        MainController mainController = new MainController(serverConfigFactory, serverSocketFactory,
                serverGui);

        assertThat(mainController.getWebServer(), is(nullValue()));
        mainController.start();
        assertThat(mainController, is(not(nullValue())));
    }

    @Test
    public void shouldStopProperly() {
        MainController mainController = new MainController(serverConfigFactory, serverSocketFactory,
                serverGui);

        mainController.start();
        assertThat(mainController.getWebServer(), is(not(nullValue())));
        mainController.stop();
        verify(serverGui, times(1)).stop();
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowExceptionOnIllegalStart() {
        MainController mainController = new MainController(serverConfigFactory, serverSocketFactory,
                serverGui);

        mainController.start();
        mainController.start();
    }

    @Test
    public void shouldTLogSituationOnExceptionOnCreateException() throws IOException {
        when(serverSocketFactory.createServerSocket()).thenThrow(new IOException("Something"));
        MainController mainController = new MainController(serverConfigFactory, serverSocketFactory,
                serverGui);

        mainController.start();
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowExceptionOnIllegalStop() {
        MainController mainController = new MainController(serverConfigFactory, serverSocketFactory,
                serverGui);

        mainController.stop();
    }
}