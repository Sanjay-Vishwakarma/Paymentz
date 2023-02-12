//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.payment.sbm.core;

import com.google.common.base.Strings;
import com.google.common.io.CharStreams;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.*;
import java.net.Proxy.Type;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import javax.net.ssl.HttpsURLConnection;
import org.apache.log4j.Logger;
import ru.bpc.ConnectionFactory;
import ru.bpc.IResponse;
import ru.bpc.ISslContextProvider;
import ru.bpc.MessageTransformer;
import ru.bpc.TrustAllConnectionsManager;
import ru.bpc.exception.BadHttpResponseCodeException;
import ru.bpc.exception.ConfigurationException;
import ru.bpc.exception.HttpsCommunicationException;
import ru.bpc.exception.SslException;
import ru.bpc.message.AdditionalParameters;
import ru.bpc.message.AgreementRequest;
import ru.bpc.message.CompletionRequest;
import ru.bpc.message.CompletionResponse;
import ru.bpc.message.ConfirmedPaymentRequest;
import ru.bpc.message.ContactRequest;
import ru.bpc.message.OrderStatusRequest;
import ru.bpc.message.OrderStatusResponse;
import ru.bpc.message.OriginalCreditRequest;
import ru.bpc.message.OriginalCreditResponse;
import ru.bpc.message.PaymentRequest;
import ru.bpc.message.PaymentResponse;
import ru.bpc.message.RefundRequest;
import ru.bpc.message.RefundResponse;
import ru.bpc.message.RegisterAndPayRequest;
import ru.bpc.message.RegisterAndPayResponse;
import ru.bpc.message.RegisterRequest;
import ru.bpc.message.RegisterResponse;
import ru.bpc.message.ReversalRequest;
import ru.bpc.message.ReversalResponse;
import ru.bpc.message.internal.AuthorisationParameters;
import ru.bpc.message.internal.SessionStatusRequest;
import ru.bpc.message.internal.SessionStatusResponse;
import java.net.HttpURLConnection;


public final class Plugin {
    private static Logger logger = Logger.getLogger(Plugin.class);
    private static final int HTTP_RESPONSE_OK = 200;
    public static final String DEFAULT_ENCODING = "UTF-8";
    public static final String DEFAULT_CONFIG_PROPERTIES = "config.properties";
    private ISslContextProvider sslProvider;
    private ConnectionFactory connectionFactory;
    protected URL url;
    protected String user;
    protected String password;
    private Proxy proxy;
    private String propertyFileName;
    private MessageTransformer messageTransformer;

    public static Plugin newInstance() {
        return new Plugin("config.properties", new ConnectionFactory());
    }

    public static Plugin newInstance(String propertiesInClasspath) {
        return new Plugin(propertiesInClasspath, new ConnectionFactory());
    }

    Plugin(String propertyFile, ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;

        logger.debug("Connection called---");
        System.out.println("Connection called---");

        try {
            this.sslProvider = new TrustAllConnectionsManager();
        } catch (NoSuchAlgorithmException var7) {
            throw new SslException("Unable to init ISslContextProvide", var7);
        } catch (KeyManagementException var8) {
            throw new SslException("Unable to init ISslContextProvide", var8);
        }

        Properties properties = new Properties();
        this.propertyFileName = propertyFile;

        try {
            properties.load(this.getClass().getResourceAsStream("/" + this.propertyFileName));
        } catch (NullPointerException var5) {
            throw new ConfigurationException("Unable to find configuration file " + this.propertyFileName + " in classpath", var5);
        } catch (IOException var6) {
            throw new ConfigurationException(this.propertyFileName + " was found. But an exception occurred while " + "reading properties from it. " + var6.getMessage(), var6);
        }

        this.validateProperties(properties);
        this.initProperties(properties);
        this.messageTransformer = new MessageTransformer();
    }

    private void validateProperties(Properties properties) {
        Iterator ex = Arrays.asList(new String[]{"ipay.api.url", "user.login", "user.password"}).iterator();

        String p;
        do {
            if(!ex.hasNext()) {
                if(Strings.isNullOrEmpty((String)properties.get("proxy.host")) ^ Strings.isNullOrEmpty((String)properties.get("proxy.port"))) {
                    throw new ConfigurationException("If you want to specify proxy you must setup both proxy.host and proxy.port. You can specify one of them without the other one. (see " + this.propertyFileName + ")");
                }

                if(!Strings.isNullOrEmpty((String)properties.get("proxy.port"))) {
                    try {
                        Integer.valueOf((String)properties.get("proxy.port"));
                    } catch (NumberFormatException var4) {
                        throw new ConfigurationException("Unable to parse proxy.port=" + properties.get("proxy.port") + ". It must be an integer but it\'s not.", var4);
                    }
                }

                return;
            }

            p = (String)ex.next();
        } while(!Strings.isNullOrEmpty((String)properties.get(p)));

        throw new ConfigurationException("Property " + p + " in " + this.propertyFileName + " is empty. It must not be empty.");
    }

    private void initProperties(Properties properties) {
        try {
            this.url = new URL(properties.getProperty("ipay.api.url"));
        } catch (MalformedURLException var5) {
            throw new ConfigurationException("Property ipay.api.url in " + this.propertyFileName + " is not a valid url [" + properties.get("ipay.api.url") + "]", var5);
        }

        String proxyHost = properties.getProperty("proxy.host");
        String proxyPort = properties.getProperty("proxy.port");
        if(!Strings.isNullOrEmpty(proxyHost) && proxyPort != null) {
            InetSocketAddress addr = new InetSocketAddress(proxyHost, Integer.valueOf(proxyPort).intValue());
            this.proxy = new Proxy(Type.SOCKS, addr);
        }

        this.user = properties.getProperty("user.login");
        this.password = properties.getProperty("user.password");

        System.out.println("user---"+user+"---"+password+"--proxy---"+proxy);
        logger.debug("proxyHost---"+proxyHost+"---"+proxyPort+"--proxy---"+proxy);
    }

    private <T extends IResponse> T sendRequest(String requestSubPath, Class<T> responseClass, List<AdditionalParameters> additionalParams, Object... requests) {
        try {
            URL ex = new URL(this.url, requestSubPath,new sun.net.www.protocol.https.Handler());
            logger.debug("Connecting to ---- " + ex);
            ArrayList params = new ArrayList();
            params.addAll(Arrays.asList(requests));
            params.add(new AuthorisationParameters(this.user, this.password));
            String postString = this.messageTransformer.toHttpPostParams(params.toArray());
            AdditionalParameters out;
            if(additionalParams != null) {
                for(Iterator conn = additionalParams.iterator(); conn.hasNext(); postString = postString + out.toHttpPOST()) {
                    out = (AdditionalParameters)conn.next();
                }
            }

            HttpsURLConnection conn1 = this.connectionFactory.newInstance(ex, this.proxy);
            conn1.setSSLSocketFactory(this.sslProvider.getSslContext().getSocketFactory());
            conn1.setHostnameVerifier(this.sslProvider.getHostnameVerifier());
            conn1.setRequestMethod("POST");
            conn1.setDoInput(true);
            conn1.setDoOutput(true);
            conn1.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            OutputStream out1 = conn1.getOutputStream();
            logger.debug("Sending request: " + postString);
            out1.write(postString.getBytes("UTF-8"));
            out1.flush();
            int responseCode = conn1.getResponseCode();
            if(responseCode != 200) {
                throw new BadHttpResponseCodeException(responseCode, ex.toString());
            } else {
                InputStream inputStream = conn1.getInputStream();
                String resultStr = CharStreams.toString(new InputStreamReader(inputStream, "UTF-8"));
                logger.debug("Getting response: " + resultStr);
                System.out.println("Getting response: " + resultStr);
                inputStream.close();
                return responseClass != null?this.messageTransformer.fromJsonResponse(resultStr, responseClass):null;
            }
        }
        catch (IOException var13) {
            logger.debug("IOException SendRequest---",var13);
            throw new HttpsCommunicationException("An exception occurred during communication with server [" + this.url + "]", var13);
        }
    }

    public RegisterResponse registerRequest(RegisterRequest rr, AdditionalParameters... params) {
        return (RegisterResponse)this.sendRequest("rest/register.do", RegisterResponse.class, Arrays.asList(params), new Object[]{rr});
    }

    public RegisterResponse registerRecurrentRequest(RegisterRequest rr, ContactRequest contactRequest, AgreementRequest agreementRequest, AdditionalParameters... params) {
        rr.setRecurrent();
        return (RegisterResponse)this.sendRequest("rest/register.do", RegisterResponse.class, Arrays.asList(params), new Object[]{rr, contactRequest, agreementRequest});
    }

    public RegisterResponse registerPreAuthRequest(RegisterRequest rr, AdditionalParameters... params) {
        return (RegisterResponse)this.sendRequest("rest/registerPreAuth.do", RegisterResponse.class, Arrays.asList(params), new Object[]{rr});
    }

    public OrderStatusResponse getOrderStatus(OrderStatusRequest rr) {
        return (OrderStatusResponse)this.sendRequest("rest/getOrderStatus.do", OrderStatusResponse.class, (List)null, new Object[]{rr});
    }

    public PaymentResponse authorizePayment(PaymentRequest rr, AdditionalParameters... params) {
        SessionStatusRequest sessionStatusRequest = new SessionStatusRequest();
        sessionStatusRequest.setOrderId(rr.getOrderId());
        sessionStatusRequest.setLanguage(rr.getLanguage());
        this.sendRequest("rest/getSessionStatus.do", SessionStatusResponse.class, (List)null, new Object[]{sessionStatusRequest});
        return (PaymentResponse)this.sendRequest("rest/processform.do", PaymentResponse.class, Arrays.asList(params), new Object[]{rr});
    }

    public PaymentResponse authorizeConfirmedPayment(ConfirmedPaymentRequest rr, AdditionalParameters... params) {
        return this.authorizePayment(rr, params);
    }

    public CompletionResponse complete(CompletionRequest rr) {
        return (CompletionResponse)this.sendRequest("rest/deposit.do", CompletionResponse.class, (List)null, new Object[]{rr});
    }

    public RefundResponse refund(RefundRequest rr) {
        return (RefundResponse)this.sendRequest("rest/refund.do", RefundResponse.class, (List)null, new Object[]{rr});
    }

    public ReversalResponse reverse(ReversalRequest rr) {
        return (ReversalResponse)this.sendRequest("rest/reverse.do", ReversalResponse.class, (List)null, new Object[]{rr});
    }

    public OriginalCreditResponse originalCredit(OriginalCreditRequest rr) {
        return (OriginalCreditResponse)this.sendRequest("rest/ocredit.do", OriginalCreditResponse.class, (List)null, new Object[]{rr});
    }

    public RegisterAndPayResponse registerAndPay(RegisterAndPayRequest rr, AdditionalParameters... params) {
        return (RegisterAndPayResponse)this.sendRequest("rest/registerAndPay.do", RegisterAndPayResponse.class, Arrays.asList(params), new Object[]{rr});
    }

    public RegisterAndPayResponse registerAndPay(RegisterAndPayRequest rr, AgreementRequest agreement, ContactRequest contact, AdditionalParameters... params) {
        rr.setDepositFlag("1");
        rr.setRecurrent();
        return (RegisterAndPayResponse)this.sendRequest("rest/registerAndPay.do", RegisterAndPayResponse.class, Arrays.asList(params), new Object[]{rr, agreement, contact});
    }

    public URL getBaseUrl() {
        return this.url;
    }
}
