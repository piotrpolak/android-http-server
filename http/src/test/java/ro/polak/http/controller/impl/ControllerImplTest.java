package ro.polak.http.controller.impl;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.io.IOException;
import java.net.ServerSocket;

import javax.net.ServerSocketFactory;

import ro.polak.http.FileUtils;
import ro.polak.http.WebServer;
import ro.polak.http.configuration.ServerConfig;
import ro.polak.http.configuration.ServerConfigFactory;
import ro.polak.http.gui.ServerGui;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

// CHECKSTYLE.OFF: JavadocType
public final class ControllerImplTest {

    private static ServerConfigFactory serverConfigFactory;
    private static ServerSocketFactory serverSocketFactory;
    private static ServerConfig serverConfig;
    private static ServerGui serverGui;

    @BeforeEach
    public void setUp() throws IOException {
        serverConfig = mock(ServerConfig.class);
        serverConfigFactory = mock(ServerConfigFactory.class);
        serverSocketFactory = mock(ServerSocketFactory.class);

        ServerSocket serverSocket = mock(ServerSocket.class);
        when(serverSocket.accept()).thenCallRealMethod(); // Avoid NPE

        when(serverConfigFactory.getServerConfig()).thenReturn(serverConfig);
        when(serverConfig.getMaxServerThreads()).thenReturn(1);
        when(serverConfig.getDocumentRootPath()).thenReturn("/somepath");
        when(serverConfig.getTempPath()).thenReturn(FileUtils.createTempDirectory());
        when(serverSocketFactory.createServerSocket()).thenReturn(serverSocket);
        serverGui = mock(ServerGui.class);
    }

    @AfterEach
    public void shutDown() {
        Thread.currentThread().setDefaultUncaughtExceptionHandler(null);
    }

    @Test
    public void shouldAssignDefaultUncaughtExceptionHandler() {
        assertThat(Thread.currentThread().getDefaultUncaughtExceptionHandler(), is(nullValue()));
        new ControllerImpl(serverConfigFactory, serverSocketFactory, serverGui);

        assertThat(Thread.currentThread().getDefaultUncaughtExceptionHandler(), is(not(nullValue())));
    }

    @Test
    public void shouldGetWebServerAfterServerStart() {
        ControllerImpl controllerImpl = new ControllerImpl(serverConfigFactory, serverSocketFactory,
                serverGui);

        assertThat(controllerImpl.getWebServer(), is(nullValue()));
        controllerImpl.start();
        assertThat(controllerImpl.getWebServer(), is(not(nullValue())));
        assertThat(controllerImpl.getWebServer().isRunning(), is(true));

    }

    @Test
    public void shouldStopProperly() {
        ControllerImpl controllerImpl = new ControllerImpl(serverConfigFactory, serverSocketFactory,
                serverGui);

        controllerImpl.start();
        assertThat(controllerImpl.getWebServer(), is(not(nullValue())));
        WebServer webServer = controllerImpl.getWebServer();
        controllerImpl.stop();
        assertThat(controllerImpl.getWebServer(), is(nullValue()));
        assertThat(webServer.isRunning(), is(false));
        verify(serverGui, times(1)).stop();
    }

    @Test
    public void shouldThrowExceptionOnIllegalStart() {
        final ControllerImpl controllerImpl = new ControllerImpl(serverConfigFactory, serverSocketFactory,
                serverGui);

        controllerImpl.start();

        assertThrows(IllegalStateException.class, new Executable() {
            @Override
            public void execute() {
                controllerImpl.start();
            }
        });
    }

    @Test
    public void shouldLogSituationOnExceptionOnCreateException() throws IOException {
        when(serverSocketFactory.createServerSocket()).thenThrow(new IOException("Something"));
        ControllerImpl controllerImpl = new ControllerImpl(serverConfigFactory, serverSocketFactory,
                serverGui);

        try {
            controllerImpl.start();
        } catch (IllegalStateException e) {
            fail("Should start web server");
        }
    }

    @Test
    public void shouldThrowExceptionOnIllegalStop() {
        final ControllerImpl controllerImpl = new ControllerImpl(serverConfigFactory, serverSocketFactory,
                serverGui);
        assertThrows(IllegalStateException.class, new Executable() {
            @Override
            public void execute() {
                controllerImpl.stop();
            }
        });
    }
}
// CHECKSTYLE.ON: JavadocType
