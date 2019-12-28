/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2016
 **************************************************/

package impl;

import android.content.Context;

import java.util.regex.Pattern;

import admin.DriveAccessServlet;
import admin.GetFileServlet;
import admin.IndexServlet;
import admin.LoginServlet;
import admin.LogoutServlet;
import admin.ServerStatsServlet;
import admin.filter.LogoutFilter;
import admin.filter.SecurityFilter;
import api.SmsInboxServlet;
import api.SmsSendServlet;
import ro.polak.http.configuration.DeploymentDescriptorBuilder;
import ro.polak.http.configuration.ServerConfig;
import ro.polak.http.session.storage.SessionStorage;
import ro.polak.webserver.base.impl.BaseAndroidServerConfigFactory;

/**
 * Android server config factory.
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201611
 */
public class AndroidServerConfigFactory extends BaseAndroidServerConfigFactory {

    public AndroidServerConfigFactory(final Context context) {
        super(context);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected DeploymentDescriptorBuilder getDeploymentDescriptorBuilder(final SessionStorage sessionStorage,
                                                                         final ServerConfig serverConfig) {
        return super.getDeploymentDescriptorBuilder(sessionStorage, serverConfig)
                .addServletContext()
                    .withContextPath("/api/1.0")
                    .addServlet()
                        .withUrlPattern(Pattern.compile("^/sms/inbox"))
                        .withServletClass(SmsInboxServlet.class)
                    .end()
                    .addServlet()
                        .withUrlPattern(Pattern.compile("^/sms/send"))
                        .withServletClass(SmsSendServlet.class)
                    .end()
                .end()

                .addServletContext()
                    .withContextPath("/admin")
                    .addFilter()
                        .withUrlPattern(Pattern.compile("^.*$"))
                        .withUrlExcludedPattern(Pattern.compile("^/(?:Login|Logout)"))
                        .withFilterClass(SecurityFilter.class)
                    .end()
                    .addFilter()
                        .withUrlPattern(Pattern.compile("^/Logout$"))
                        .withFilterClass(LogoutFilter.class)
                    .end()
                    .addServlet()
                        .withUrlPattern(Pattern.compile("^/DriveAccess$"))
                        .withServletClass(DriveAccessServlet.class)
                    .end()
                    .addServlet()
                        .withUrlPattern(Pattern.compile("^/GetFile$"))
                        .withServletClass(GetFileServlet.class)
                    .end()
                    .addServlet()
                        .withUrlPattern(Pattern.compile("^/Index$"))
                        .withServletClass(IndexServlet.class)
                    .end()
                    .addServlet()
                        .withUrlPattern(Pattern.compile("^/$"))
                        .withServletClass(IndexServlet.class)
                    .end()
                    .addServlet()
                        .withUrlPattern(Pattern.compile("^/Login$"))
                        .withServletClass(LoginServlet.class)
                    .end()
                    .addServlet()
                        .withUrlPattern(Pattern.compile("^/Logout$"))
                        .withServletClass(LogoutServlet.class)
                    .end()
                    .addServlet()
                        .withUrlPattern(Pattern.compile("^/ServerStats$"))
                        .withServletClass(ServerStatsServlet.class)
                    .end()
                    .addServlet()
                        .withUrlPattern(Pattern.compile("^/SmsInbox$"))
                        .withServletClass(admin.SmsInboxServlet.class)
                    .end()
                .end();
    }
}
