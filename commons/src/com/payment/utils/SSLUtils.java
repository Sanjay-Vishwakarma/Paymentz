package com.payment.utils;

import javax.net.ssl.*;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.X509Certificate;

/**
 * Created by IntelliJ IDEA.
 * User: jignesh.r
 * Date: Aug 9, 2013
 * Time: 8:45:22 PM
 * To change this template use File | Settings | File Templates.
 */


public final class SSLUtils
{

    private static com.sun.net.ssl.HostnameVerifier __hostnameVerifier;
    private static com.sun.net.ssl.TrustManager[] __trustManagers;
    private static HostnameVerifier _hostnameVerifier;
    private static TrustManager[] _trustManagers;

    private static void __trustAllHostnames()
    {
        if (__hostnameVerifier == null)
        {
            __hostnameVerifier = new _FakeHostnameVerifier();
        }
        com.sun.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(__hostnameVerifier);
    }

    private static void __trustAllHttpsCertificates()
    {
        com.sun.net.ssl.SSLContext context;

        if (__trustManagers == null)
        {
            __trustManagers = new com.sun.net.ssl.TrustManager[]
                    {new _FakeX509TrustManager()};
        }
        try
        {
            context = com.sun.net.ssl.SSLContext.getInstance("SSL");
            context.init(null, __trustManagers, new SecureRandom());
        }
        catch (GeneralSecurityException gse)
        {
            throw new IllegalStateException(gse.getMessage());
        }
        com.sun.net.ssl.HttpsURLConnection.
                setDefaultSSLSocketFactory(context.getSocketFactory());
    }

    private static boolean isDeprecatedSSLProtocol()
    {
        return ("com.sun.net.ssl.internal.www.protocol".equals(System.
                getProperty("java.protocol.handler.pkgs")));
    }

    private static void _trustAllHostnames()
    {
        if (_hostnameVerifier == null)
        {
            _hostnameVerifier = new FakeHostnameVerifier();
        }
        HttpsURLConnection.setDefaultHostnameVerifier(_hostnameVerifier);
    }

    private static void _trustAllHttpsCertificates()
    {
        SSLContext context;

        if (_trustManagers == null)
        {
            _trustManagers = new TrustManager[]{new FakeX509TrustManager()};
        }

        try
        {
            context = SSLContext.getInstance("SSL");
            context.init(null, _trustManagers, new SecureRandom());
        }
        catch (GeneralSecurityException gse)
        {
            throw new IllegalStateException(gse.getMessage());
        }
        HttpsURLConnection.setDefaultSSLSocketFactory(context.
                getSocketFactory());
    }

    public static void trustAllHostnames()
    {
        if (isDeprecatedSSLProtocol())
        {
            __trustAllHostnames();
        }
        else
        {
            _trustAllHostnames();
        }
    }

    public static void trustAllHttpsCertificates()
    {
        if (isDeprecatedSSLProtocol())
        {
            __trustAllHttpsCertificates();
        }
        else
        {
            _trustAllHttpsCertificates();
        }
    }

    public static class _FakeHostnameVerifier
            implements com.sun.net.ssl.HostnameVerifier
    {

        public boolean verify(String hostname, String session)
        {
            return (true);
        }
    }


    public static class _FakeX509TrustManager
            implements com.sun.net.ssl.X509TrustManager
    {

        private static final X509Certificate[] _AcceptedIssuers =
                new X509Certificate[]{};


        public boolean isClientTrusted(X509Certificate[] chain)
        {
            return (true);
        }

        public boolean isServerTrusted(X509Certificate[] chain)
        {
            return (true);
        }

        public X509Certificate[] getAcceptedIssuers()
        {
            return (_AcceptedIssuers);
        }
    }


    public static class FakeHostnameVerifier implements HostnameVerifier
    {

        public boolean verify(String hostname,
                              SSLSession session)
        {
            return (true);
        }
    }


    public static class FakeX509TrustManager implements X509TrustManager
    {

        private static final X509Certificate[] _AcceptedIssuers =
                new X509Certificate[]{};


        public void checkClientTrusted(X509Certificate[] chain,
                                       String authType)
        {
        }

        public void checkServerTrusted(X509Certificate[] chain,
                                       String authType)
        {
        }

        public X509Certificate[] getAcceptedIssuers()
        {
            return (_AcceptedIssuers);
        }
    }

    public static void setupSecurityProvider()
    {
        System.setProperty("java.protocol.handler.pkgs", "com.sun.net.ssl.internal.www.protocol");
        Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());

        com.sun.net.ssl.HostnameVerifier hv = new com.sun.net.ssl.HostnameVerifier()
        {
            public boolean verify(String urlHostname, String certHostname)
            {
                return true;
            }
        };
        com.sun.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(hv);
    }
}

