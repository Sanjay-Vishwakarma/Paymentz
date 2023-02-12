package com.payment.visaNet;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.GatewayType;
import com.directi.pg.core.GatewayTypeService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.*;
import com.manager.dao.TransactionDAO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.common.core.CommMerchantVO;
import com.payment.common.core.CommRequestVO;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraint.PZGenericConstraint;
import com.payment.exceptionHandler.constraintType.PZConstraintExceptionEnum;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import com.payment.utils.SSLUtils;
import com.payment.visaNet.com_auth.WsautorizarLocator;
import com.payment.visaNet.com_auth.WsautorizarSoap12Stub;
import com.payment.visaNet.com_cancel.WsanularLocator;
import com.payment.visaNet.com_cancel.WsanularSoap12Stub;
import com.payment.visaNet.com_capture.WsdepositarLocator;
import com.payment.visaNet.com_capture.WsdepositarSoap12Stub;
import javax.xml.rpc.ServiceException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.ResourceBundle;

/**
 * Created with IntelliJ IDEA.
 * User: sandip
 * Date: 4/15/15
 * Time: 12:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class VisaNetPaymentGateway extends AbstractPaymentGateway
{
    public static final String GATEWAY_TYPE = "visanet";
    final static ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.visanet");
   // private static Logger log = new Logger(VisaNetPaymentGateway.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(VisaNetPaymentGateway.class.getName());
    public VisaNetUtils utils = new VisaNetUtils();
    public VisaNetPaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }

    public static String round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_DOWN);
        return bd.toString();
    }

    @Override
    public String getMaxWaitDays()
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        errorCodeListVO.addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_FUNCTIONALITY_NOT_ALLOWED));
        PZGenericConstraint genConstraint = new PZGenericConstraint("VisaNetPaymentGateway","processSale",null,"common","This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",errorCodeListVO);
        throw new PZGenericConstraintViolationException(genConstraint,"This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",null);
    }
    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionLogger.debug("Entering processAuthentication of VisaNetPaymentGateway...");

        SSLUtils.setupSecurityProvider();
        SSLUtils.trustAllHttpsCertificates();
        System.setProperty("java.protocol.handler.pkgs", "com.sun.net.ssl.internal.www.protocol");
        java.security.Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());


        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        VisaNetResponseVO visaNetResponseVO = new VisaNetResponseVO();

        GenericTransDetailsVO genericTransDetailsVO = commRequestVO.getTransDetailsVO();
        GenericCardDetailsVO genericCardDetailsVO = commRequestVO.getCardDetailsVO();
        GenericAddressDetailsVO genericAddressDetailsVO = commRequestVO.getAddressDetailsVO();
        CommMerchantVO commMerchantVO = commRequestVO.getCommMerchantVO();

        GatewayAccount account = GatewayAccountService.getGatewayAccount(accountId);
        GatewayType gatewayType = GatewayTypeService.getGatewayType(account.getPgTypeId());

        String isPSTProcessingRequest = commRequestVO.getIsPSTProcessingRequest();

        String CODTIENDA = "";
        String CODASOCIADO = "";
        String NOMBREASOCIADO = "";
        String MCC = null;

        String pspString = "";
        transactionLogger.error("isPSTProcessingRequest====" + isPSTProcessingRequest);
        if ("Y".equals(isPSTProcessingRequest))
        {
            CODTIENDA = commMerchantVO.getMerchantId();
        }
        else
        {
            CODTIENDA = gatewayType.getPspCode();
            CODASOCIADO = commMerchantVO.getMerchantId();
            NOMBREASOCIADO = commMerchantVO.getMerchantUsername();
            MCC = account.getAliasName();

            pspString = "<parametro id=\"CODASOCIADO\">" + CODASOCIADO + "</parametro>" +
                    "<parametro id=\"NOMBREASOCIADO\">" + NOMBREASOCIADO + "</parametro>" +
                    "<parametro id=\"MCC\">" + MCC + "</parametro>";
        }
        //

        String NOMBRE = genericAddressDetailsVO.getFirstname();
        String APELLIDO = genericAddressDetailsVO.getLastname();

        if ("culqi".equalsIgnoreCase(NOMBRE))
        {
            NOMBRE = "";
        }
        if ("peru".equalsIgnoreCase(APELLIDO))
        {
            APELLIDO = "";
        }

        String requestXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<registrar_pedido>" +
                "<parametros>" +
                "<parametro id=\"CODTIENDA\">" + CODTIENDA + "</parametro>" +
                "<parametro id=\"NUMORDEN\">"+trackingID+"</parametro>" +
                "<parametro id=\"MOUNT\">"+round(Double.valueOf(genericTransDetailsVO.getAmount()),2)+"</parametro>" +
                "<parametro id=\"PAN\">"+genericCardDetailsVO.getCardNum()+"</parametro>" +
                "<parametro id=\"EXPIRYYEAR\">"+genericCardDetailsVO.getExpYear()+"</parametro>" +
                "<parametro id=\"EXPIRYMONTH\">"+genericCardDetailsVO.getExpMonth()+"</parametro>" +
                "<parametro id=\"CSCCODE\">" + genericCardDetailsVO.getcVV() + "</parametro>" +
                "<parametro id=\"CAVV\">0000000000000000000000000000000000000000</parametro>" +
                "<parametro id=\"ECI\">07</parametro>" +
                "<parametro id=\"XID\">00000000000000000000</parametro>" +
                "<parametro id=\"STATEFLOW\">14000</parametro>" +
                "<parametro id=\"CURRENCY\">"+utils.getVisaNetCurrencyCode(utils.getPZTransactionCurrency(genericTransDetailsVO.getCurrency()))+"</parametro>" +
                "<parametro id=\"NOMBRE\">" + NOMBRE + "</parametro>" +
                "<parametro id=\"APELLIDO\">" + APELLIDO + "</parametro>" +
                "<parametro id=\"DIRECCION\">"+genericAddressDetailsVO.getStreet()+"</parametro>" +
                "<parametro id=\"CIUDAD\">"+genericAddressDetailsVO.getCity()+"</parametro>" +
                "<parametro id=\"EMAIL\">"+genericAddressDetailsVO.getEmail()+"</parametro>" +
                "<parametro id=\"CODIGO_POSTAL\">"+genericAddressDetailsVO.getZipCode()+"</parametro>" +
                "<parametro id=\"ESTADO\">"+genericAddressDetailsVO.getState()+"</parametro>" +
                "<parametro id=\"CODPAIS\">"+genericAddressDetailsVO.getCountry()+"</parametro>" +
                "" + pspString + "" +
                /*"<parametro id=\"CODASOCIADO\">" + CODASOCIADO + "</parametro>" +
                "<parametro id=\"NOMBREASOCIADO\">" + NOMBREASOCIADO + "</parametro>" +
                "<parametro id=\"MCC\">" + MCC + "</parametro>" +*/
                "</parametros>" +
                "</registrar_pedido>";



        //

        String logRequestData="<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
                "<registrar_pedido>"+
                "<parametros>"+
                "<parametro id=\"CODTIENDA\">" + CODTIENDA + "</parametro>" +
                "<parametro id=\"NUMORDEN\">"+trackingID+"</parametro>"+
                "<parametro id=\"MOUNT\">"+round(Double.valueOf(genericTransDetailsVO.getAmount()),2)+"</parametro>"+
                "<parametro id=\"CAVV\">0000000000000000000000000000000000000000</parametro>"+
                "<parametro id=\"ECI\">07</parametro>"+
                "<parametro id=\"XID\">00000000000000000000</parametro>"+
                "<parametro id=\"STATEFLOW\">14000</parametro>"+
                "<parametro id=\"CURRENCY\">"+utils.getVisaNetCurrencyCode(utils.getPZTransactionCurrency(genericTransDetailsVO.getCurrency()))+"</parametro>"+
                "<parametro id=\"NOMBRE\">" + NOMBRE + "</parametro>" +
                "<parametro id=\"APELLIDO\">" + APELLIDO + "</parametro>" +
                "<parametro id=\"DIRECCION\">"+genericAddressDetailsVO.getStreet()+"</parametro>"+
                "<parametro id=\"CIUDAD\">"+genericAddressDetailsVO.getCity()+"</parametro>"+
                "<parametro id=\"EMAIL\">"+genericAddressDetailsVO.getEmail()+"</parametro>"+
                "<parametro id=\"CODIGO_POSTAL\">"+genericAddressDetailsVO.getZipCode()+"</parametro>"+
                "<parametro id=\"ESTADO\">"+genericAddressDetailsVO.getState()+"</parametro>"+
                "<parametro id=\"CODPAIS\">"+genericAddressDetailsVO.getCountry()+"</parametro>"+
                "" + pspString + "" +
                /*"<parametro id=\"CODASOCIADO\">" + CODASOCIADO + "</parametro>" +
                "<parametro id=\"NOMBREASOCIADO\">" + NOMBREASOCIADO + "</parametro>" +
                "<parametro id=\"MCC\">" + MCC + "</parametro>" +*/

                "</parametros>"+
                "</registrar_pedido>";

        //transactionLogger.error("=====request xml===="+logRequestData);
        String responseXML = "";
        try
        {
            boolean isTestAccount=getAccountType(accountId);
            WsautorizarSoap12Stub wsautorizarSoap12Stub = null;
            WsautorizarLocator locator=new WsautorizarLocator();

            if(isTestAccount)
            {
              locator.setWsautorizarSoap12_address(RB.getString("testauthurl"));
              locator.setWsautorizarSoap_address(RB.getString("testauthurl"));
            }
            else
            {
             locator.setWsautorizarSoap12_address(RB.getString("liveauthurl"));
             locator.setWsautorizarSoap_address(RB.getString("liveauthurl"));
            }


            //

            //todo gatewayCall
            Date date104=new Date();

            wsautorizarSoap12Stub = (WsautorizarSoap12Stub)locator.getwsautorizarSoap12();
            transactionLogger.error("-------process authentication request XML-----" + logRequestData);
            transactionLogger.error("-------process authentication request XML1-----" + requestXML);
            responseXML = wsautorizarSoap12Stub.autorizarTransaccion(requestXML);
            transactionLogger.error("------process authentication response XML---" + responseXML);


            HashMap hashMap=utils.readVisaNetResponse(responseXML);
            TransactionDetailsVO vCurrentTransaction=null;
            Functions functions=new Functions();
            String descriptor=GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
            String successCode="";
            String status="";
            String description="";
            String paymentId="";
            String remark="";

            String authCode="";
            String resultCode="";
            String resultDescription="";
            String cardSource="";
            String cardIssuerName="";
            String ECI="0";
            String ECIDescription="";
            String cvvResult="";
            String txAcqId="";
            String bankTransDate="";
            String validationDescription="";

            if(hashMap!=null)
            {
                TransactionDAO tDAO = new TransactionDAO();
                vCurrentTransaction = tDAO.getDetailFromCommon(trackingID);

                if("000".equals(hashMap.get("CODACCION")) && "1".equals(hashMap.get("RESPUESTA"))){
                    status="success";
                    description="Transaction  successful";
                    remark="Transaction Successful";}
                else{
                    status="fail";
                    description="Transaction  Failed";
                    if(hashMap.get("1")!=null){
                        remark=(String)hashMap.get("1");
                    }else{remark="Bank connectivity issue";}
                }
                if(hashMap.get("CODACCION")!=null){
                    successCode=(String)hashMap.get("CODACCION");
                    resultCode=(String)hashMap.get("CODACCION");
                }if(hashMap.get("COD_AUTORIZA")!=null){
                    authCode=(String)hashMap.get("COD_AUTORIZA");
                    paymentId=(String)hashMap.get("COD_AUTORIZA");
                }if(hashMap.get("DSC_COD_ACCION")!=null){
                    resultDescription=(String)hashMap.get("DSC_COD_ACCION");
                }if(hashMap.get("ORI_TARJETA")!=null){
                    cardSource=(String)hashMap.get("ORI_TARJETA");
                }if(hashMap.get("NOM_EMISOR")!=null){
                    cardIssuerName=(String)hashMap.get("NOM_EMISOR");
                }if(functions.isValueNull((String)hashMap.get("ECI"))){
                    ECI=(String)hashMap.get("ECI");
                }if(hashMap.get("DSC_ECI")!=null){
                    ECIDescription=(String)hashMap.get("DSC_ECI");
                }if(hashMap.get("RES_CVV2")!=null){
                    cvvResult=(String)hashMap.get("RES_CVV2");
                }if(hashMap.get("ID_UNICO")!=null){
                    txAcqId=(String)hashMap.get("ID_UNICO");
                }if(hashMap.get("FECHAYHORA_TX")!=null){
                    bankTransDate=(String)hashMap.get("FECHAYHORA_TX");
                }if(hashMap.get("1")!=null){
                validationDescription = validationDescription + " " + hashMap.get("1");
                }if(hashMap.get("2")!=null){
                validationDescription = validationDescription + " " + hashMap.get("2");
                }if(hashMap.get("3")!=null){
                validationDescription = validationDescription + " " + hashMap.get("3");
                }
            }

            visaNetResponseVO.setStatus(status);
            visaNetResponseVO.setDescription(description);
            visaNetResponseVO.setTransactionType("auth");
            visaNetResponseVO.setDescriptor(descriptor);
            visaNetResponseVO.setTransactionStatus(vCurrentTransaction.getStatus());
            visaNetResponseVO.setErrorCode(successCode);
            visaNetResponseVO.setTransactionId(paymentId);
            visaNetResponseVO.setRemark(remark);

            visaNetResponseVO.setAuthCode(authCode);
            visaNetResponseVO.setResultCode(resultCode);
            visaNetResponseVO.setResultDescription(resultDescription);
            visaNetResponseVO.setCardSource(cardSource);
            visaNetResponseVO.setCardIssuerName(cardIssuerName);
            visaNetResponseVO.setECI(ECI);
            visaNetResponseVO.setECIDescription(ECIDescription);
            visaNetResponseVO.setCvvResult(cvvResult);
            visaNetResponseVO.setCvvResult(cvvResult);
            visaNetResponseVO.setTxAcqId(txAcqId);
            visaNetResponseVO.setBankTransDate(bankTransDate);
            visaNetResponseVO.setValidationDescription(validationDescription);

            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            visaNetResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));


            //

        }
        catch (ServiceException se)
        {
            transactionLogger.error("Exception while connecting with bank", se);
            PZExceptionHandler.raiseTechnicalViolationException("VisaNetPaymentGateway.java", "processAuthentication()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.SERVICE_EXCEPTION, null, se.getMessage(), se.getCause());
        }
        catch (RemoteException re)
        {
            transactionLogger.error("Exception while connecting with bank", re);
            PZExceptionHandler.raiseTechnicalViolationException("VisaNetPaymentGateway.java", "processAuthentication()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.REMOTE_EXCEPTION, null, re.getMessage(), re.getCause());
        }
        return visaNetResponseVO;
    }

    public GenericResponseVO processCapture(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionLogger.debug("Entering processCapture of VisaNetPaymentGateway...");

        SSLUtils.setupSecurityProvider();
        SSLUtils.trustAllHttpsCertificates();
        System.setProperty("java.protocol.handler.pkgs", "com.sun.net.ssl.internal.www.protocol");
        java.security.Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());

        CommRequestVO commRequestVO  =    (CommRequestVO)requestVO;
        VisaNetResponseVO  commResponseVO=new VisaNetResponseVO();
        CommMerchantVO commMerchantVO = commRequestVO.getCommMerchantVO();
        GenericTransDetailsVO genericTransDetailsVO=commRequestVO.getTransDetailsVO();

        GatewayAccount  account=GatewayAccountService.getGatewayAccount(commMerchantVO.getAccountId());
        GatewayType gatewayType = GatewayTypeService.getGatewayType(account.getPgTypeId());


        String isPSTProcessingRequest = commRequestVO.getIsPSTProcessingRequest();
        //System.out.println("isPSTProcessingRequest========"+isPSTProcessingRequest);
        String CODTIENDA="";
        if ("Y".equals(isPSTProcessingRequest))
        {
            CODTIENDA=commMerchantVO.getMerchantId();
        }
        else
        {
            CODTIENDA=gatewayType.getPspCode();
        }

        try
        {
            boolean isTestAccount=getAccountType(commMerchantVO.getAccountId());
            WsdepositarSoap12Stub wsdepositarSoap12Stub=null;
            WsdepositarLocator locator=new WsdepositarLocator();
            if(isTestAccount)
            {
              locator.setWsdepositarSoap12_address(RB.getString("testdepositurl"));
              locator.setWsdepositarSoap_address(RB.getString("testdepositurl"));
            }
            else
            {
                locator.setWsdepositarSoap12_address(RB.getString("livedepositurl"));
                locator.setWsdepositarSoap_address(RB.getString("livedepositurl"));
            }
            wsdepositarSoap12Stub=(WsdepositarSoap12Stub)locator.getwsdepositarSoap12();

            String requestXML="<?xml version=\"1.0\" encoding=\"UTF-8\" ?>"+
                    "<depositar_pedido>"+
                    "<parametros>"+
                    "<parametro id=\"CODTIENDA\">"+CODTIENDA+"</parametro>"+
                    "<parametro id=\"USUARIO\">"+account.getFRAUD_FILE_SHORT_NAME()+"</parametro>"+
                    "<parametro id=\"PASSWORD\">"+account.getPassword()+"</parametro>"+
                    "<parametro id=\"NUMORDEN\">"+trackingID+"</parametro>"+
                    "<parametro id=\"MONTODEPOSITO\">"+genericTransDetailsVO.getAmount()+"</parametro>"+
                    "</parametros>"+
                    "</depositar_pedido>";

            String responseXML="";
            String resultCode="";
            String resultDescription="";
            String lote="";
            String status ="";
            String bankStatus="";
            String description="";

            transactionLogger.error("------capture request-----" + requestXML);
            responseXML = wsdepositarSoap12Stub.depositarPedido(requestXML);
            transactionLogger.error("------capture response---" + responseXML);
            HashMap hashMap=utils.readVisaNetResponse(responseXML);

            if(hashMap!=null){
                if("1".equals(hashMap.get("RESPUESTA"))){
                    status = "success";
                    description="Transaction  successful";
                    resultCode=(String)hashMap.get("RESPUESTA");
                    resultDescription=(String)hashMap.get("1");
                    lote=(String)hashMap.get("LOTE");
                    bankStatus=(String)hashMap.get("ESTADO");
                }
                else{
                    status="fail";
                    description="Your order failed to capture";
                    resultCode=(String)hashMap.get("RESPUESTA");
                    resultDescription=(String)hashMap.get("1");
                    lote=(String)hashMap.get("LOTE");
                    bankStatus=(String)hashMap.get("ESTADO");
                }
            }
            else{
                status="fail";
                description="Bank connectivity issue";
                resultCode=null;
                resultDescription="Bank connectivity issue";
            }

            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();

            commResponseVO.setStatus(status);
            commResponseVO.setDescription(description);
            commResponseVO.setResultCode(resultCode);
            commResponseVO.setResultDescription(resultDescription);
            commResponseVO.setBankTransStatus(bankStatus);
            commResponseVO.setLote(lote);
            commResponseVO.setTransactionType("Capture");
            commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
        }
        catch (ServiceException se)
        {
            PZExceptionHandler.raiseTechnicalViolationException("VisaNetPaymentGateway.java", "processCapture()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.SERVICE_EXCEPTION, null, se.getMessage(), se.getCause());
        }
        catch (RemoteException re)
        {
            PZExceptionHandler.raiseTechnicalViolationException("VisaNetPaymentGateway.java", "processCapture()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.REMOTE_EXCEPTION, null, re.getMessage(), re.getCause());
        }
        return commResponseVO;
    }

    public GenericResponseVO processVoid(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionLogger.debug("Entering processVoid of VisaNet...");

        SSLUtils.setupSecurityProvider();
        SSLUtils.trustAllHttpsCertificates();
        System.setProperty("java.protocol.handler.pkgs", "com.sun.net.ssl.internal.www.protocol");
        java.security.Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());

        CommRequestVO commRequestVO  =    (CommRequestVO)requestVO;
        VisaNetResponseVO commResponseVO=new VisaNetResponseVO();
        CommMerchantVO commMerchantVO = commRequestVO.getCommMerchantVO();

        //String username = commMerchantVO.getMerchantUsername();
        //String password = commMerchantVO.getPassword();

        GatewayAccount  account=GatewayAccountService.getGatewayAccount(commMerchantVO.getAccountId());
        GatewayType gatewayType = GatewayTypeService.getGatewayType(account.getPgTypeId());

        String isPSTProcessingRequest = commRequestVO.getIsPSTProcessingRequest();
        String CODTIENDA="";
        if ("Y".equals(isPSTProcessingRequest))
        {
            CODTIENDA=commMerchantVO.getMerchantId();
        }
        else
        {
            CODTIENDA=gatewayType.getPspCode();
        }

        try
        {
            String requestXML="<?xml version=\"1.0\" encoding=\"UTF-8\" ?>"+
                    "<anular_pedido>" +
                    "<parametros>" +
                    "<parametro id=\"CODTIENDA\">"+CODTIENDA+"</parametro>"+
                    "<parametro id=\"USUARIO\">"+account.getFRAUD_FILE_SHORT_NAME()+"</parametro>"+
                    "<parametro id=\"PASSWORD\">"+account.getPassword()+"</parametro>"+
                    "<parametro id=\"NUMORDEN\">"+trackingID+"</parametro>"+
                    "</parametros>" +
                    "</anular_pedido>";

            String responseXML="";
            String status ="";
            String bankStatus="";
            String errorCode="";
            String resultCode="";
            String description="";
            String resultDescription="";

            boolean isTestAccount=getAccountType(commMerchantVO.getAccountId());
            WsanularSoap12Stub wsanularSoap12Stub=null;
            WsanularLocator locator=new WsanularLocator();
            if(isTestAccount)
            {
                locator.setWsanularSoap12_address(RB.getString("testvoidurl"));
                locator.setWsanularSoap_address(RB.getString("testvoidurl"));
            }
            else
            {
               locator.setWsanularSoap12_address(RB.getString("livevoidurl"));
               locator.setWsanularSoap_address(RB.getString("livevoidurl"));
            }
            wsanularSoap12Stub=(WsanularSoap12Stub)locator.getwsanularSoap12();

            transactionLogger.error("-----void request XML-----" + requestXML);
            responseXML = wsanularSoap12Stub.anularPedido(requestXML);
            transactionLogger.error("------void response XML-----" + responseXML);
            HashMap hashMap=utils.readVisaNetResponse(responseXML);
            if(hashMap !=null)
            {
                if("1".equals(hashMap.get("RESPUESTA"))){
                    status = "success";
                    description="Your transaction is cancelled successfully";
                    errorCode=(String)hashMap.get("RESPUESTA");
                    resultCode=(String)hashMap.get("RESPUESTA");
                    resultDescription=(String)hashMap.get("1");
                    bankStatus=(String)hashMap.get("ESTADO");
                }
                else{
                    status = "fail";
                    description="Your transaction is cancellation failed";
                    if(hashMap.get("RESPUESTA")!=null){
                        errorCode=hashMap.get("RESPUESTA").toString();
                        resultCode=(String)hashMap.get("RESPUESTA");
                        resultDescription=(String)hashMap.get("1");
                        bankStatus=(String)hashMap.get("ESTADO");
                    }
                }
            }
            else{
                status="fail";
                description="Bank connectivity issue";
                errorCode=null;
                resultCode=null;
                resultDescription="Bank connectivity issue";
            }

            commResponseVO.setStatus(status);
            commResponseVO.setBankTransStatus(bankStatus);
            commResponseVO.setDescription(description);
            commResponseVO.setResultDescription(resultDescription);
            commResponseVO.setErrorCode(errorCode);
            commResponseVO.setResultCode(resultCode);
            commResponseVO.setTransactionType("Cancel");
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
        }
        catch (ServiceException se)
        {
            PZExceptionHandler.raiseTechnicalViolationException("VisaNetPaymentGateway.java", "processVoid()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.SERVICE_EXCEPTION,null, se.getMessage(), se.getCause());
        }
        catch (RemoteException re)
        {
            PZExceptionHandler.raiseTechnicalViolationException("VisaNetPaymentGateway.java", "processVoid()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.REMOTE_EXCEPTION,null, re.getMessage(), re.getCause());
        }
        return commResponseVO;
    }

    public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException
    {
        PZExceptionHandler.raiseConstraintViolationException("VisaNetPaymentGateway","processRefund()",null,"common","Functionality not allowed for supporting Gateway", PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,null,null,null);
        return null;
    }

    public GenericResponseVO processRebilling(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        PZGenericConstraint genConstraint = new PZGenericConstraint("AlliedWalletPaymentGateway","processAuthentication",null,"common","This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",null);
        throw new PZGenericConstraintViolationException(genConstraint,"This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",null);
    }

    public boolean getAccountType(String accountId)
    {
        Connection con = null;
        PreparedStatement preparedStatement=null;
        ResultSet rs=null;
        boolean accountType=true;
        try
        {
            con= Database.getConnection();
            StringBuffer stringBuffer =new StringBuffer("select istest from gateway_accounts where accountid=?");
            preparedStatement=con.prepareStatement(stringBuffer.toString());
            preparedStatement.setString(1,accountId);
            rs=preparedStatement.executeQuery();
            if(rs.next())
            {
                if("N".equals(rs.getString("istest")))
                {
                    accountType=false;
                }
            }
        }
        catch (SystemError systemError)
        {
            transactionLogger.error("SystemError:::::",systemError);
        }
        catch (SQLException e)
        {
            transactionLogger.error("SQLException:::::::",e);
        }
        finally
        {
            Database.closeConnection(con);
        }
        return accountType;
    }
}