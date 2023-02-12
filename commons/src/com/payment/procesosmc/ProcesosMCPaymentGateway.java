package com.payment.procesosmc;

import TDES.TDESBase64;
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
import com.payment.procesosmc.org.tempuri.BasicHttpBinding_IsrvprocesoStub;
import com.payment.procesosmc.org.tempuri.auth.SrvprocesoLocator;
import javax.xml.rpc.ServiceException;
import java.io.UnsupportedEncodingException;
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
 * Date: 5/8/15
 * Time: 03:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class ProcesosMCPaymentGateway extends AbstractPaymentGateway
{
    public static final String GATEWAY_TYPE = "procesosmc";
    final static ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.pmc");
    //private static Logger log = new Logger(ProcesosMCPaymentGateway.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(ProcesosMCPaymentGateway.class.getName());
    ProcesosMCUtils utils=new ProcesosMCUtils();
    public ProcesosMCPaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }

    public static String round(double value, int places)
    {
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

    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionLogger.debug("Entering processAuthentication of ProcesosMCPaymentGateway...");


        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        ProcesosMCResponseVO procesosMCResponseVO=new ProcesosMCResponseVO();


        GenericTransDetailsVO genericTransDetailsVO = commRequestVO.getTransDetailsVO();
        GenericCardDetailsVO genericCardDetailsVO = commRequestVO.getCardDetailsVO();
        CommMerchantVO commMerchantVO = commRequestVO.getCommMerchantVO();

        GatewayAccount account=GatewayAccountService.getGatewayAccount(accountId);
        GatewayType gatewayType = GatewayTypeService.getGatewayType(account.getPgTypeId());

        String merchantId=account.getMerchantId();/*commMerchantVO.getMerchantId();*/
        //String merchantKey=gatewayType.getKey();/*utils.getPaymentFacilitatorKey(merchantId);*/
        String merchantKey=account.getFRAUD_FTP_PATH();/*utils.getPaymentFacilitatorKey(merchantId);*/
        String facilitatorCode =gatewayType.getPspCode();/*utils.getPaymentFacilitatorCode(merchantId);*/

        TDESBase64 objTDESB64 = new TDESBase64(merchantKey);
        Date date = new Date();
        String transDate=utils.getTransDate(date);
        String transTime=utils.getTransTime(date);

        String cardType=utils.getProcesosMCSpecificCardTypeName(genericCardDetailsVO.getCardType());
        String isPSTProcessingRequest = commRequestVO.getIsPSTProcessingRequest();
        transactionLogger.error("isPSTProcessingRequest====" + isPSTProcessingRequest);
        if ("Y".equals(isPSTProcessingRequest))
        {
            facilitatorCode = "";
        }

        //
        String requestXML =
                "<Autorizacion>"+
                        "<Marca>"+cardType+"</Marca>" +
                        "<Comercio>"+merchantId+"</Comercio>" +
                        "<Referencia>"+trackingID+"</Referencia>" +
                        "<Monto>"+objTDESB64.Encripta(round(Double.valueOf(genericTransDetailsVO.getAmount()),2))+"</Monto>" +
                        "<Moneda>"+genericTransDetailsVO.getCurrency()+"</Moneda>" +
                        "<Tarjeta>"+objTDESB64.Encripta(genericCardDetailsVO.getCardNum())+"</Tarjeta>" +
                        "<CVC>" + objTDESB64.Encripta(genericCardDetailsVO.getcVV()) + "</CVC>" +
                        "<FechaExp>"+objTDESB64.Encripta(genericCardDetailsVO.getExpMonth()+utils.getExpiryYearLast2Digit(genericCardDetailsVO.getExpYear()))+"</FechaExp>" +
                        "<Fecha>"+transDate+"</Fecha>" +
                        "<Hora>"+transTime+"</Hora>" +
                        "<Diferido>0</Diferido>" +
                        "<Cuotas>00</Cuotas>" +
                        "<TipoProceso>AT</TipoProceso>" +
                        "<Autogenerador>"+transDate+transTime+"</Autogenerador>" +
                        "<ComercioFacilitador>"+facilitatorCode+"</ComercioFacilitador>" +
                        "<DataEnc></DataEnc>" +
                        "<Firma></Firma>" +
                        "</Autorizacion>";



        //
        String logRequestData="<Autorizacion>"+
                "<Marca>"+cardType+"</Marca>"+
                "<Comercio>"+merchantId+"</Comercio>"+
                "<Referencia>"+trackingID+"</Referencia>"+
                "<Monto>"+objTDESB64.Encripta(round(Double.valueOf(genericTransDetailsVO.getAmount()),2))+"</Monto>"+
                "<Moneda>"+genericTransDetailsVO.getCurrency()+"</Moneda>"+
                "<Fecha>"+transDate+"</Fecha>"+
                "<Hora>"+transTime+"</Hora>"+
                "<Diferido>0</Diferido>"+
                "<Cuotas>00</Cuotas>" +
                "<TipoProceso>AT</TipoProceso>"+
                "<Autogenerador>"+transDate+transTime+"</Autogenerador>"+
                "<ComercioFacilitador>"+facilitatorCode+"</ComercioFacilitador>"+
                "<DataEnc></DataEnc>"+
                "<Firma></Firma>"+
                "</Autorizacion>";

        String responseXML = "";
        try
        {
            boolean isTestAccount=getAccountType(accountId);
            BasicHttpBinding_IsrvprocesoStub basicHttpBinding_isrvprocesoStub=null;
            SrvprocesoLocator locator=new SrvprocesoLocator();

            if(isTestAccount)
            {
               locator.setBasicHttpBinding_Isrvproceso_address(RB.getString("testauthurl"));
            }
            else
            {
                locator.setBasicHttpBinding_Isrvproceso_address(RB.getString("liveauthurl"));
            }

            //
            basicHttpBinding_isrvprocesoStub=(BasicHttpBinding_IsrvprocesoStub)locator.getBasicHttpBinding_Isrvproceso();


            //todo
            //Date date104=new Date();

            transactionLogger.error("----process authentication request XML----"+trackingID+"---" + logRequestData);
            responseXML=basicHttpBinding_isrvprocesoStub.autoriza(requestXML);
            transactionLogger.error("-----process authentication response XML----"+trackingID+"---" + responseXML);

            HashMap hashMap=utils.readResponseForAuth(responseXML);

            String status="fail";
            String successCode="";
            String description="";
            String remark="";

            String paymentId="";
            String authCode="";
            String refNumber="";
            String numberOfFees="0";
            String firstFeeDate="";
            String feeCurrency="";
            double feeAmount=0.00;
            String resultCode="";
            String resultDescription="";
            String txAcqId="";
            String cardCountryCode="";
            String bankTransDate="";
            String bankTransTime="";

            Functions functions=new Functions();

            TransactionDetailsVO vCurrentTransaction=null;
            String descriptor= GatewayAccountService.getGatewayAccount(accountId).getDisplayName();

            if(hashMap !=null)
            {
                TransactionDAO tDAO = new TransactionDAO();
                vCurrentTransaction = tDAO.getDetailFromCommon(trackingID);

                bankTransDate=(String)hashMap.get("Fecha");
                bankTransTime=(String)hashMap.get("Hora");

                if("A".equals(hashMap.get("Resultado")))
                {
                    status="success";
                    description="Transaction  successful";
                    remark="Transaction Successful";
                }
                else
                {
                    status="fail";
                    description="Transaction  Failed";
                    remark="Transaction Failed";
                }
                if(hashMap.get("CodRespuesta")!=null)
                {
                    successCode=(String)hashMap.get("CodRespuesta");
                    resultCode=(String)hashMap.get("CodRespuesta");
                }
                if(hashMap.get("Mensaje")!=null)
                {
                    resultDescription=(String)hashMap.get("Mensaje");
                }
                if(hashMap.get("CodAutoriza")!=null)
                {
                    String URLdecCodAutorizacion=objTDESB64.DecodingBase64((String)hashMap.get("CodAutoriza"));
                    authCode=objTDESB64.Desencripta(URLdecCodAutorizacion);
                }
                if(hashMap.get("ReferenciaPMP")!=null)
                {
                    paymentId=(String)hashMap.get("ReferenciaPMP");
                }
                if(hashMap.get("Cuotas")!=null)
                {
                    numberOfFees=(String)hashMap.get("Cuotas");
                }
                if(hashMap.get("FecPrimeraCuota")!=null)
                {
                    firstFeeDate=(String)hashMap.get("FecPrimeraCuota");
                }
                if(hashMap.get("MonedaCuota")!=null)
                {
                    feeCurrency=(String)hashMap.get("MonedaCuota");
                }
                if(functions.isValueNull((String) hashMap.get("MontoCuota")))
                {
                    feeAmount = Double.valueOf((String) hashMap.get("MontoCuota"));
                }
                if(hashMap.get("IdTxn")!=null)
                {
                    String URLencIdOperacion=objTDESB64.DecodingBase64((String) hashMap.get("IdTxn"));
                    refNumber=objTDESB64.Desencripta(URLencIdOperacion);
                    txAcqId=refNumber;
                }
                if(functions.isValueNull((String)hashMap.get("CodPais")))
                {
                   cardCountryCode=(String)hashMap.get("CodPais");
                }
            }
            procesosMCResponseVO.setStatus(status);
            procesosMCResponseVO.setDescription(description);
            procesosMCResponseVO.setTransactionType("auth");
            procesosMCResponseVO.setDescriptor(descriptor);
            procesosMCResponseVO.setTransactionStatus(vCurrentTransaction.getStatus());
            procesosMCResponseVO.setErrorCode(successCode);
            procesosMCResponseVO.setTransactionId(paymentId);
            procesosMCResponseVO.setBankTransDate(bankTransDate);
            procesosMCResponseVO.setBankTransTime(bankTransTime);
            procesosMCResponseVO.setAuthCode(authCode);
            procesosMCResponseVO.setRefNumber(refNumber);
            procesosMCResponseVO.setNumberOfFees(numberOfFees);
            procesosMCResponseVO.setFirstFeeDate(firstFeeDate);
            procesosMCResponseVO.setFeeCurrency(feeCurrency);
            procesosMCResponseVO.setFeeAmount(feeAmount);
            procesosMCResponseVO.setResultCode(resultCode);
            procesosMCResponseVO.setResultDescription(resultDescription);
            procesosMCResponseVO.setTxAcqId(txAcqId);
            procesosMCResponseVO.setCardCountryCode(cardCountryCode);
            procesosMCResponseVO.setRemark(remark);

            DateFormat dateFormat1 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date1 = new Date();
            procesosMCResponseVO.setResponseTime(String.valueOf(dateFormat1.format(date1)));

            //


        }
        catch (ServiceException se)
        {
            transactionLogger.error("Exception while connecting with bank", se);
            PZExceptionHandler.raiseTechnicalViolationException("ProcesosMCPaymentGateway.java", "processAuthentication()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.SERVICE_EXCEPTION, null, se.getMessage(), se.getCause());
        }
        catch (RemoteException re)
        {
            transactionLogger.error("Exception while connecting with bank", re);
            PZExceptionHandler.raiseTechnicalViolationException("ProcesosMCPaymentGateway.java", "processAuthentication()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.REMOTE_EXCEPTION, null, re.getMessage(), re.getCause());
        }
        catch (UnsupportedEncodingException e)
        {
            transactionLogger.error("Exception while encoding bank parameters", e);
            PZExceptionHandler.raiseTechnicalViolationException("ProcesosMCPaymentGateway.java", "processAuthentication()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.UNSUPPORTED_ENCOADING_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        return procesosMCResponseVO;
    }

    public GenericResponseVO processCapture(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionLogger.debug("Entering processCapture of ProcesosMCPaymentGateway...");
        ProcesosMCRequestVO procesosMCRequestVO=(ProcesosMCRequestVO)requestVO;
        ProcesosMCResponseVO procesosMCResponseVO=new ProcesosMCResponseVO();
        CommMerchantVO commMerchantVO = procesosMCRequestVO.getCommMerchantVO();
        GenericTransDetailsVO genericTransDetailsVO=procesosMCRequestVO.getTransDetailsVO();

        GatewayAccount account=GatewayAccountService.getGatewayAccount(commMerchantVO.getAccountId());
        GatewayType gatewayType = GatewayTypeService.getGatewayType(account.getPgTypeId());

        try
        {
            com.payment.procesosmc.org.tempuri.BasicHttpBinding_IsrvprocesoStub basicHttpBinding_isrvprocesoStub=null;
            com.payment.procesosmc.org.tempuri.depositcancel.SrvprocesoLocator locator=new com.payment.procesosmc.org.tempuri.depositcancel.SrvprocesoLocator();
            boolean isTestAccount=getAccountType(commMerchantVO.getAccountId());
            if(isTestAccount)
            {
                locator.setBasicHttpBinding_Isrvproceso_address(RB.getString("testdepositvoidinquiryurl"));
            }
            else
            {
                locator.setBasicHttpBinding_Isrvproceso_address(RB.getString("livedepositvoidinquiryurl"));
            }

            basicHttpBinding_isrvprocesoStub=(com.payment.procesosmc.org.tempuri.BasicHttpBinding_IsrvprocesoStub)locator.getBasicHttpBinding_Isrvproceso();

            String requestXML="<Deposito>" +
                    "<Comercio>"+account.getMerchantId()+"</Comercio>" +
                    "<IdTxn>"+procesosMCRequestVO.getRefNumber()+"</IdTxn>" +
                    "<CodAutorizacion>"+procesosMCRequestVO.getAuthCode()+"</CodAutorizacion>" +
                    "<NumPedido>"+trackingID+"</NumPedido>" +
                    "<Moneda>"+genericTransDetailsVO.getCurrency()+"</Moneda>" +
                    "<Monto>"+genericTransDetailsVO.getAmount()+"</Monto>" +
                    "<FechaTxn>"+procesosMCRequestVO.getBankTransDate()+"</FechaTxn>" +
                    "<HoraTxn>"+procesosMCRequestVO.getBankTransTime()+"</HoraTxn>" +
                    "</Deposito>";

            String responseXML="";
            transactionLogger.error("------process capture request XML--------" + requestXML);
            responseXML=basicHttpBinding_isrvprocesoStub.deposito(requestXML);
            transactionLogger.error("------process capture response XML-------" + responseXML);
            HashMap hashMap=utils.readResponseForDeposit(responseXML);


            String status="fail";
            String bankStatus="";
            String lote="";

            String description="";
            String resultDescription;
            String remark="";


            String errorCode="";
            String resultCode="";

            if(hashMap!=null)
            {
                if("00".equals(hashMap.get("CodRespuesta")))
                {
                    status = "success";
                    description="Transaction  successful";
                }
                else
                {
                    status="fail";
                    description="Your order failed to capture";
                }
                errorCode=(String)hashMap.get("CodRespuesta");
                resultCode=(String)hashMap.get("CodRespuesta");
                resultDescription=(String)hashMap.get("MensajeResp");
                lote=(String)hashMap.get("NroLote");
                remark=(String)hashMap.get("MensajeResp");
            }
            else
            {
                transactionLogger.debug("inside hash is empty");
                status="fail";
                description="Your order failed to capture";
                resultDescription="Bank connectivity Issue.";
                remark="Bank connectivity Issue.";
            }

            procesosMCResponseVO.setStatus(status);
            procesosMCResponseVO.setDescription(description);
            procesosMCResponseVO.setErrorCode(errorCode);
            procesosMCResponseVO.setResultCode(resultCode);
            procesosMCResponseVO.setResultDescription(resultDescription);
            procesosMCResponseVO.setRemark(remark);
            procesosMCResponseVO.setLote(lote);
            procesosMCResponseVO.setTransactionType("Capture");

            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            procesosMCResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));

        }
        catch (ServiceException se)
        {
            PZExceptionHandler.raiseTechnicalViolationException(ProcesosMCPaymentGateway.class.getName(),"processCapture()",null,"common","Technical Exception:::::", PZTechnicalExceptionEnum.SERVICE_EXCEPTION,null,se.getMessage(),se.getCause());
        }
        catch (RemoteException re)
        {
            PZExceptionHandler.raiseTechnicalViolationException(ProcesosMCPaymentGateway.class.getName(), "processCapture()", null, "common", "Technical Exception:::::", PZTechnicalExceptionEnum.REMOTE_EXCEPTION, null, re.getMessage(), re.getCause());
        }
        return procesosMCResponseVO;
    }

    public GenericResponseVO processVoid(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionLogger.debug("Entering processVoid of ProcesosMCPaymentGateway...");
        ProcesosMCRequestVO procesosMCRequestVO=(ProcesosMCRequestVO)requestVO;
        ProcesosMCResponseVO procesosMCResponseVO=new ProcesosMCResponseVO();
        CommMerchantVO commMerchantVO = procesosMCRequestVO.getCommMerchantVO();
        GenericTransDetailsVO genericTransDetailsVO=procesosMCRequestVO.getTransDetailsVO();

        GatewayAccount account=GatewayAccountService.getGatewayAccount(commMerchantVO.getAccountId());
        GatewayType gatewayType = GatewayTypeService.getGatewayType(account.getPgTypeId());
        try
        {
            com.payment.procesosmc.org.tempuri.BasicHttpBinding_IsrvprocesoStub basicHttpBinding_isrvprocesoStub=null;
            com.payment.procesosmc.org.tempuri.depositcancel.SrvprocesoLocator locator=new com.payment.procesosmc.org.tempuri.depositcancel.SrvprocesoLocator();
            boolean isTestAccount=getAccountType(commMerchantVO.getAccountId());
            if(isTestAccount)
            {
                locator.setBasicHttpBinding_Isrvproceso_address(RB.getString("testdepositvoidinquiryurl"));
            }
            else
            {
                locator.setBasicHttpBinding_Isrvproceso_address(RB.getString("livedepositvoidinquiryurl"));
            }
            basicHttpBinding_isrvprocesoStub=(com.payment.procesosmc.org.tempuri.BasicHttpBinding_IsrvprocesoStub)locator.getBasicHttpBinding_Isrvproceso();
            //basicHttpBinding_isrvprocesoStub=(com.payment.procesosmc.org.tempuri.BasicHttpBinding_IsrvprocesoStub)new com.payment.procesosmc.org.tempuri.depositcancel.SrvprocesoLocator().getBasicHttpBinding_Isrvproceso();
            String requestXML="<Anulacion>" +
                    "<Comercio>"+account.getMerchantId()+"</Comercio>" +
                    "<IdTxn>"+procesosMCRequestVO.getRefNumber()+"</IdTxn>" +
                    "<CodAutorizacion>"+procesosMCRequestVO.getAuthCode()+"</CodAutorizacion>" +
                    "<NumPedido>"+trackingID+"</NumPedido>" +
                    "<Moneda>"+genericTransDetailsVO.getCurrency()+"</Moneda>" +
                    "<Monto>"+genericTransDetailsVO.getAmount()+"</Monto>" +
                    "<FechaTxn>"+procesosMCRequestVO.getBankTransDate()+"</FechaTxn>" +
                    "<HoraTxn>"+procesosMCRequestVO.getBankTransTime()+"</HoraTxn>" +
                    "</Anulacion>";

            String responseXML = "";

            transactionLogger.error("----process void request XML---" + requestXML);
            responseXML =basicHttpBinding_isrvprocesoStub.anulacion(requestXML);
            transactionLogger.error("----process void response XML----" + responseXML);

            HashMap hashMap=utils.readResponseForCancel(responseXML);

            String status="fail";
            String description="";
            String resultDescription="";
            String remark="";

            String errorCode=null;
            String resultCode=null;
            if(hashMap!=null)
            {
                if("00".equals(hashMap.get("CodRespuesta")))
                {
                    status = "success";
                    description="Transaction  successful";
                }
                else
                {
                    status="fail";
                    description="Your order failed to cancel";
                }
                errorCode=(String)hashMap.get("CodRespuesta");
                resultCode=(String)hashMap.get("CodRespuesta");
                resultDescription=(String)hashMap.get("MensajeResp");
                remark=(String)hashMap.get("MensajeResp");
            }
            else
            {
                transactionLogger.debug("inside hash is empty");
                status="fail";
                description="Your order failed to capture";
                resultDescription="Bank connectivity issue";
                remark="Bank connectivity issue";
            }

            procesosMCResponseVO.setStatus(status);
            procesosMCResponseVO.setResultCode(resultCode);
            procesosMCResponseVO.setErrorCode(errorCode);
            procesosMCResponseVO.setResultDescription(resultDescription);
            procesosMCResponseVO.setRemark(remark);
            procesosMCResponseVO.setDescription(description);
            procesosMCResponseVO.setTransactionType("Cancel");

            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            procesosMCResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));

        }
        catch (ServiceException se)
        {
            PZExceptionHandler.raiseTechnicalViolationException(ProcesosMCPaymentGateway.class.getName(), "processVoid()", null, "common", "Technical Exception:::::", PZTechnicalExceptionEnum.SERVICE_EXCEPTION, null, se.getMessage(), se.getCause());
        }
        catch (RemoteException re)
        {
            PZExceptionHandler.raiseTechnicalViolationException(ProcesosMCPaymentGateway.class.getName(), "processVoid()", null, "common", "Technical Exception:::::", PZTechnicalExceptionEnum.REMOTE_EXCEPTION, null, re.getMessage(), re.getCause());
        }
        return procesosMCResponseVO;
    }

    public GenericResponseVO processInquiry(GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionLogger.debug("Entering processInquiry of ProcesosMCPaymentGateway...");
        CommRequestVO commRequestVO  =    (CommRequestVO)requestVO;
        ProcesosMCResponseVO commResponseVO=new ProcesosMCResponseVO();
        CommMerchantVO commMerchantVO = commRequestVO.getCommMerchantVO();
        GatewayAccount account=GatewayAccountService.getGatewayAccount(commMerchantVO.getAccountId());
        GatewayType gatewayType = GatewayTypeService.getGatewayType(account.getPgTypeId());
        try
        {
            com.payment.procesosmc.org.tempuri.BasicHttpBinding_IsrvprocesoStub basicHttpBinding_isrvprocesoStub=null;
            com.payment.procesosmc.org.tempuri.depositcancel.SrvprocesoLocator locator=new com.payment.procesosmc.org.tempuri.depositcancel.SrvprocesoLocator();
            boolean isTestAccount=getAccountType(commMerchantVO.getAccountId());
            //basicHttpBinding_isrvprocesoStub=(com.payment.procesosmc.org.tempuri.BasicHttpBinding_IsrvprocesoStub)new com.payment.procesosmc.org.tempuri.depositcancel.SrvprocesoLocator().getBasicHttpBinding_Isrvproceso();
            if(isTestAccount)
            {
                locator.setBasicHttpBinding_Isrvproceso_address(RB.getString("testdepositvoidinquiryurl"));
            }
            else
            {
                locator.setBasicHttpBinding_Isrvproceso_address(RB.getString("livedepositvoidinquiryurl"));
            }
            basicHttpBinding_isrvprocesoStub=(com.payment.procesosmc.org.tempuri.BasicHttpBinding_IsrvprocesoStub)locator.getBasicHttpBinding_Isrvproceso();
            String requestXML="<Consulta>" +
                    "<Comercio>"+gatewayType.getPspCode()+"</Comercio>" +
                    "<NumPedido>"+commRequestVO.getTransDetailsVO().getOrderId()+"</NumPedido>" +
                    "</Consulta>";

            String responseXML="";

            transactionLogger.error("----process inquiry request XML---" + requestXML);
            responseXML =basicHttpBinding_isrvprocesoStub.consulta(requestXML);
            transactionLogger.error("----process inquiry response XML---" + responseXML);
            HashMap hashMap=utils.readResponseForInquiry(responseXML);
            //transactionLogger.debug("hashMap======="+hashMap);

            String authCode="";
            String refNumber="";
            String bankTransDate="";
            String status="";

            if("00".equals(hashMap.get("CodRespuesta")))
            {
                if("AT".equals(hashMap.get("Estado")) || "DP".equals(hashMap.get("Estado")))
                {
                    status="success";
                }
                else if("RT".equals(hashMap.get("Estado")) || "".equals(hashMap.get("Estado")))
                {
                    status="failed";
                }
            }
            else if("MC".equals(hashMap.get("MC")))
            {
                status="fail";
            }

            authCode=(String)hashMap.get("CodAutorizacion");
            refNumber=(String)hashMap.get("IdTxn");
            bankTransDate=(String)hashMap.get("FechaTxn");


            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();

            commResponseVO.setAuthCode(authCode);
            commResponseVO.setBankTransDate(bankTransDate);
            commResponseVO.setDescription((String) hashMap.get("MensajeEstado"));
            commResponseVO.setStatus(status);
            commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
            commResponseVO.setTransactionId(refNumber);
            commResponseVO.setTransactionStatus("success");
            commResponseVO.setDescriptor((String) hashMap.get("MensajeResp"));

        }
        catch (ServiceException se)
        {
            PZExceptionHandler.raiseTechnicalViolationException(ProcesosMCPaymentGateway.class.getName(), "processInquiry()", null, "common", "Technical Exception:::::", PZTechnicalExceptionEnum.SERVICE_EXCEPTION, null, se.getMessage(), se.getCause());
        }
        catch (RemoteException re)
        {
            PZExceptionHandler.raiseTechnicalViolationException(ProcesosMCPaymentGateway.class.getName(), "processInquiry()", null, "common", "Technical Exception:::::", PZTechnicalExceptionEnum.REMOTE_EXCEPTION, null, re.getMessage(), re.getCause());
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

    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO)throws PZGenericConstraintViolationException
    {
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        errorCodeListVO.addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_FUNCTIONALITY_NOT_ALLOWED));
        PZGenericConstraint genConstraint = new PZGenericConstraint("ProcesosMCPaymentGateway","processSale",null,"common","This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",errorCodeListVO);
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
