package com.directi.pg.core.paymentgateway;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.lpb.LPBResponseVO;
import com.directi.pg.core.lpb.LpbRequestVO;
import com.directi.pg.core.lpb.LpbUtils;
import com.directi.pg.core.lpb.core.message.EcomBindingStub;
import com.directi.pg.core.lpb.core.message.EcomPaymentType;
import com.directi.pg.core.lpb.core.message.EcomServiceLocator;
import com.directi.pg.core.valueObjects.*;
import com.manager.PaymentManager;
import com.payment.common.core.*;
import com.payment.exceptionHandler.*;
import com.payment.exceptionHandler.constraint.PZGenericConstraint;
import com.payment.exceptionHandler.constraintType.PZConstraintExceptionEnum;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeVO;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.rpc.ServiceException;
import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.rmi.RemoteException;
import java.security.Security;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.ResourceBundle;

/**
 * Created by IntelliJ IDEA.
 * User:Roshan
 * Date: Dec 7, 2012
 * Time: 9:30:30 AM
 * To change this template use File | Settings | File Templates.
 */
public class LpbPaymentGateway extends AbstractPaymentGateway
{
    //Configuration
    public static final String GATEWAY_TYPE = "LPB";
    final static ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.LpbServlet");
    public static final String mode = RB.getString("mode");
    public static final String privateKey = RB.getString("testPrivateKey");
    public static final String publicKey = RB.getString("testPublicKey");
    static{
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * Hex string utility
     */

    static final byte[] HEX_CHAR_TABLE = {
            (byte) '0', (byte) '1', (byte) '2', (byte) '3', (byte) '4', (byte) '5',
            (byte) '6', (byte) '7', (byte) '8', (byte) '9', (byte) 'a', (byte) 'b',
            (byte) 'c', (byte) 'd', (byte) 'e', (byte) 'f'};
    private static Logger log = new Logger(LpbPaymentGateway.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(LpbPaymentGateway.class.getName());

    public LpbPaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }

    public static String doPostHTTPSURLConnection(String strURL, String req) throws PZTechnicalViolationException
    {
        OutputStreamWriter outSW = null;
        BufferedReader in = null;
        String strResponse = "";
        try
        {

            URL url = new URL(strURL);
            URLConnection connection = url.openConnection();

            if (connection instanceof HttpURLConnection)
            {
                ((HttpURLConnection) connection).setRequestMethod("POST");
            }

            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);


            connection.setRequestProperty("Content-type", "application/x-www-form-urlencoded");

            outSW = new OutputStreamWriter(
                    connection.getOutputStream());
            outSW.write(req);
            outSW.close();

            in = new BufferedReader(
                    new InputStreamReader(
                            connection.getInputStream()));
            String decodedString;
            while ((decodedString = in.readLine()) != null)
            {
                strResponse = strResponse + decodedString;
            }
            in.close();


        }
        catch (MalformedURLException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(LpbPaymentGateway.class.getName(), "doPostHTTPSURLConnection()", null, "common", "Technical Exception while placing transaction", PZTechnicalExceptionEnum.MALFORMED_URL_EXCEPITON, null, e.getMessage(), e.getCause());
        }
        catch (ProtocolException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(LpbPaymentGateway.class.getName(), "doPostHTTPSURLConnection()", null, "common", "Technical Exception while placing transaction", PZTechnicalExceptionEnum.PROTOCOL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (IOException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(LpbPaymentGateway.class.getName(), "doPostHTTPSURLConnection()", null, "common", "Technical Exception while placing transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            if (outSW != null)
            {
                try
                {
                    outSW.close();
                }
                catch (IOException e)
                {
                }
            }
            if (in != null)
            {
                try
                {
                    in.close();
                }
                catch (IOException e)
                {
                }
            }
        }
        if (strResponse == null)
            return "";
        else
            return strResponse;
    }

    static String getHexString(byte[] raw) throws UnsupportedEncodingException
    {
        byte[] hex = new byte[2 * raw.length];
        int index = 0;

        for (byte b : raw)
        {
            int v = b & 0xFF;
            hex[index++] = HEX_CHAR_TABLE[v >>> 4];
            hex[index++] = HEX_CHAR_TABLE[v & 0xF];
        }
        return new String(hex, "");
    }

    public static LPBResponseVO getCommResponseVO(String xmlResponse) throws PZTechnicalViolationException
    {
        LPBResponseVO res = new LPBResponseVO();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;

        try
        {
            builder = factory.newDocumentBuilder();
        }
        catch (ParserConfigurationException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(LpbPaymentGateway.class.getName(), "getCommResponseVO()", null, "common", "Technical Exception while placing transaction", PZTechnicalExceptionEnum.PARSE_CONFIG_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        Document document = null;
        try
        {
            document = builder.parse(new InputSource(new StringReader(xmlResponse)));
        }
        catch (SAXException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(LpbPaymentGateway.class.getName(), "getCommResponseVO()", null, "common", "Technical Exception while placing transaction", PZTechnicalExceptionEnum.SAXEXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (IOException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(LpbPaymentGateway.class.getName(), "getCommResponseVO()", null, "common", "Technical Exception while placing transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }

        Element data = document.getDocumentElement();
        NodeList list1 = data.getChildNodes();
        for (int i = 0; i < list1.getLength(); i++)
        {
            Node node1 = list1.item(i);
            if ("Payment".equals(node1.getNodeName()))
            {
                NodeList list2 = node1.getChildNodes();

                for (int j = 0; j < list2.getLength(); j++)
                {
                    Node node2 = list2.item(j);
                    if ("ID".equals(node2.getNodeName()))
                    {
                        res.setTransactionId(node2.getTextContent());
                    }
                    else if ("LastDate".equals(node2.getNodeName()))
                    {
                        res.setResponseTime(node2.getTextContent());
                    }

                }
            }
            else if ("Auth".equals(node1.getNodeName()))
            {
                NodeList list2 = node1.getChildNodes();
                for (int j = 0; j < list2.getLength(); j++)
                {
                    Node node2 = list2.item(j);

                    if ("ActionCode".equals(node2.getNodeName()))
                    {
                        res.setErrorCode(node2.getTextContent());
                    }

                    else if ("Description".equals(node2.getNodeName()))
                    {
                        res.setTransactionStatus(node2.getTextContent());
                    }

                }
            }
            if ("D3D".equals(node1.getNodeName()))
            {
                NodeList list2 = node1.getChildNodes();
                for (int j = 0; j < list2.getLength(); j++)
                {
                    Node node2 = list2.item(j);
                    if ("Enrolled".equals(node2.getNodeName()))
                    {
                        res.setEnrolled(node2.getTextContent());
                    }
                    if ("ECI".equals(node2.getNodeName()))
                    {
                        res.setEci(node2.getTextContent());
                    }

                    if ("ACS".equals(node2.getNodeName()))
                    {
                        NodeList list3 = node2.getChildNodes();
                        for (int k = 0; k < list3.getLength(); k++)
                        {
                            Node node3 = list3.item(k);
                            if ("URL".equals(node3.getNodeName()))
                            {
                                res.setACSUrl(node3.getTextContent());
                            }
                            if ("PaReq".equals(node3.getNodeName()))
                            {
                                res.setPaReq(node3.getTextContent());
                            }
                        }
                    }
                }
            }
        }

        return res;
    }

    @Override
    public Hashtable processAuthentication(String trackingID, String transAmount, Hashtable cardDetailHash, Hashtable billingAddrHash, Hashtable shippingAddrHash, Hashtable MPIDataHash, String ipaddress) throws SystemError
    {
        return super.processAuthentication(trackingID, transAmount, cardDetailHash, billingAddrHash, shippingAddrHash, MPIDataHash, ipaddress);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public String getMaxWaitDays()
    {
        return "3.5";
    }

    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException, PZDBViolationException
    {
        validateForSale(trackingID, requestVO);
        LPBResponseVO responseVO = null;
        LpbUtils.Package packet;
        String sign;

        GatewayAccount account = GatewayAccountService.getGatewayAccount(accountId);
        CommRequestVO comRequestVO = (CommRequestVO) requestVO;
        GenericTransDetailsVO genericTransDetailsVO = comRequestVO.getTransDetailsVO();
        GenericAddressDetailsVO genericAddressDetailsVO = comRequestVO.getAddressDetailsVO();
        GenericCardDetailsVO genericCardDetailsVO = comRequestVO.getCardDetailsVO();
        CommMerchantVO commMerchantVO= comRequestVO.getCommMerchantVO();
        String _3DSupportAccount = account.get_3DSupportAccount();
        Double amount = Double.parseDouble(genericTransDetailsVO.getAmount()) * 100;
        String attemptThreeD = "";
        Functions functions = new Functions();

        transactionLogger.error("host url----"+commMerchantVO.getHostUrl());
        String termUrl = "";
        if (functions.isValueNull(commMerchantVO.getHostUrl()))
        {
            termUrl = "https://"+commMerchantVO.getHostUrl()+RB.getString("HOST_URL");
            transactionLogger.error("from host url----"+termUrl);
        }
        else
        {
            termUrl = RB.getString("TERM_URL");
            transactionLogger.error("from RB----"+termUrl);
        }

        if (functions.isValueNull(comRequestVO.getAttemptThreeD()))
        {
            attemptThreeD = comRequestVO.getAttemptThreeD();
        }

        String mode = "4";
        if ("Y".equals(_3DSupportAccount) && !("Direct".equalsIgnoreCase(attemptThreeD)))
        {
            mode = "6";
        }

        String saleRequest = "<data>" +
                "<AutoDeposit>true</AutoDeposit>" +
                "<Payment>" +
                "<Mode>" + mode + "</Mode>" +
                "</Payment>" +
                "<Merchant>" +
                "<Code>" + account.getMerchantId() + "</Code>" +
                "</Merchant>" +
                "<Order>" +
                "<ID>" + "PMNTZ" + genericTransDetailsVO.getOrderId() + "</ID>" +
                "<Amount>" + amount.intValue() + "</Amount>" +
                "<Currency>" + genericTransDetailsVO.getCurrency() + "</Currency>" +
                "<Description>" + genericTransDetailsVO.getOrderDesc() + "</Description>" +
                "</Order>" +
                "<Card>" +
                "<Name>" + genericAddressDetailsVO.getFirstname() + genericAddressDetailsVO.getLastname() + "</Name>" +
                "<Number>" + genericCardDetailsVO.getCardNum() + "</Number>" +
                "<Expiry>" + genericCardDetailsVO.getExpYear().substring(2) + genericCardDetailsVO.getExpMonth() + "</Expiry>" +
                "<CSC>" + genericCardDetailsVO.getcVV() + "</CSC>" +
                "</Card>" +
                "<RemoteAddress>" + genericAddressDetailsVO.getIp() + "</RemoteAddress>" +
                "</data>";
        String saleRequestlog = "<data>" +
                "<AutoDeposit>true</AutoDeposit>" +
                "<Payment>" +
                "<Mode>" + mode + "</Mode>" +
                "</Payment>" +
                "<Merchant>" +
                "<Code>" + account.getMerchantId() + "</Code>" +
                "</Merchant>" +
                "<Order>" +
                "<ID>" + "PMNTZ" + genericTransDetailsVO.getOrderId() + "</ID>" +
                "<Amount>" + amount.intValue() + "</Amount>" +
                "<Currency>" + genericTransDetailsVO.getCurrency() + "</Currency>" +
                "<Description>" + genericTransDetailsVO.getOrderDesc() + "</Description>" +
                "</Order>" +
                "<Card>" +
                "<Name>" + genericAddressDetailsVO.getFirstname() + genericAddressDetailsVO.getLastname() + "</Name>" +
                "<Number>" + functions.maskingPan(genericCardDetailsVO.getCardNum()) + "</Number>" +
                "<Expiry>" + functions.maskingExpiry(genericCardDetailsVO.getExpYear().substring(2) + genericCardDetailsVO.getExpMonth()) + "</Expiry>" +
                "<CSC>" + functions.maskingNumber(genericCardDetailsVO.getcVV()) + "</CSC>" +
                "</Card>" +
                "<RemoteAddress>" + genericAddressDetailsVO.getIp() + "</RemoteAddress>" +
                "</data>";

        transactionLogger.debug("-----LPB sale request------" +trackingID + "--" +  saleRequestlog);

        sign = LpbUtils.sign(saleRequest, privateKey);
        packet = LpbUtils.encrypt(saleRequest, publicKey);

        String responseXml;
        BigInteger index = new BigInteger("1");
        EcomPaymentType request = new EcomPaymentType(account.getMerchantId(), index, packet.key, packet.data, sign);
        EcomPaymentType response = processRequest(request, "sale");

        LpbUtils.Package responseKeyData = new LpbUtils.Package(response.getDATA(), response.getKEY());
        responseXml = LpbUtils.decrypt(responseKeyData, privateKey);

        transactionLogger.debug("-----LPB sale response-----" +trackingID + "--" +  responseXml);
        responseVO = getCommResponseVO(responseXml);

        if (responseVO != null && "Y".equalsIgnoreCase(responseVO.getEnrolled()))
        {
            String status = "pending3DConfirmation";

            PaymentManager paymentManager = new PaymentManager();
            paymentManager.updatePaymentIdForCommon(responseVO, trackingID);

            LPBResponseVO comm3DResponseVO = new LPBResponseVO();

            comm3DResponseVO.setPaReq(responseVO.getPaReq());
            comm3DResponseVO.setUrlFor3DRedirect(responseVO.getACSUrl());
            comm3DResponseVO.setMd(trackingID);
            comm3DResponseVO.setRedirectMethod("POST");
            comm3DResponseVO.setTerURL(termUrl+trackingID);
            comm3DResponseVO.setStatus(status);
            comm3DResponseVO.setDescriptor(account.getDisplayName());
            comm3DResponseVO.setTransactionType("sale");
            comm3DResponseVO.setTransactionId(responseVO.getTransactionId());
            comm3DResponseVO.setCurrency(genericTransDetailsVO.getCurrency());
            comm3DResponseVO.setTmpl_Amount(genericAddressDetailsVO.getTmpl_amount());
            comm3DResponseVO.setTmpl_Currency(genericAddressDetailsVO.getTmpl_currency());

            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();

            comm3DResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
            return comm3DResponseVO;
        }
        else if (responseVO != null && "000".equals(responseVO.getErrorCode()))
        {
            responseVO.setStatus("success");
            responseVO.setDescription("Transaction successful.");
        }
        else
        {
            responseVO.setStatus("fail");
            responseVO.setDescription("Transaction failed");
        }
        responseVO.setCurrency(genericTransDetailsVO.getCurrency());
        responseVO.setTmpl_Amount(genericAddressDetailsVO.getTmpl_amount());
        responseVO.setTmpl_Currency(genericAddressDetailsVO.getTmpl_currency());
        return responseVO;
    }

    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException, PZDBViolationException
    {
        validateForSale(trackingID, requestVO);
        LPBResponseVO responseVO = null;
        GatewayAccount account = GatewayAccountService.getGatewayAccount(accountId);

        String sign;
        LpbUtils.Package packet;

        CommRequestVO comRequestVO = (CommRequestVO) requestVO;
        GenericTransDetailsVO genericTransDetailsVO = comRequestVO.getTransDetailsVO();
        GenericAddressDetailsVO genericAddressDetailsVO = comRequestVO.getAddressDetailsVO();
        GenericCardDetailsVO genericCardDetailsVO = comRequestVO.getCardDetailsVO();
        CommMerchantVO commMerchantVO=comRequestVO.getCommMerchantVO();
        Double amount = Double.parseDouble(genericTransDetailsVO.getAmount()) * 100;
        String _3DSupportAccount = account.get_3DSupportAccount();
        String attemptThreeD = "";
        Functions functions = new Functions();

        transactionLogger.error("host url----"+commMerchantVO.getHostUrl());
        String termUrl = "";
        if (functions.isValueNull(commMerchantVO.getHostUrl()))
        {
            termUrl = "https://"+commMerchantVO.getHostUrl()+RB.getString("HOST_URL");
            transactionLogger.error("from host url----"+termUrl);
        }
        else
        {
            termUrl = RB.getString("TERM_URL");
            transactionLogger.error("from RB----"+termUrl);
        }

        if (functions.isValueNull(comRequestVO.getAttemptThreeD()))
        {
            attemptThreeD = comRequestVO.getAttemptThreeD();
        }

        String mode = "4";
        if ("Y".equals(_3DSupportAccount) && !("Direct".equalsIgnoreCase(attemptThreeD)))
        {
            mode = "6";
        }

        String authRequest = "<data>" +
                "<AutoDeposit>false</AutoDeposit>" +
                "<Payment>" +
                "<Mode>" + mode + "</Mode>" +
                "</Payment>" +
                "<Merchant>" +
                "<Code>" + account.getMerchantId() + "</Code>" +
                "</Merchant>" +
                "<Order>" +
                "<ID>" + "PMNTZ" + genericTransDetailsVO.getOrderId() + "</ID>" +
                "<Amount>" + amount.intValue() + "</Amount>" +
                "<Currency>" + genericTransDetailsVO.getCurrency() + "</Currency>" +
                "<Description>" + genericTransDetailsVO.getOrderDesc() + "</Description>" +
                "</Order>" +
                "<Card>" +
                "<Name>" + genericAddressDetailsVO.getFirstname() + genericAddressDetailsVO.getLastname() + "</Name>" +
                "<Number>" + genericCardDetailsVO.getCardNum() + "</Number>" +
                "<Expiry>" + genericCardDetailsVO.getExpYear().substring(2) + genericCardDetailsVO.getExpMonth() + "</Expiry>" +
                "<CSC>" + genericCardDetailsVO.getcVV() + "</CSC>" +
                "</Card>" +
                "<RemoteAddress>" + genericAddressDetailsVO.getIp() + "</RemoteAddress>" +
                "</data>";
        String authRequestlog = "<data>" +
                "<AutoDeposit>false</AutoDeposit>" +
                "<Payment>" +
                "<Mode>" + mode + "</Mode>" +
                "</Payment>" +
                "<Merchant>" +
                "<Code>" + account.getMerchantId() + "</Code>" +
                "</Merchant>" +
                "<Order>" +
                "<ID>" + "PMNTZ" + genericTransDetailsVO.getOrderId() + "</ID>" +
                "<Amount>" + amount.intValue() + "</Amount>" +
                "<Currency>" + genericTransDetailsVO.getCurrency() + "</Currency>" +
                "<Description>" + genericTransDetailsVO.getOrderDesc() + "</Description>" +
                "</Order>" +
                "<Card>" +
                "<Name>" + genericAddressDetailsVO.getFirstname() + genericAddressDetailsVO.getLastname() + "</Name>" +
                "<Number>" + functions.maskingPan(genericCardDetailsVO.getCardNum()) + "</Number>" +
                "<Expiry>" + functions.maskingExpiry(genericCardDetailsVO.getExpYear().substring(2) + genericCardDetailsVO.getExpMonth()) + "</Expiry>" +
                "<CSC>" + functions.maskingNumber(genericCardDetailsVO.getcVV()) + "</CSC>" +
                "</Card>" +
                "<RemoteAddress>" + genericAddressDetailsVO.getIp() + "</RemoteAddress>" +
                "</data>";
        transactionLogger.debug("-----LPB Auth request-----" + trackingID + "--" + authRequestlog);
        sign = LpbUtils.sign(authRequest, privateKey);
        packet = LpbUtils.encrypt(authRequest, publicKey);

        String responseXml;
        BigInteger index = new BigInteger("1");
        EcomPaymentType request = new EcomPaymentType(account.getMerchantId(), index, packet.key, packet.data, sign);
        EcomPaymentType response = processRequest(request, "auth");

        LpbUtils.Package responseKeyData = new LpbUtils.Package(response.getDATA(), response.getKEY());
        responseXml = LpbUtils.decrypt(responseKeyData, privateKey);
        transactionLogger.debug("-----LPB Auth response----" +trackingID + "--" +  responseXml);
        responseVO = getCommResponseVO(responseXml);

        if (responseVO != null && "Y".equalsIgnoreCase(responseVO.getEnrolled()))
        {
            String status = "pending3DConfirmation";

            PaymentManager paymentManager = new PaymentManager();
            paymentManager.updatePaymentIdForCommon(responseVO, trackingID);

            LPBResponseVO comm3DResponseVO = new LPBResponseVO();
            comm3DResponseVO.setPaReq(responseVO.getPaReq());
            comm3DResponseVO.setUrlFor3DRedirect(responseVO.getACSUrl());
            comm3DResponseVO.setMd(trackingID);
            comm3DResponseVO.setRedirectMethod("POST");
            comm3DResponseVO.setTerURL(termUrl+trackingID);
            comm3DResponseVO.setStatus(status);
            comm3DResponseVO.setDescriptor(account.getDisplayName());
            comm3DResponseVO.setTransactionType("auth");
            comm3DResponseVO.setTransactionId(responseVO.getTransactionId());
            comm3DResponseVO.setCurrency(genericTransDetailsVO.getCurrency());
            comm3DResponseVO.setTmpl_Amount(genericAddressDetailsVO.getTmpl_amount());
            comm3DResponseVO.setTmpl_Currency(genericAddressDetailsVO.getTmpl_currency());

            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();

            comm3DResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
            return comm3DResponseVO;
        }
        if (responseVO != null && "000".equals(responseVO.getErrorCode()))
        {
            responseVO.setStatus("success");
            responseVO.setDescription("Transaction successful.");
        }
        else
        {
            responseVO.setStatus("fail");
            responseVO.setDescription("Transaction failed");
        }
        responseVO.setCurrency(genericTransDetailsVO.getCurrency());
        responseVO.setTmpl_Amount(genericAddressDetailsVO.getTmpl_amount());
        responseVO.setTmpl_Currency(genericAddressDetailsVO.getTmpl_currency());
        return responseVO;
    }

    /**
     *
     * @param trackingID
     * @param requestVO
     * @return
     * @throws com.directi.pg.SystemError
     */
    public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException
    {
        validateForRefund(trackingID, requestVO);
        LPBResponseVO responseVO = null;
        GatewayAccount account = GatewayAccountService.getGatewayAccount(accountId);

        String sign;
        LpbUtils.Package packet;

        CommRequestVO comRequestVO = (CommRequestVO) requestVO;
        CommTransactionDetailsVO transDetailsVO = comRequestVO.getTransDetailsVO();
        Double amount = Double.parseDouble(transDetailsVO.getAmount()) * 100;
        String _3DSupportAccount = account.get_3DSupportAccount();
        String mode = "4";
        if ("Y".equals(_3DSupportAccount))
        {
            mode = "6";
        }
        String refundRequest = "<data>" +
                "<Payment>" +
                "<ID>" + transDetailsVO.getPreviousTransactionId() + "</ID>" +
                "<Mode>" + mode + "</Mode>" +
                "</Payment>" +
                "<Merchant>" +
                "<Code>" + account.getMerchantId() + "</Code>" +
                "</Merchant>" +
                "<Order>" +
                "<Amount>" + amount.intValue() + "</Amount>" +
                "<Currency>" + transDetailsVO.getCurrency() + "</Currency>" +
                "</Order>" +
                "</data>";

        transactionLogger.debug("-----LPB Refund request----" + trackingID + "--" + refundRequest);
        sign = LpbUtils.sign(refundRequest, privateKey);
        packet = LpbUtils.encrypt(refundRequest, publicKey);

        String responseXml;
        BigInteger index = new BigInteger("1");
        EcomPaymentType request = new EcomPaymentType(account.getMerchantId(), index, packet.key, packet.data, sign);

        EcomPaymentType response = processRequest(request, "refund");

        LpbUtils.Package responseKeyData = new LpbUtils.Package(response.getDATA(), response.getKEY());
        responseXml = LpbUtils.decrypt(responseKeyData, privateKey);
        transactionLogger.debug("-----LPB Refund response----" +trackingID + "--" +  responseXml);
        responseVO = getCommResponseVO(responseXml);

        if (responseVO != null && "000".equals(responseVO.getErrorCode()))
        {
            responseVO.setStatus("success");
            responseVO.setDescription("Transaction successful.");
        }
        else
        {
            responseVO.setStatus("fail");
        }

        return responseVO;

    }

    /**
     *
     * @param trackingID
     * @param requestVO
     * @return
     * @throws com.directi.pg.SystemError
     */
    public GenericResponseVO processCapture(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException
    {

        validateForRefund(trackingID, requestVO);
        LPBResponseVO responseVO = null;
        GatewayAccount account = GatewayAccountService.getGatewayAccount(accountId);
        String sign;
        LpbUtils.Package packet;

        CommRequestVO comRequestVO = (CommRequestVO) requestVO;
        CommTransactionDetailsVO transDetailsVO = comRequestVO.getTransDetailsVO();
        String _3DSupportAccount = account.get_3DSupportAccount();
        String mode = "4";
        if ("Y".equals(_3DSupportAccount))
        {
            mode = "6";
        }
        Double amount = Double.parseDouble(transDetailsVO.getAmount()) * 100;
        String captureRequest = "<data>" +
                "<Payment>" +
                "<ID>" + transDetailsVO.getPreviousTransactionId() + "</ID>" +
                "<Mode>" + mode + "</Mode>" +
                "</Payment>" +
                "<Merchant>" +
                "<Code>" + account.getMerchantId() + "</Code>" +
                "</Merchant>" +
                "<Order>" +
                "<Amount>" + amount.intValue() + "</Amount>" +
                "<Currency>" + transDetailsVO.getCurrency() + "</Currency>" +
                "</Order>" +
                "</data>";


        transactionLogger.debug("-----Capture Request-----" + trackingID + "--" + captureRequest);
        sign = LpbUtils.sign(captureRequest, privateKey);
        packet = LpbUtils.encrypt(captureRequest, publicKey);

        String responseXml;
        BigInteger index = new BigInteger("1");
        EcomPaymentType request = new EcomPaymentType(account.getMerchantId(), index, packet.key, packet.data, sign);

        EcomPaymentType response = processRequest(request, "capture");

        LpbUtils.Package responseKeyData = new LpbUtils.Package(response.getDATA(), response.getKEY());
        responseXml = LpbUtils.decrypt(responseKeyData, privateKey);
        transactionLogger.debug("Capture Response:::::" + trackingID + "--" + responseXml);
        responseVO = getCommResponseVO(responseXml);

        if (responseVO != null && "000".equals(responseVO.getErrorCode()))
        {
            responseVO.setStatus("success");
            responseVO.setDescription("Transaction successful.");
        }
        else
        {
            responseVO.setStatus("fail");
        }


        return responseVO;

    }

    /**
     *
     * @param trackingID
     * @param requestVO
     * @return
     * @throws com.directi.pg.SystemError
     */
    public GenericResponseVO processVoid(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException
    {

        validateForRefund(trackingID, requestVO);
        LPBResponseVO responseVO = null;
        GatewayAccount account = GatewayAccountService.getGatewayAccount(accountId);
        String sign;
        LpbUtils.Package packet;

        CommRequestVO comRequestVO = (CommRequestVO) requestVO;
        CommTransactionDetailsVO transDetailsVO = comRequestVO.getTransDetailsVO();
        String _3DSupportAccount = account.get_3DSupportAccount();
        String mode = "4";
        if ("Y".equals(_3DSupportAccount))
        {
            mode = "6";
        }
        String cancelRequest = "<data>" +
                "<Payment>" +
                "<ID>" + transDetailsVO.getPreviousTransactionId() + "</ID>" +
                "<Mode>" + mode + "</Mode>" +
                "</Payment>" +
                "<Merchant>" +
                "<Code>" + account.getMerchantId() + "</Code>" +
                "</Merchant>" +
                "</data>";


        sign = LpbUtils.sign(cancelRequest, privateKey);
        packet = LpbUtils.encrypt(cancelRequest, publicKey);
        transactionLogger.debug("-----Cancel Request-----" + trackingID + "--" + cancelRequest);
        String responseXml;
        BigInteger index = new BigInteger("1");
        EcomPaymentType request = new EcomPaymentType(account.getMerchantId(), index, packet.key, packet.data, sign);

        EcomPaymentType response = processRequest(request, "cancel");

        LpbUtils.Package responseKeyData = new LpbUtils.Package(response.getDATA(), response.getKEY());
        responseXml = LpbUtils.decrypt(responseKeyData, privateKey);
        transactionLogger.debug("-----Cancel Response-----" + trackingID + "--" + responseXml);
        responseVO = getCommResponseVO(responseXml);

        if (responseVO != null && "000".equals(responseVO.getErrorCode()))
        {
            responseVO.setStatus("success");
            responseVO.setDescription("Transaction successful.");
        }
        else
        {
            responseVO.setStatus("fail");
        }


        return responseVO;

    }

    public GenericResponseVO processInquiry(GenericRequestVO requestVO) throws PZTechnicalViolationException, PZDBViolationException
    {
        LPBResponseVO responseVO = null;
        String sign = null;
        LpbUtils.Package packet = null;

        CommRequestVO comRequestVO = (CommRequestVO) requestVO;
        CommTransactionDetailsVO transDetailsVO = comRequestVO.getTransDetailsVO();

        GatewayAccount account = GatewayAccountService.getGatewayAccount(accountId);
        String transId = getTransIdFromTrackingId(transDetailsVO.getOrderId());
        String mid = account.getMerchantId();
        String _3DSupportAccount = account.get_3DSupportAccount();
        String mode = "4";
        if ("Y".equals(_3DSupportAccount))
        {
            mode = "6";
        }

        String merchantOrderId = "";
        String transactionId = "";
        String authCode = "";
        String transactionStatus = "";
        String transactionType = "";
        String amount = "";
        String currency = "";
        String remark = "";
        String status = "";
        String transactionDate = "";
        String errorCode = "";

        String inqueryRequest = "<data>" +
                "<Payment>" +
                "<ID>" + transId + "</ID>" +
                "<Mode>" + mode + "</Mode>" +
                "</Payment>" +
                "</data>";


        transactionLogger.error("------inquiry request-----" + inqueryRequest);
        sign = LpbUtils.sign(inqueryRequest, privateKey);
        packet = LpbUtils.encrypt(inqueryRequest, publicKey);

        String responseXml;
        BigInteger index = new BigInteger("1");
        EcomPaymentType request = new EcomPaymentType(account.getMerchantId(), index, packet.key, packet.data, sign);

        EcomPaymentType response = processRequest(request, "inquiry");
        LpbUtils.Package responseKeyData = new LpbUtils.Package(response.getDATA(), response.getKEY());

        responseXml = LpbUtils.decrypt(responseKeyData, privateKey);
        transactionLogger.error("-----inquiry response-----" + responseXml);
        responseVO = getCommResponseVO(responseXml);
        if (responseVO != null)
        {
            if ("000".equals(responseVO.getErrorCode()))
            {
                status = "success";
                remark = "Transaction successful";
            }
            else
            {
                status = "Failed";
                remark = "Transaction Failed";
            }
            authCode = responseVO.getAuthCode();
            transactionStatus = responseVO.getStatus();
            transactionDate = responseVO.getBankTransactionDate();
        }

        merchantOrderId = transDetailsVO.getOrderId();
        transactionId = transDetailsVO.getPreviousTransactionId();
        amount = transDetailsVO.getAmount();
        currency = transDetailsVO.getCurrency();

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();

        responseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
        responseVO.setMerchantId(mid);
        responseVO.setMerchantOrderId(merchantOrderId);
        responseVO.setTransactionId(transactionId);
        responseVO.setAuthCode(authCode);
        responseVO.setTransactionType(transactionType);
        responseVO.setTransactionStatus(transactionStatus);
        responseVO.setStatus(status);
        responseVO.setAmount(amount);
        responseVO.setCurrency(currency);
        responseVO.setBankTransactionDate(transactionDate);
        responseVO.setErrorCode(errorCode);
        responseVO.setDescription(remark);
        responseVO.setRemark(remark);
        return responseVO;
    }

    public GenericResponseVO processRebilling(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        errorCodeListVO.addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_RECURRINGALLOW));
        PZGenericConstraint genConstraint = new PZGenericConstraint("LpbPaymentGateway", "processRebilling", null, "common", "This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::", errorCodeListVO);
        throw new PZGenericConstraintViolationException(genConstraint, "This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::", null);
    }

    public GenericResponseVO process3DSaleConfirmation(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException
    {
        transactionLogger.error("-----Entered in process3DSaleConfirmation()-----");

        LPBResponseVO responseVO = null;
        String sign;
        LpbUtils.Package packet;

        LpbRequestVO lpbRequestVO = (LpbRequestVO) requestVO;
        CommTransactionDetailsVO genericTransactionDetailsVO = lpbRequestVO.getTransDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO=lpbRequestVO.getAddressDetailsVO();
        GatewayAccount account = GatewayAccountService.getGatewayAccount(accountId);

        String secondRequest =
                "<data>" +
                        "<Payment>" +
                        "<ID>" + genericTransactionDetailsVO.getPreviousTransactionId() + "</ID>" +
                        "</Payment>" +
                        "<D3D>" +
                        "<ACS>" +
                        "<PaRes>" + lpbRequestVO.getPaRes() + "</PaRes>" +
                        "</ACS>" +
                        "</D3D>" +
                        "</data>";


        transactionLogger.debug("-----LPB secondRequest----" +trackingID + "--" +  secondRequest);

        sign = LpbUtils.sign(secondRequest, privateKey);
        packet = LpbUtils.encrypt(secondRequest, publicKey);

        String responseXml;
        BigInteger index = new BigInteger("1");

        EcomPaymentType request = new EcomPaymentType(account.getMerchantId(), index, packet.key, packet.data, sign);

        EcomPaymentType response = processRequest(request, "confirmation");

        LpbUtils.Package responseKeyData = new LpbUtils.Package(response.getDATA(), response.getKEY());
        responseXml = LpbUtils.decrypt(responseKeyData, privateKey);
        transactionLogger.debug("LPB secondResponse----" +trackingID + "--" +  responseXml);
        responseVO = getCommResponseVO(responseXml);

        if (responseVO != null && "000".equals(responseVO.getErrorCode()))
        {
            responseVO.setStatus("success");
            responseVO.setDescription("Transaction successful.");

        }
        else
        {
            responseVO.setStatus("fail");
            responseVO.setDescription("Transaction failed");
        }

        responseVO.setCurrency(genericTransactionDetailsVO.getCurrency());
        responseVO.setTmpl_Amount(commAddressDetailsVO.getTmpl_amount());
        responseVO.setTmpl_Currency(commAddressDetailsVO.getTmpl_currency());
        return responseVO;
    }

    public GenericResponseVO processCommon3DSaleConfirmation(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException
    {
        transactionLogger.error("-----Entered in processCommon3DSaleConfirmation()-----");

        LPBResponseVO responseVO = null;
        String sign;
        LpbUtils.Package packet;

        Comm3DRequestVO comm3DRequestVO = (Comm3DRequestVO) requestVO;
        CommTransactionDetailsVO genericTransactionDetailsVO = comm3DRequestVO.getTransDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO=comm3DRequestVO.getAddressDetailsVO();
        GatewayAccount account = GatewayAccountService.getGatewayAccount(accountId);

        String secondRequest =
                "<data>" +
                        "<Payment>" +
                        "<ID>" + genericTransactionDetailsVO.getPreviousTransactionId() + "</ID>" +
                        "</Payment>" +
                        "<D3D>" +
                        "<ACS>" +
                        "<PaRes>" + comm3DRequestVO.getPaRes() + "</PaRes>" +
                        "</ACS>" +
                        "</D3D>" +
                        "</data>";


        transactionLogger.debug("-----LPB secondRequest----" + trackingID + "--" + secondRequest);

        sign = LpbUtils.sign(secondRequest, privateKey);
        packet = LpbUtils.encrypt(secondRequest, publicKey);

        String responseXml;
        BigInteger index = new BigInteger("1");

        EcomPaymentType request = new EcomPaymentType(account.getMerchantId(), index, packet.key, packet.data, sign);

        EcomPaymentType response = processRequest(request, "confirmation");

        LpbUtils.Package responseKeyData = new LpbUtils.Package(response.getDATA(), response.getKEY());
        responseXml = LpbUtils.decrypt(responseKeyData, privateKey);
        transactionLogger.debug("LPB secondResponse----" + trackingID + "--" + responseXml);
        responseVO = getCommResponseVO(responseXml);

        if (responseVO != null && "000".equals(responseVO.getErrorCode()))
        {
            responseVO.setStatus("success");
            responseVO.setDescription("Transaction successful.");

        }
        else
        {
            responseVO.setStatus("fail");
            responseVO.setDescription("Transaction failed");
        }

        responseVO.setCurrency(genericTransactionDetailsVO.getCurrency());
        responseVO.setTmpl_Amount(commAddressDetailsVO.getTmpl_amount());
        responseVO.setTmpl_Currency(commAddressDetailsVO.getTmpl_currency());
        return responseVO;
    }
    @Override
    public GenericResponseVO processCommon3DAuthConfirmation(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException, UnsupportedEncodingException, PZGenericConstraintViolationException
    {
        transactionLogger.error("-----Entered in processCommon3DAuthConfirmation()-----");

        LPBResponseVO responseVO = null;
        String sign;
        LpbUtils.Package packet;

        Comm3DRequestVO comm3DRequestVO = (Comm3DRequestVO) requestVO;
        CommTransactionDetailsVO genericTransactionDetailsVO = comm3DRequestVO.getTransDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO=comm3DRequestVO.getAddressDetailsVO();
        GatewayAccount account = GatewayAccountService.getGatewayAccount(accountId);

        String secondRequest =
                "<data>" +
                        "<Payment>" +
                        "<ID>" + genericTransactionDetailsVO.getPreviousTransactionId() + "</ID>" +
                        "</Payment>" +
                        "<D3D>" +
                        "<ACS>" +
                        "<PaRes>" + comm3DRequestVO.getPaRes() + "</PaRes>" +
                        "</ACS>" +
                        "</D3D>" +
                        "</data>";


        transactionLogger.debug("-----LPB secondRequest----" +trackingID + "--" +  secondRequest);

        sign = LpbUtils.sign(secondRequest, privateKey);
        packet = LpbUtils.encrypt(secondRequest, publicKey);

        String responseXml;
        BigInteger index = new BigInteger("1");

        EcomPaymentType request = new EcomPaymentType(account.getMerchantId(), index, packet.key, packet.data, sign);

        EcomPaymentType response = processRequest(request, "confirmation");

        LpbUtils.Package responseKeyData = new LpbUtils.Package(response.getDATA(), response.getKEY());
        responseXml = LpbUtils.decrypt(responseKeyData, privateKey);
        transactionLogger.debug("LPB secondResponse----" +trackingID + "--" +  responseXml);
        responseVO = getCommResponseVO(responseXml);

        if (responseVO != null && "000".equals(responseVO.getErrorCode()))
        {
            responseVO.setStatus("success");
            responseVO.setDescription("Transaction successful.");

        }
        else
        {
            responseVO.setStatus("fail");
            responseVO.setDescription("Transaction failed");
        }
        responseVO.setCurrency(genericTransactionDetailsVO.getCurrency());
        responseVO.setTmpl_Amount(commAddressDetailsVO.getTmpl_amount());
        responseVO.setTmpl_Currency(commAddressDetailsVO.getTmpl_currency());
        return responseVO;
    }

    private String getTransIdFromTrackingId(String trackingId) throws PZDBViolationException
    {
        Connection conn = null;
        String transId = null;
        try
        {
            conn = Database.getConnection();
            transId = null;
            String transaction = "SELECT responsetransactionid FROM transaction_common_details WHERE detailid = (SELECT MAX(detailid) FROM transaction_common_details WHERE trackingid=? AND responsetransactionid IS NOT NULL);";
            PreparedStatement transPreparedStatement = conn.prepareStatement(transaction);
            transPreparedStatement.setString(1, String.valueOf(trackingId));

            ResultSet rs = transPreparedStatement.executeQuery();
            if (rs.next())
            {
                transId = rs.getString("responsetransactionid");
            }
        }
        catch (SQLException e)
        {
            log.error("SqlException while getting the previous transaction details for Lpb", e);
            transactionLogger.error("SqlException while getting the previous transaction details for Lpb", e);

            PZExceptionHandler.raiseDBViolationException(LpbPaymentGateway.class.getName(), "getTransIdFromTrackingId()", null, "common", "SqlException while getting the previous transaction details for Lpb", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            log.error("System Error while getting the previous transaction details for Lpb", systemError);
            transactionLogger.error("System Error while getting the previous transaction details for Lpb", systemError);

            PZExceptionHandler.raiseDBViolationException(LpbPaymentGateway.class.getName(), "getTransIdFromTrackingId()", null, "common", "Syetem Error while getting the previous transaction details for Lpb", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return transId;
    }

    private EcomPaymentType processRequest(EcomPaymentType request, String operation) throws PZTechnicalViolationException
    {
        EcomBindingStub binding;
        EcomPaymentType response = null;
        GatewayAccount account = GatewayAccountService.getGatewayAccount(accountId);
        try
        {
            binding = (EcomBindingStub)
                    new EcomServiceLocator().getEcomPort();

            binding.setTimeout(60000);

            if (operation.equals("auth"))
            {
                response = binding.payment(request);
            }
            else if (operation.equals("sale"))
            {
                response = binding.payment(request);
            }
            else if (operation.equals("capture"))
            {
                response = binding.deposit(request);
            }
            else if (operation.equals("cancel"))
            {
                response = binding.reverse(request);
            }
            else if (operation.equals("refund"))
            {
                response = binding.reverse(request);
            }
            else if (operation.equals("inquiry"))
            {
                response = binding.getPayment(request);

            }
            else if (operation.equals("confirmation"))
            {
                response = binding.authenticate(request);
            }

        }
        catch (ServiceException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(LpbPaymentGateway.class.getName(), "processRequest()", null, "common", "Technical Exception while placing transaction", PZTechnicalExceptionEnum.SERVICE_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (RemoteException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(LpbPaymentGateway.class.getName(), "processRequest()", null, "common", "Technical Exception while placing transaction", PZTechnicalExceptionEnum.REMOTE_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        return response;
    }

    /**
     * Check the input values (to be updated later for 3D Secure case )
     *
     * @param trackingID
     * @param requestVO
     * @throws com.directi.pg.SystemError
     */
    private void validateForRefund(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException
    {
        if (trackingID == null || trackingID.equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(LpbPaymentGateway.class.getName(), "validateForRefund()", null, "common", "Tracking Id not provided while Refunding OR Capturing OR Authenticating transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, null, "Tracking Id not provided while Refunding transaction", new Throwable("Tracking Id not provided while Refunding transaction"));
        }

        if (requestVO == null)
        {
            PZExceptionHandler.raiseConstraintViolationException(LpbPaymentGateway.class.getName(), "validateForRefund()", null, "common", "Request  not provided while Refunding OR Capturing OR Authenticating transaction", PZConstraintExceptionEnum.VO_MISSING, null, "Request  not provided while Refunding transaction", new Throwable("Request  not provided while Refunding transaction"));
        }

        CommRequestVO comRequestVO = (CommRequestVO) requestVO;


        CommTransactionDetailsVO commTransactionDetailsVO = comRequestVO.getTransDetailsVO();
        if (commTransactionDetailsVO == null)
        {
            PZExceptionHandler.raiseConstraintViolationException(LpbPaymentGateway.class.getName(), "validateForRefund()", null, "common", "TransactionDetails  not provided while Refunding OR Capturing OR Authenticating transaction", PZConstraintExceptionEnum.VO_MISSING, null, "TransactionDetails  not provided while Refunding transaction", new Throwable("TransactionDetails  not provided while Refunding transaction"));
        }
        if (commTransactionDetailsVO.getAmount() == null || commTransactionDetailsVO.getAmount().equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(LpbPaymentGateway.class.getName(), "validateForRefund()", null, "common", "Amount not provided while Refunding OR Capturing OR Authenticating transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, null, "Amount not provided while Refunding transaction", new Throwable("Amount not provided while Refunding transaction"));
        }

        if (commTransactionDetailsVO.getPreviousTransactionId() == null || commTransactionDetailsVO.getPreviousTransactionId().equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(LpbPaymentGateway.class.getName(), "validateForRefund()", null, "common", "Previous Transaction ID not provided while Refunding OR Capturing OR Authenticating transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, null, "Previous Transaction Id not provided while Refunding transaction", new Throwable("Previous Transaction Id not provided while Refunding transaction"));
        }

    }

    /**
     * Check the input values (to be updated later for 3D Secure case )
     *
     * @param trackingID
     * @param requestVO
     * @throws com.directi.pg.SystemError
     */
    private void validateForSale(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException
    {
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
        if (trackingID == null || trackingID.equals(""))
        {
            errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_TRACKINGID);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(LpbPaymentGateway.class.getName(), "validateForSale()", null, "common", "TrackingId not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, errorCodeListVO, "TrackingId not provided while placing transaction", new Throwable("TrackingId not provided while placing transaction"));
        }

        if (requestVO ==null)
        {
            errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_REQUEST_NULL);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(LpbPaymentGateway.class.getName(), "validateForSale()", null, "common", "Request  not provided while placing transaction", PZConstraintExceptionEnum.VO_MISSING, errorCodeListVO, "Request  not provided while placing transaction", new Throwable("Request  not provided while placing transaction"));
        }

        CommRequestVO comRequestVO = (CommRequestVO) requestVO;


        GenericTransDetailsVO genericTransDetailsVO = comRequestVO.getTransDetailsVO();
        if (genericTransDetailsVO ==null)
        {
            errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_REQUEST_NULL);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(LpbPaymentGateway.class.getName(), "validateForSale()", null, "common", "TransactionDetails  not provided while placing transaction", PZConstraintExceptionEnum.VO_MISSING, errorCodeListVO, "TransactionDetails  not provided while placing transaction", new Throwable("TransactionDetails  not provided while placing transaction"));
        }
        if (genericTransDetailsVO.getAmount() == null || genericTransDetailsVO.getAmount().equals(""))
        {
            errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_AMOUNT);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(LpbPaymentGateway.class.getName(), "validateForSale()", null, "common", "Amount not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, errorCodeListVO, "Amount not provided while placing transaction", new Throwable("Amount not provided while placing transaction"));
        }

        GenericAddressDetailsVO genericAddressDetailsVO = comRequestVO.getAddressDetailsVO();

        if (genericAddressDetailsVO ==null)
        {
            errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_REQUEST_NULL);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(LpbPaymentGateway.class.getName(), "validateForSale()", null, "common", "AddressDetails  not provided while placing transaction", PZConstraintExceptionEnum.VO_MISSING, errorCodeListVO, "AddressDetails  not provided while placing transaction", new Throwable("AddressDetails  not provided while placing transaction"));
        }
        //User Details
        if (genericAddressDetailsVO.getFirstname() == null || genericAddressDetailsVO.getFirstname().equals(""))
        {
            errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_FIRSTNAME);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(LpbPaymentGateway.class.getName(), "validateForSale()", null, "common", "First Name not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, errorCodeListVO, "First Name not provided while placing transaction", new Throwable("First Name not provided while placing transaction"));

        }
        if (genericAddressDetailsVO.getLastname() == null || genericAddressDetailsVO.getLastname().equals(""))
        {
            errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_LASTNAME);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(LpbPaymentGateway.class.getName(), "validateForSale()", null, "common", "Last Name not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, errorCodeListVO, "Last Name not provided while placing transaction", new Throwable("Last Name not provided while placing transaction"));

        }

        if (genericAddressDetailsVO.getEmail() == null || genericAddressDetailsVO.getEmail().equals(""))
        {
            errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_EMAIL);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(LpbPaymentGateway.class.getName(), "validateForSale()", null, "common", "Email Id not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, errorCodeListVO, "Email Id not provided while placing transaction", new Throwable("Email Id not provided while placing transaction"));
        }


        if (genericAddressDetailsVO.getIp() == null || genericAddressDetailsVO.getIp().equals(""))
        {
            errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_IPADDRESS);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(LpbPaymentGateway.class.getName(), "validateForSale()", null, "common", "IP address not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, errorCodeListVO, "IP Address not provided while placing transaction", new Throwable("IP Address not provided while placing transaction"));
        }


        //Address Details
        if (genericAddressDetailsVO.getStreet() == null || genericAddressDetailsVO.getStreet().equals(""))
        {
            errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_STREET);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(LpbPaymentGateway.class.getName(), "validateForSale()", null, "common", "Street not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, errorCodeListVO, "Street not provided while placing transaction", new Throwable("Street not provided while placing transaction"));
        }

        if (genericAddressDetailsVO.getCity() == null || genericAddressDetailsVO.getCity().equals(""))
        {
            errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_CITY);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(LpbPaymentGateway.class.getName(), "validateForSale()", null, "common", "City not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, errorCodeListVO, "City not provided while placing transaction", new Throwable("City not provided while placing transaction"));
        }


        if (genericAddressDetailsVO.getCountry() == null || genericAddressDetailsVO.getCountry().equals(""))
        {
            errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_COUNTRY_CODE);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(LpbPaymentGateway.class.getName(), "validateForSale()", null, "common", "Country not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, errorCodeListVO, "Country not provided while placing transaction", new Throwable("Country not provided while placing transaction"));
        }

        if (genericAddressDetailsVO.getState() == null || genericAddressDetailsVO.getState().equals(""))
        {
            errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_STATE);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(LpbPaymentGateway.class.getName(), "validateForSale()", null, "common", "State not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, errorCodeListVO, "State not provided while placing transaction", new Throwable("State not provided while placing transaction"));
        }

        if (genericAddressDetailsVO.getZipCode() == null || genericAddressDetailsVO.getZipCode().equals(""))
        {
            errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_ZIP);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(LpbPaymentGateway.class.getName(), "validateForSale()", null, "common", "Zip code not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, errorCodeListVO, "Zip code not provided while placing transaction", new Throwable("Zip Code not provided while placing transaction"));
        }


        GenericCardDetailsVO genericCardDetailsVO = comRequestVO.getCardDetailsVO();

        if (genericCardDetailsVO == null)
        {
            errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_REQUEST_NULL);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(LpbPaymentGateway.class.getName(), "validateForSale()", null, "common", "CardDetails  not provided while placing transaction", PZConstraintExceptionEnum.VO_MISSING, errorCodeListVO, "CardDetails  not provided while placing transaction", new Throwable("CardDetails  not provided while placing transaction"));
        }
        //Card Details

        if (genericCardDetailsVO.getCardNum() == null || genericCardDetailsVO.getCardNum().equals(""))
        {
            errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_REQUEST_NULL);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(LpbPaymentGateway.class.getName(), "validateForSale()", null, "common", "Card NO not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, errorCodeListVO, "Card NO not provided while placing transaction", new Throwable("Card NO not provided while placing transaction"));
        }

        String ccnum = genericCardDetailsVO.getCardNum();

        if (ccnum != null && !Functions.isValid(ccnum))
        {
            errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_REQUEST_NULL);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(LpbPaymentGateway.class.getName(), "validateForSale()", null, "common", "Card NO provided is invalid while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, errorCodeListVO, "Card NO provided is invalid while placing transaction", new Throwable("Card NO provided is invalid while placing transaction"));
        }


        if (genericCardDetailsVO.getcVV() == null || genericCardDetailsVO.getcVV().equals(""))
        {
            errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_CVV);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(LpbPaymentGateway.class.getName(), "validateForSale()", null, "common", "CVV not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, errorCodeListVO, "CVV not provided while placing transaction", new Throwable("CVV not provided while placing transaction"));
        }


        if (genericCardDetailsVO.getExpMonth() == null || genericCardDetailsVO.getExpMonth().equals(""))
        {
            errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_EXP_MONTH);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(LpbPaymentGateway.class.getName(), "validateForSale()", null, "common", "Expiry Month not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, errorCodeListVO, "Expiry Month not provided while placing transaction", new Throwable("Expiry Month not provided while placing transaction"));
        }


        if (genericCardDetailsVO.getExpYear() == null || genericCardDetailsVO.getExpYear().equals(""))
        {
            errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_EXP_YEAR);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(LpbPaymentGateway.class.getName(), "validateForSale()", null, "common", "Expiry Year not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, errorCodeListVO, "Expiry Year not provided while placing transaction", new Throwable("Expiry Year not provided while placing transaction"));
        }

    }

}
