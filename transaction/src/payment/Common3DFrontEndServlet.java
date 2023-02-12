package payment;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.directi.pg.core.valueObjects.GenericCardDetailsVO;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.manager.PaymentManager;
import com.manager.TransactionManager;
import com.manager.dao.MerchantDAO;
import com.manager.vo.MarketPlaceVO;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.Enum.PZProcessType;
import com.payment.Enum.PaymentModeEnum;
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.PZTransactionStatus;
import com.payment.Triple000.Triple000Utils;
import com.payment.awepay.AwepayBundle.core.AwePayUtils;
import com.payment.common.core.*;
import com.payment.exceptionHandler.*;
import com.payment.exceptionHandler.constraintType.PZConstraintExceptionEnum;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.sabadell.SabadellUtils;
import com.payment.sms.AsynchronousSmsService;
import com.payment.statussync.StatusSyncDAO;
import com.payment.validators.vo.CommonValidatorVO;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.reference.DefaultEncoder;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLDecoder;
import java.sql.Connection;
import java.util.*;

import static com.payment.awepay.AwepayBundle.core.AwePayUtils.decrypt;

/**
 * Created by Uday on 9/7/18.
 */
public class Common3DFrontEndServlet extends HttpServlet
{
    TransactionLogger transactionLogger= new TransactionLogger(Common3DFrontEndServlet.class.getName());
    String ctoken = ESAPI.randomizer().getRandomString(32, DefaultEncoder.CHAR_ALPHANUMERICS);
    public void doGet(HttpServletRequest request,HttpServletResponse response) throws IOException ,ServletException
    {
        doService(request,response);
    }

    public void doPost(HttpServletRequest request,HttpServletResponse response) throws  IOException,ServletException
    {
        doService(request,response);
    }

    public void doService(HttpServletRequest request,HttpServletResponse response) throws IOException,ServletException
    {
        transactionLogger.error("-----Inside Common3DFrontEndServlet-----");


        Enumeration enumeration=request.getParameterNames();
        while (enumeration.hasMoreElements())
        {
            String key = (String) enumeration.nextElement();
            String value = request.getParameter(key);

            transactionLogger.error("Name=" + key + "-----Value=" + value);
        }
        HttpSession session                         = request.getSession(true);
        CommonValidatorVO commonValidatorVO         =new CommonValidatorVO();
        GenericAddressDetailsVO addressDetailsVO    =new GenericAddressDetailsVO();
        GenericCardDetailsVO cardDetailsVO          = new GenericCardDetailsVO();
        GenericTransDetailsVO transDetailsVO        = new GenericTransDetailsVO();
        TransactionUtility transactionUtility       = new TransactionUtility();
        TransactionManager transactionManager       = new TransactionManager();
        AuditTrailVO auditTrailVO   = new AuditTrailVO();
        AbstractPaymentGateway pg   = null;
        Functions functions                     = new Functions();
        List<MarketPlaceVO> marketPlaceVOList   = new ArrayList<>();
        MarketPlaceVO marketPlaceVO             = new MarketPlaceVO();
        Connection con                          = null;
        Comm3DResponseVO transRespDetails       = null;
        String PaymentType  = "";
        String trackingId   = null;
        String toId         =null;
        String isService    =null;
        String accountId    =null;
        String status       =null;
        String amount       =null;
        String description  = null;
        String redirectUrl  = null;
        String autoRedirect = null;
        String logoName     = null;
        String partnerName  = null;
        String orderDesc    = null;
        String currency     = null;
        String transactionId        = null;
        String authorization_code   = "";
        String transactionStatus    = null;
        String message          = null;
        String errorName        = null;
        String billingDesc      = null;
        String rrn          = "";
        String transType    = null;
        String dbStatus     = null;
        String tmpl_amt     = null;
        String tmpl_currency = null;
        String paymodeid        = null;
        String cardtypeid       = null;
        String email            = null;
        String eci              = null;
        String erroCode         = null;
        String responseStatus   = null;
        String notificationUrl  = null;
        String date         ="";
        String customerId   = null;
        String firstName    = null;
        String lastName     = null;
        String version      = null;
        String ccnum        = null;
        String expDate      = null;
        String expMonth     = null;
        String expYear      = null;
        String cvv          = null;
        String terminalid   = null;
        String paymentid    = null;
        String confirmStatus= null;
        String fromType     = null;
        String paRes        = null;
        String md           = null;
        String updatedStatus= null;
        String street   = "";
        String zip      = "";
        String state    = "";
        String city     = "";
        String country  = "";
        String telno    = "";
        String telcc    = "";
        String enData   = "";
        String OPcode   = "";
        String gatewayRecommendation = "";
        try
        {
            if (functions.isValueNull(request.getParameter("trackingId")))
            {
                trackingId = request.getParameter("trackingId");

                if(trackingId.contains("?")){
                    String value[]  = trackingId.split("\\?");
                    trackingId      = value[0];
                }

                if(trackingId.contains("_")){
                    String value[]  = trackingId.split("_");
                    trackingId      = value[0];
                    OPcode          = value[1];
                }

                String remoteAddr   = Functions.getIpAddress(request);
                String reqIp        = "";
                if(remoteAddr.contains(","))
                {
                    String sIp[]    = remoteAddr.split(",");
                    reqIp           = sIp[0].trim();
                }
                else
                {
                    reqIp           = remoteAddr;
                }
                enData = request.getParameter("data");
                transactionLogger.debug("reqIp-----"+reqIp);
                String customerIpCountry=functions.getIPCountryShort(reqIp);
                if(!functions.isValueNull(customerIpCountry))
                    customerIpCountry="";
                TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingId);
                if (transactionDetailsVO != null)
                {
                    Comm3DRequestVO comm3DRequestVO = new Comm3DRequestVO();
                    fromType        = transactionDetailsVO.getFromtype();
                    toId            = transactionDetailsVO.getToid();
                    accountId       = transactionDetailsVO.getAccountId();
                    amount          = transactionDetailsVO.getAmount();
                    tmpl_amt        = transactionDetailsVO.getTemplateamount();
                    tmpl_currency   = transactionDetailsVO.getTemplatecurrency();
                    description     = transactionDetailsVO.getDescription();
                    redirectUrl     = transactionDetailsVO.getRedirectURL();
                    orderDesc       = transactionDetailsVO.getOrderDescription();
                    currency        = transactionDetailsVO.getCurrency();
                    paymodeid       = transactionDetailsVO.getPaymodeId();
                    cardtypeid      = transactionDetailsVO.getCardTypeId();
                    email           = transactionDetailsVO.getEmailaddr();
                    dbStatus        = transactionDetailsVO.getStatus();
                    firstName       = transactionDetailsVO.getFirstName();
                    lastName        = transactionDetailsVO.getLastName();
                    version         = transactionDetailsVO.getVersion();
                    terminalid      = transactionDetailsVO.getTerminalId();
                    customerId      =transactionDetailsVO.getCustomerId();
                    paymentid       =transactionDetailsVO.getPaymentId();
                    zip             = transactionDetailsVO.getZip();
                    street          = transactionDetailsVO.getStreet();
                    state           = transactionDetailsVO.getState();
                    city            = transactionDetailsVO.getCity();
                    country         = transactionDetailsVO.getCountry();
                    telcc           = transactionDetailsVO.getTelcc();
                    telno           = transactionDetailsVO.getTelno();

                    transactionLogger.debug("version-----"+version);
                    transactionLogger.debug("notificationUrl-----"+notificationUrl);
                    transactionLogger.error("fromType-----"+fromType);
                    GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
                    if("sabadell".equalsIgnoreCase(fromType)){
                        SabadellUtils sabadellUtils=new SabadellUtils();
                        transRespDetails=new Comm3DResponseVO();
                        String Ds_SignatureVersion="";
                        String Ds_MerchantParameters="";
                        String Ds_Signature="";
                        if(functions.isValueNull(request.getParameter("Ds_SignatureVersion")))
                            Ds_SignatureVersion=request.getParameter("Ds_SignatureVersion");
                        if(functions.isValueNull(request.getParameter("Ds_MerchantParameters")))
                            Ds_MerchantParameters=request.getParameter("Ds_MerchantParameters");
                        if(functions.isValueNull(request.getParameter("Ds_Signature")))
                            Ds_Signature=request.getParameter("Ds_Signature");

                        String Ds_Response = "";
                        String Ds_TransactionType = "";
                        String Ds_AuthorisationCode = "";
                        String Ds_Terminal = "";
                        String Ds_Date = "";
                        String Ds_MerchantData = "";
                        status = "";
                        String remark = "";
                        String decodedParams = sabadellUtils.decodeMerchantParameters(Ds_MerchantParameters);
                        String calculatedSign = sabadellUtils.createMerchantSignatureNotif(gatewayAccount.getFRAUD_FTP_USERNAME(), Ds_MerchantParameters);
                        if (calculatedSign.equalsIgnoreCase(Ds_Signature))
                        {
                            JSONObject jsonObject = new JSONObject(decodedParams);
                            if (jsonObject != null)
                            {
                                if (jsonObject.has("Ds_Response"))
                                {
                                    Ds_Response = jsonObject.getString("Ds_Response");
                                    if ("0000".equals(Ds_Response) || "0099".equalsIgnoreCase(Ds_Response))
                                    {
                                        status = "success";
                                    }
                                    else
                                    {
                                        status = "fails";
                                    }
                                }
                                if (jsonObject.has("Ds_TransactionType"))
                                {
                                    Ds_TransactionType = jsonObject.getString("Ds_TransactionType");
                                }
                                if (jsonObject.has("Ds_AuthorisationCode"))
                                {
                                    Ds_AuthorisationCode = jsonObject.getString("Ds_AuthorisationCode");
                                }
                                if (jsonObject.has("Ds_Terminal"))
                                {
                                    Ds_Terminal = jsonObject.getString("Ds_Terminal");
                                }
                                if (jsonObject.has("Ds_Order"))
                                {
                                    trackingId = jsonObject.getString("Ds_Order");
                                }
                                if (jsonObject.has("Ds_Date"))
                                {
                                    Ds_Date = jsonObject.getString("Ds_Date");
                                }
                                if (jsonObject.has("Ds_MerchantData"))
                                {
                                    Ds_MerchantData = jsonObject.getString("Ds_MerchantData");
                                }

                                if ("success".equalsIgnoreCase(status))
                                {
                                    billingDesc=gatewayAccount.getDisplayName();
                                    transRespDetails.setStatus("success");
                                    transRespDetails.setDescriptor(billingDesc);
                                    if (functions.isValueNull(Ds_MerchantData))
                                    {
                                        remark = Ds_MerchantData;
                                    }
                                    else
                                    {
                                        remark = "Transaction Successful";
                                    }
                                }
                                else
                                {
                                    remark = "Transaction Failed";
                                    transRespDetails.setStatus(status);
                                }
                                transRespDetails.setTransactionId(transactionId);
                                transRespDetails.setAuthCode(Ds_AuthorisationCode);
                                transRespDetails.setRemark(remark);
                                transRespDetails.setDescription(status);
                                transRespDetails.setBankTransactionDate(URLDecoder.decode(Ds_Date));
                            }
                        }
                        transRespDetails.setStatus("pending");
                        transRespDetails.setRemark("FE:Transaction is pending");
                    }
                    else if("zotapay".equalsIgnoreCase(fromType))
                    {
                        transRespDetails=new Comm3DResponseVO();
                        if (functions.isValueNull(request.getParameter("status")))
                            status=request.getParameter("status");
                        if(functions.isValueNull(request.getParameter("client_orderid")))
                            trackingId=request.getParameter("client_orderid");
                        if(functions.isValueNull(request.getParameter("orderid")))
                            transactionId=request.getParameter("orderid");

                        if(functions.isValueNull(request.getParameter("approval-code")))
                            erroCode=request.getParameter("approval-code");

                        if("approved".equalsIgnoreCase(status))
                        {
                            transRespDetails.setStatus("success");
                        }else {
                            transRespDetails.setStatus("fail");
                        }
                        transRespDetails.setTransactionId(transactionId);
                        transRespDetails.setRemark(status);
                        transRespDetails.setDescription(status);
                        transRespDetails.setErrorName(erroCode);
                    }else if("rsp".equalsIgnoreCase(fromType)){
                        transRespDetails=new Comm3DResponseVO();
                        String resReferenceId =null;
                        String resCode = null;
                        String resMessage = null;
                        String resDescription =null;
                        String resAmount =null;
                        String resAuthCode=null;
                        String resRemarks =null;
                        String resDatetime=null;

                        if(functions.isValueNull(request.getParameter("res_referenceid")));
                        resReferenceId=request.getParameter("res_referenceid");
                        if(functions.isValueNull(request.getParameter("res_code")))
                            resCode=request.getParameter("res_code");
                        if(functions.isValueNull(request.getParameter("res_message")))
                            resMessage=request.getParameter("res_message");
                        if(functions.isValueNull(request.getParameter("res_description")))
                            resDescription = request.getParameter("res_description");
                        if(functions.isValueNull(request.getParameter("res_amount")))
                            resAmount = request.getParameter("res_amount");
                        if(functions.isValueNull(request.getParameter("res_authcode")))
                            resAuthCode = request.getParameter("res_authcode");
                        if(functions.isValueNull(request.getParameter("res_remarks")))
                            resRemarks = request.getParameter("res_remarks");
                        if(functions.isValueNull(request.getParameter("res_datetime")))
                            resDatetime = request.getParameter("res_datetime");

                        if ("0".equals(resCode) && "Approved".equals(resMessage))
                        {
                            status = "success";
                            billingDesc = gatewayAccount.getDisplayName();
                            transRespDetails.setDescriptor(billingDesc);
                        }else {
                            status="fail";
                        }
                        transRespDetails.setStatus(status);
                        transRespDetails.setTransactionId(resReferenceId);
                        transRespDetails.setRemark(resRemarks);
                        transRespDetails.setAuthCode(resAuthCode);
                        transRespDetails.setBankTransactionDate(resDatetime);
                        transRespDetails.setAmount(resAmount);
                        transRespDetails.setDescription(resDescription);
                    }
                    else if("triple000".equalsIgnoreCase(fromType)){
                        String tran_id="";
                        String error_code="";
                        String type="";
                        String operator_id="";
                        String ext_id="";
                        String trxn_amount="";
                        String authtype="";
                        String resp_time="";

                        transRespDetails=new Comm3DResponseVO();
                       String responsestr=request.getParameter("response");
                        Triple000Utils triple000Utils = new Triple000Utils();
                        HashMap<String,String> responseMap= triple000Utils.readTriple000XMLResponse(responsestr,trackingId,"");

                        if(functions.isValueNull(responseMap.get("tran_id")));
                        tran_id=responseMap.get("tran_id");
                        if(functions.isValueNull(responseMap.get("error_code")))
                            error_code=responseMap.get("error_code");
                        if(functions.isValueNull(responseMap.get("message")))
                            message=responseMap.get("message");
                        if(functions.isValueNull(responseMap.get("type")))
                            type = responseMap.get("type");
                        if(functions.isValueNull(responseMap.get("operator_id")))
                            operator_id = responseMap.get("operator_id");
                        if(functions.isValueNull(responseMap.get("ext_id")))
                            ext_id = responseMap.get("ext_id");
                        if(functions.isValueNull(responseMap.get("currency")))
                            currency = responseMap.get("currency");
                        if(functions.isValueNull(responseMap.get("trxn_amount")))
                            trxn_amount = responseMap.get("trxn_amount");
                        if(functions.isValueNull(responseMap.get("authtype")))
                            authtype = responseMap.get("authtype");
                        if(functions.isValueNull(responseMap.get("resp_time")))
                            resp_time = responseMap.get("resp_time");
                        transactionLogger.error("responsestr-->"+responsestr);
                        transactionLogger.error("error_code in frontend-->"+error_code);

                        if ("0".equals(error_code))
                        {
                            status = "success";
                            billingDesc = gatewayAccount.getDisplayName();
                            transRespDetails.setDescriptor(billingDesc);
                        }else {
                            status="failed";
                        }
                        transRespDetails.setStatus(status);
                        transRespDetails.setTransactionId(tran_id);
                        transRespDetails.setRemark(message);
                        transRespDetails.setBankTransactionDate(resp_time);
                        transRespDetails.setAmount(amount);
                        transRespDetails.setDescription(message);
                        transRespDetails.setCurrency(currency);
                        transRespDetails.setAuthCode(authtype);
                    }

                    else if("agnipay".equalsIgnoreCase(fromType) || "trnsactWLD".equalsIgnoreCase(fromType) || "shimotomo".equalsIgnoreCase(fromType)){
                        transRespDetails= new Comm3DResponseVO();
                        if(functions.isValueNull(request.getParameter("paymentId"))){
                            transactionId=request.getParameter("paymentId");
                        }
                        if(functions.isValueNull(request.getParameter("status"))){
                            status=request.getParameter("status");
                        }
                        if(functions.isValueNull(request.getParameter("currency"))){
                            currency=request.getParameter("currency");
                        }
                        if(functions.isValueNull(request.getParameter("amount"))){
                            amount=request.getParameter("amount");
                        }
                        if(functions.isValueNull(request.getParameter("tmpl_currency"))){
                            tmpl_currency=request.getParameter("tmpl_currency");
                        }
                        if(functions.isValueNull(request.getParameter("tmpl_amount"))){
                            tmpl_amt=request.getParameter("tmpl_amount");
                        }
                        if(functions.isValueNull(request.getParameter("timestamp"))){
                            date=request.getParameter("timestamp");
                        }
                        if(functions.isValueNull(request.getParameter("resultCode"))){
                            erroCode=request.getParameter("resultCode");
                        }
                        if(functions.isValueNull(request.getParameter("resultDescription"))){
                            message=request.getParameter("resultDescription");
                        }

                        if("Y".equalsIgnoreCase(status)){
                            transRespDetails.setStatus("success");
                            transRespDetails.setDescriptor(gatewayAccount.getDisplayName());
                        }else {
                            transRespDetails.setStatus("fail");
                        }
                        transRespDetails.setAmount(amount);
                        transRespDetails.setCurrency(currency);
                        transRespDetails.setTmpl_Amount(tmpl_amt);
                        transRespDetails.setTmpl_Currency(tmpl_currency);
                        transRespDetails.setBankTransactionDate(date);
                        transRespDetails.setErrorCode(erroCode);
                        transRespDetails.setDescription(message);
                        transRespDetails.setRemark(message);
                        transRespDetails.setTransactionId(transactionId);
                    }else if("SBMGateway".equalsIgnoreCase(fromType)){
                        transRespDetails= new Comm3DResponseVO();
                        String ActionCodeDescription="";
                        String approvalCode="";
                        String actionCode="";
                        String orderStatus="";
                        String postDate="";
                        String orderid="";
                        eci="";

                        if(functions.isValueNull(request.getParameter("ActionCodeDescription"))){
                            ActionCodeDescription=request.getParameter("ActionCodeDescription");
                        }
                        if(functions.isValueNull(request.getParameter("approvalCode"))){
                            approvalCode=request.getParameter("approvalCode");
                        }
                        if(functions.isValueNull(request.getParameter("actionCode"))){
                            actionCode=request.getParameter("actionCode");
                        }
                        if(functions.isValueNull(request.getParameter("orderStatus"))){
                            orderStatus=request.getParameter("orderStatus");
                        }
                        if(functions.isValueNull(request.getParameter("postDate"))){
                            postDate=request.getParameter("postDate");
                        }
                        if(functions.isValueNull(request.getParameter("orderid"))){
                            orderid=request.getParameter("orderid");
                        }
                        if(functions.isValueNull(request.getParameter("eci"))){
                            orderid=request.getParameter("eci");
                        }

                        if("2".equalsIgnoreCase(orderStatus) && "0".equalsIgnoreCase(actionCode)){
                            status="success";
                        }else{
                            status="fail";
                        }
                        transRespDetails.setStatus(status);
                        transRespDetails.setRemark(ActionCodeDescription);
                        transRespDetails.setDescription(ActionCodeDescription);
                        transRespDetails.setTransactionId(orderid);
                        transRespDetails.setErrorCode(actionCode);
                        transRespDetails.setBankTransactionDate(postDate);

                    }else if(fromType.equalsIgnoreCase("awepay")){
                        transRespDetails= new Comm3DResponseVO();
                        String data="";
                        if(functions.isValueNull(request.getParameter("status"))){
                            data=request.getParameter("status");
                            String dec = decrypt(data, "a3b4b7");
                            data = java.net.URLDecoder.decode(dec, "UTF-8");

                            Map map= AwePayUtils.getQueryMap(data);
                            String resStatus="";
                            String txId="";
                            String amt="";
                            String descriptor="";
                            String errorCode="";
                            String errorMsg="";
                            if(map!=null && map.size()!=0)
                            {
                                if(map.get("status")!=null)
                                {
                                    resStatus=(String)map.get("status");
                                }
                                if(map.get("txid")!=null)
                                {
                                    txId=(String)map.get("txid");
                                }
                                if(map.get("amount")!=null)
                                {
                                    amt=(String)map.get("amount");
                                }
                                if(map.get("descriptor")!=null)
                                {
                                    descriptor=(String)map.get("descriptor");
                                }
                                if(map.get("error[code]")!=null)
                                {
                                    errorCode=(String)map.get("error[code]");
                                }
                                if(map.get("error[msg]")!=null)
                                {
                                    errorMsg=(String)map.get("error[msg]");
                                }
                            }

                            transactionLogger.debug("map -----" + map);
                            transactionLogger.debug("resStatus -----" + resStatus);
                            transactionLogger.debug("txId -----" + txId);
                            transactionLogger.debug("amt -----" + amt);
                            transactionLogger.debug("descriptor -----" + descriptor);
                            transactionLogger.debug("errorCode -----" + errorCode);
                            transactionLogger.debug("errorMsg -----" + errorMsg);

                            if(resStatus.equalsIgnoreCase("OK"))
                            {
                                transRespDetails.setStatus("success");
                                transRespDetails.setDescriptor(descriptor);
                                if(functions.isValueNull(errorMsg))
                                {
                                    transRespDetails.setRemark("Transaction Successful");
                                    transRespDetails.setDescription("Transaction Successful");
                                }
                                else
                                {
                                    transRespDetails.setRemark(errorMsg);
                                }
                            }
                            else
                            {
                                transRespDetails.setStatus("fail");
                                if(functions.isValueNull(errorMsg))
                                {
                                    transRespDetails.setRemark("Transaction Failed");
                                    transRespDetails.setDescription("Transaction Failed");
                                }
                                else
                                {
                                    transRespDetails.setRemark(errorMsg);
                                }
                            }

                            transRespDetails.setErrorCode(errorCode);
                            transRespDetails.setTransactionId(txId);
                            transRespDetails.setAmount(amt);

                        }
                    }else if(fromType.equalsIgnoreCase("beekash")){
                        if (functions.isValueNull(request.getParameter("trackingId")))
                        {
                            trackingId = request.getParameter("trackingId");
                            if (trackingId.contains("?"))
                            {
                                String value[] = trackingId.split("\\?");
                                trackingId = value[0];
                                paymentid=value[1];

                                if(functions.isValueNull(paymentid)){
                                    String data[]=paymentid.split("=");
                                    paymentid=data[1];
                                }
                            }
                        }
                    }else if(fromType.equalsIgnoreCase("Appletree")){
                         PaymentType    = GatewayAccountService.getPaymentTypes(transactionDetailsVO.getPaymodeId());
                        //trackingId      = request.getParameter("trackingId");
                        enData          = request.getParameter("data");
                        String AuthCode ="";
                       // String OPcode   ="";
                        String acsurl   ="";
                        String pareq    ="";

                        if(functions.isValueNull(request.getParameter("AuthCode"))){
                            AuthCode    = request.getParameter("AuthCode");
                        }
                        /*if(functions.isValueNull(request.getParameter("OPcode"))){
                            OPcode=request.getParameter("OPcode");
                        }*/
                        if(functions.isValueNull(request.getParameter("PaRes"))){
                            paRes   = request.getParameter("PaRes");
                        }
                        if(functions.isValueNull(request.getParameter("MD"))){
                            md      = request.getParameter("MD");
                        }
                        if(functions.isValueNull(request.getParameter("pareq"))){
                            pareq   = request.getParameter("pareq");
                        }
                        transactionLogger.debug("paRes-----" + paRes + "-----MD--" + md);
                        comm3DRequestVO.setPaRes(paRes);
                        comm3DRequestVO.setMd(md);
                        comm3DRequestVO.setAuthCode(AuthCode);
                        comm3DRequestVO.setPaReq(pareq);
                        comm3DRequestVO.setOptionalCode(OPcode);
                    }else if(fromType.equalsIgnoreCase("ubamc")){
                        transactionLogger.error("---------- Inside UBA MC ---------");
                        String ubamcTransactionId    = "";
                        String ubaResult             = "";
                        String orderId               = "";
                        if (functions.isValueNull(request.getParameter("order.id")))
                        {
                            orderId = request.getParameter("order.id");
                        }

                        if(request.getParameter("transaction.id") != null){
                            paymentid = request.getParameter("transaction.id");
                        }
                        if(request.getParameter("response.gatewayRecommendation") != null){
                            gatewayRecommendation = request.getParameter("response.gatewayRecommendation");
                        }
                        if(request.getParameter("result") != null){
                            ubaResult = request.getParameter("result");
                        }
                        transactionLogger.debug("orderId "+" "+orderId+" paymentid "+" "+paymentid+" gatewayRecommendation"+" "+gatewayRecommendation+" "+ubaResult+" "+ubaResult);

                    }
                    else
                    {
                        transactionLogger.debug("----inside else----");
                        if(functions.isValueNull(request.getParameter("PaRes")))
                            paRes=request.getParameter("PaRes");
                        if(functions.isValueNull(request.getParameter("MD")))
                            md=request.getParameter("MD");
                        transactionLogger.debug("paRes-----"+paRes+"-----MD--"+md);
                        comm3DRequestVO.setPaRes(paRes);
                        comm3DRequestVO.setMd(md);
                    }

                    transactionLogger.debug("paymentId-------"+paymentid);
                    transactionLogger.debug("toid------"+toId);
                    transactionLogger.debug("amount------"+amount);
                    transactionLogger.debug("currency------"+currency);

                    if (functions.isValueNull(transactionDetailsVO.getCcnum()))
                    {
                        ccnum = PzEncryptor.decryptPAN(transactionDetailsVO.getCcnum());
                    }
                    if (functions.isValueNull(transactionDetailsVO.getExpdate()))
                    {
                        expDate = PzEncryptor.decryptExpiryDate(transactionDetailsVO.getExpdate());
                        String temp[] = expDate.split("/");

                        if (functions.isValueNull(temp[0]))
                        {
                            expMonth = temp[0];
                        }
                        if (functions.isValueNull(temp[1]))
                        {
                            expYear = temp[1];
                        }

                    }
                    if(functions.isValueNull(enData))
                    {
                        cvv=PzEncryptor.decryptCVV(enData);
                        transactionLogger.error("cvvv"+ cvv);
                    }
                    MerchantDAO merchantDAO = new MerchantDAO();
                    MerchantDetailsVO merchantDetailsVO = merchantDAO.getMemberDetails(toId);

                    if (merchantDetailsVO != null)
                    {
                        autoRedirect = merchantDetailsVO.getAutoRedirect();
                        logoName = merchantDetailsVO.getLogoName();
                        partnerName = merchantDetailsVO.getPartnerName();
                        isService = merchantDetailsVO.getIsService();
                        if (!"N".equalsIgnoreCase(merchantDetailsVO.getMarketPlace()))
                        {
                            marketPlaceVOList = transactionManager.getChildDetailsByParentTrackingid(trackingId);
                        }
                    }

                    auditTrailVO.setActionExecutorName("AcquirerFrontEnd");
                    auditTrailVO.setActionExecutorId(toId);
                    PaymentManager paymentManager = new PaymentManager();
                    transactionLogger.debug("dbStatus------"+dbStatus);
                    if (PZTransactionStatus.AUTH_STARTED.toString().equalsIgnoreCase(dbStatus))
                    {
                        notificationUrl = transactionDetailsVO.getNotificationUrl();
                        int k = paymentManager.mark3DTransaction(trackingId);
                        transactionLogger.error("paymentManager.mark3DTransaction().k=" + k);
                        if (k == 1)
                        {

                            CommAddressDetailsVO commAddressDetailsVO = new CommAddressDetailsVO();
                            CommTransactionDetailsVO commTransactionDetailsVO = new CommTransactionDetailsVO();
                            CommCardDetailsVO commCardDetailsVO= new CommCardDetailsVO();
                            CommMerchantVO commMerchantVO = new CommMerchantVO();

                            commCardDetailsVO.setCardNum(ccnum);
                            commCardDetailsVO.setExpMonth(expMonth);
                            commCardDetailsVO.setExpYear(expYear);
                            commCardDetailsVO.setcVV(cvv);
                            commCardDetailsVO.setCardType(transactionDetailsVO.getCardtype());

                            commTransactionDetailsVO.setPreviousTransactionId(paymentid);
                            commTransactionDetailsVO.setResponseHashInfo(paymentid);
                            commTransactionDetailsVO.setAmount(amount);
                            commTransactionDetailsVO.setCurrency(currency);

                            commAddressDetailsVO.setFirstname(firstName);
                            commAddressDetailsVO.setLastname(lastName);
                            commAddressDetailsVO.setStreet(transactionDetailsVO.getStreet());
                            commAddressDetailsVO.setState(transactionDetailsVO.getState());
                            commAddressDetailsVO.setCity(transactionDetailsVO.getCity());
                            commAddressDetailsVO.setCountry(transactionDetailsVO.getCountry());
                            commAddressDetailsVO.setTmpl_amount(transactionDetailsVO.getTemplateamount());
                            commAddressDetailsVO.setTmpl_currency(transactionDetailsVO.getTemplatecurrency());
                            commAddressDetailsVO.setCardHolderIpAddress(reqIp);
                            commAddressDetailsVO.setIp(transactionDetailsVO.getIpAddress());
                            commAddressDetailsVO.setEmail(email);
                            commAddressDetailsVO.setPhone(transactionDetailsVO.getTelno());
                            commAddressDetailsVO.setZipCode(transactionDetailsVO.getZip());

                            commMerchantVO.setIsService(isService);
                            commMerchantVO.setBrandName(merchantDetailsVO.getBrandName());
                            commMerchantVO.setAddress(merchantDetailsVO.getAddress());
                            comm3DRequestVO.setTransDetailsVO(commTransactionDetailsVO);
                            comm3DRequestVO.setAddressDetailsVO(commAddressDetailsVO);
                            comm3DRequestVO.setCommMerchantVO(commMerchantVO);
                            comm3DRequestVO.setCardDetailsVO(commCardDetailsVO);
                            ActionEntry actionEntry = new ActionEntry();
                            StatusSyncDAO statusSyncDAO = new StatusSyncDAO();
                            CommonPaymentProcess paymentProcess = new CommonPaymentProcess();
                            pg=AbstractPaymentGateway.getGateway(accountId);
                            transactionLogger.debug("isService------"+isService);
                            transactionLogger.debug("fromType-------"+fromType);

                            if ("Y".equalsIgnoreCase(isService) )
                            {
                                transType="Sale";
                                actionEntry.actionEntryFor3DCommon(trackingId, amount, ActionEntry.ACTION_3D_CONFIRMATION_STARTED, ActionEntry.STATUS_3D_CONFIRMATION, transRespDetails, comm3DRequestVO, auditTrailVO, null);
                                if(!"N".equalsIgnoreCase(merchantDetailsVO.getMarketPlace()) && marketPlaceVOList!=null && marketPlaceVOList.size()>0)
                                {
                                    for(int i=0;i<marketPlaceVOList.size();i++)
                                    {
                                        marketPlaceVO=marketPlaceVOList.get(i);
                                        actionEntry.actionEntryFor3DCommon(marketPlaceVO.getTrackingid(), marketPlaceVO.getAmount(), ActionEntry.ACTION_3D_CONFIRMATION_STARTED, ActionEntry.STATUS_3D_CONFIRMATION, transRespDetails, comm3DRequestVO, auditTrailVO, null);
                                    }
                                }
                                if("bennupay".equalsIgnoreCase(fromType))
                                {
                                    transRespDetails = (Comm3DResponseVO) pg.processCommon3DSaleConfirmation(trackingId, comm3DRequestVO);
                                }
                                if("Appletree".equalsIgnoreCase(fromType))
                                {
                                    commTransactionDetailsVO.setOrderDesc(orderDesc);

                                    transactionLogger.error("CREDIT_CARD_PAYMODE " + String.valueOf(PaymentModeEnum.CREDIT_CARD_PAYMODE.getValue()));
                                    if(String.valueOf(PaymentModeEnum.CREDIT_CARD_PAYMODE.getValue()).equalsIgnoreCase(transactionDetailsVO.getPaymodeId())
                                            || String.valueOf(PaymentModeEnum.DEBIT_CARD_PAYMODE.getValue()).equalsIgnoreCase(transactionDetailsVO.getPaymodeId())
                                            ){
                                        transRespDetails = (Comm3DResponseVO) pg.processCommon3DSaleConfirmation(trackingId, comm3DRequestVO);
                                    }else{
                                        CommRequestVO requestVO                     = new CommRequestVO();
                                        commTransactionDetailsVO.setPreviousTransactionId(paymentid);
                                        requestVO.setTransDetailsVO(commTransactionDetailsVO);
                                        transRespDetails = (Comm3DResponseVO) pg.processQuery(trackingId, requestVO);
                                    }
                                }else if("ubamc".equalsIgnoreCase(fromType)){
                                    if("PROCEED".equalsIgnoreCase(gatewayRecommendation)){
                                        transRespDetails = (Comm3DResponseVO) pg.processCommon3DSaleConfirmation(trackingId, comm3DRequestVO);
                                    }else{
                                        CommRequestVO requestVO                     = new CommRequestVO();
                                        commTransactionDetailsVO.setPreviousTransactionId(paymentid);
                                        requestVO.setTransDetailsVO(commTransactionDetailsVO);
                                        transRespDetails = (Comm3DResponseVO) pg.processQuery(trackingId, comm3DRequestVO);
                                    }

                                }else if( !("sabadell".equalsIgnoreCase(fromType) || "zotapay".equalsIgnoreCase(fromType) ||"npayon".equalsIgnoreCase(fromType)
                                        ||"rsp".equalsIgnoreCase(fromType) || "agnipay".equalsIgnoreCase(fromType) || "trnsactWLD".equalsIgnoreCase(fromType) || "shimotomo".equalsIgnoreCase(fromType) || "SBMGateway".equalsIgnoreCase(fromType) || "awepay".equalsIgnoreCase(fromType)
                                        || "beekash".equalsIgnoreCase(fromType) ||"triple000".equalsIgnoreCase(fromType)))
                                {
                                    transRespDetails = (Comm3DResponseVO) pg.processCommon3DSaleConfirmation(trackingId, comm3DRequestVO);
                                }
                            }
                            else
                            {
                                transType="Auth";
                                actionEntry.actionEntryFor3DCommon(trackingId, amount, ActionEntry.ACTION_3D_CONFIRMATION_STARTED, ActionEntry.STATUS_3D_CONFIRMATION, transRespDetails, comm3DRequestVO, auditTrailVO, null);
                                if(!"N".equalsIgnoreCase(merchantDetailsVO.getMarketPlace()) && marketPlaceVOList!=null && marketPlaceVOList.size()>0)
                                {
                                    for(int i=0;i<marketPlaceVOList.size();i++)
                                    {
                                        actionEntry.actionEntryFor3DCommon(marketPlaceVO.getTrackingid(), marketPlaceVO.getAmount(), ActionEntry.ACTION_3D_CONFIRMATION_STARTED, ActionEntry.STATUS_3D_CONFIRMATION, transRespDetails, comm3DRequestVO, auditTrailVO, null);
                                    }
                                }
                                if("ubamc".equalsIgnoreCase(fromType)){
                                    if("PROCEED".equalsIgnoreCase(gatewayRecommendation)){
                                        transactionLogger.error("fromType inside PROCEED ----> "+fromType);
                                        transRespDetails = (Comm3DResponseVO) pg.processCommon3DAuthConfirmation(trackingId, comm3DRequestVO);
                                    }else{
                                        transactionLogger.error("fromType inside processInquiry ----> "+pg);
                                        CommRequestVO requestVO                     = new CommRequestVO();
                                        commTransactionDetailsVO.setPreviousTransactionId(paymentid);
                                        requestVO.setTransDetailsVO(commTransactionDetailsVO);
                                        transRespDetails= (Comm3DResponseVO) pg.processQuery(trackingId, comm3DRequestVO);
                                    }
                                }
                                else if( !("sabadell".equalsIgnoreCase(fromType) || "Appletree".equalsIgnoreCase(fromType) || "zotapay".equalsIgnoreCase(fromType) ||"npayon".equalsIgnoreCase(fromType) ||"rsp".equalsIgnoreCase(fromType) || "agnipay".equalsIgnoreCase(fromType) || "trnsactWLD".equalsIgnoreCase(fromType) || "shimotomo".equalsIgnoreCase(fromType) || "SBMGateway".equalsIgnoreCase(fromType) || "awepay".equalsIgnoreCase(fromType) || "beekash".equalsIgnoreCase(fromType) ||"triple000".equalsIgnoreCase(fromType)))
                                {
                                    commTransactionDetailsVO.setOrderDesc(orderDesc);
                                    transactionLogger.error("fromType inside another equalsIgnoreCase ----> "+fromType);
                                    System.out.println(fromType);
                                    transRespDetails = (Comm3DResponseVO) pg.processCommon3DAuthConfirmation(trackingId, comm3DRequestVO);

                                }
                            }
                            transactionLogger.debug("fromType-----"+fromType);

                            if("npayon".equalsIgnoreCase(fromType) || "pvp".equalsIgnoreCase(fromType) || "beekash".equalsIgnoreCase(fromType)){
                                transRespDetails= (Comm3DResponseVO) pg.processInquiry(comm3DRequestVO);
                            }

                            transactionLogger.debug("--- transRespDetails---"+transRespDetails);
                            if (transRespDetails != null)
                            {
                                transactionLogger.debug("---inside transRespDetails---"+transRespDetails);
                                transactionStatus   = transRespDetails.getStatus();
                                transactionId       = transRespDetails.getTransactionId();
                                if(functions.isValueNull(transRespDetails.getAuthCode())){
                                    authorization_code = transRespDetails.getAuthCode();
                                }
                                message = transRespDetails.getDescription();
                                transactionLogger.error("message--->"+message);
                                eci         = transRespDetails.getEci();
                                errorName   = transRespDetails.getErrorName();
                                billingDesc = transRespDetails.getDescriptor();

                                if(functions.isValueNull(transRespDetails.getRrn())){
                                    rrn = transRespDetails.getRrn();
                                }

                                if(!functions.isValueNull(transRespDetails.getCurrency()))
                                    transRespDetails.setCurrency(currency);
                                if(!functions.isValueNull(transRespDetails.getTmpl_Amount()))
                                    transRespDetails.setTmpl_Amount(tmpl_amt);
                                if(!functions.isValueNull(transRespDetails.getTmpl_Currency()))
                                    transRespDetails.setTmpl_Currency(tmpl_currency);
                                if(functions.isValueNull(transRespDetails.getAmount()))
                                    amount = transRespDetails.getAmount();

                                transactionLogger.debug("transactionStatus-----"+transactionStatus);
                                StringBuffer dbBuffer       = new StringBuffer();
                                StringBuffer childDBBuffer  = null;
                                con = Database.getConnection();
                                transactionLogger.error("transactionStatus------------->"+transactionStatus);

                                if("paysend".equalsIgnoreCase(fromType)){
                                    TransactionDetailsVO transactionDetailsVO2 = transactionManager.getTransDetailFromCommon(trackingId);
                                    dbStatus = transactionDetailsVO2.getStatus();
                                    transactionLogger.error(" ----- new db status -----"+dbStatus);
                                }

                                transactionLogger.error("after if db status ----"+dbStatus);

                                if ((PZTransactionStatus.AUTH_STARTED.toString().equals(dbStatus) || PZTransactionStatus.AUTHSTARTED_3D.toString().equals(dbStatus)) && "success".equalsIgnoreCase(transactionStatus))
                                {
                                    transactionLogger.error("------------ INSIDE IF SUCCESS -----------");
                                    status              = "success";
                                    confirmStatus       = "Y";
                                    responseStatus      = "Successful";
                                    transactionLogger.error("billingDesc---->"+billingDesc);
                                    if(!functions.isValueNull(billingDesc)){
                                        billingDesc = gatewayAccount.getDisplayName();
                                    }
                                    transactionLogger.error("transType--------------->"+transType);
                                    if ("Sale".equalsIgnoreCase(transType))
                                    {
                                        transRespDetails.setTransactionType(PZProcessType.THREE_D_SALE.toString());
                                        dbBuffer.append("update transaction_common set captureamount='" + amount + "',paymentid='" + transactionId + "',status='capturesuccess' ,eci='" + eci + "',customerIp='" + reqIp + "',customerIpCountry='" + customerIpCountry + "',authorization_code='"+authorization_code+"',rrn='"+rrn+"'" + ",successtimestamp='" + functions.getTimestamp() +"'");
                                        dbBuffer.append(" ,remark='" + message + "' where trackingid = " + trackingId);
                                        transactionLogger.error("dbBuffer--->"+dbBuffer);
                                        Database.executeUpdate(dbBuffer.toString(), con);
                                        paymentProcess.actionEntry(trackingId, amount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, transRespDetails, null, auditTrailVO);
                                        statusSyncDAO.updateAllTransactionFlowFlag(trackingId, "capturesuccess");
                                        updatedStatus   = PZTransactionStatus.CAPTURE_SUCCESS.toString();
                                        if(!"N".equalsIgnoreCase(merchantDetailsVO.getMarketPlace()) && marketPlaceVOList!=null && marketPlaceVOList.size()>0)
                                        {
                                            for(int i=0;i<marketPlaceVOList.size();i++)
                                            {
                                                marketPlaceVO   = marketPlaceVOList.get(i);
                                                childDBBuffer   = new StringBuffer();
                                                transRespDetails.setTransactionType(PZProcessType.THREE_D_SALE.toString());
                                                childDBBuffer.append("update transaction_common set captureamount='" + marketPlaceVO.getAmount() + "',paymentid='" + transactionId + "',status='capturesuccess' ,eci='" + eci + "',remark='" + message + "',customerIp='" + reqIp + "',customerIpCountry='" + customerIpCountry + "',successtimestamp='" + functions.getTimestamp()  +"' where trackingid = " + marketPlaceVO.getTrackingid());
                                                transactionLogger.error("childDBBuffer------------>" + childDBBuffer);
                                                Database.executeUpdate(childDBBuffer.toString(), con);
                                                paymentProcess.actionEntry(marketPlaceVO.getTrackingid(), marketPlaceVO.getAmount(), ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, transRespDetails, null, auditTrailVO);
                                                statusSyncDAO.updateAllTransactionFlowFlag(marketPlaceVO.getTrackingid(), "capturesuccess");
                                            }
                                        }
                                    }
                                    else
                                    {
                                        transRespDetails.setTransactionType(PZProcessType.THREE_D_AUTH.toString());
                                        dbBuffer.append("update transaction_common set paymentid='" + transactionId + "' ,status='authsuccessful' ,eci='" + eci + "',customerIp='" + reqIp + "',customerIpCountry='" + customerIpCountry + "',authorization_code='"+authorization_code+"',rrn='"+rrn+"'" + ",successtimestamp='" + functions.getTimestamp() +"'");
                                        dbBuffer.append(" ,remark='" + message + "' where trackingid = " + trackingId);
                                        transactionLogger.error("dbBuffer--->"+dbBuffer);
                                        Database.executeUpdate(dbBuffer.toString(), con);
                                        paymentProcess.actionEntry(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_SUCCESSFUL, ActionEntry.STATUS_AUTHORISTION_SUCCESSFUL, transRespDetails, null, auditTrailVO);
                                        statusSyncDAO.updateAllTransactionFlowFlag(trackingId, "authsuccessful");
                                        updatedStatus=PZTransactionStatus.AUTH_SUCCESS.toString();
                                        if(!"N".equalsIgnoreCase(merchantDetailsVO.getMarketPlace()) && marketPlaceVOList!=null && marketPlaceVOList.size()>0)
                                        {
                                            for(int i=0;i<marketPlaceVOList.size();i++)
                                            {
                                                marketPlaceVO   = marketPlaceVOList.get(i);
                                                childDBBuffer   = new StringBuffer();
                                                transRespDetails.setTransactionType(PZProcessType.THREE_D_AUTH.toString());
                                                childDBBuffer.append("update transaction_common set paymentid='" + transactionId + "' ,status='authsuccessful' ,eci='" + eci + "',remark='" + message + "',customerIp='" + reqIp + "',customerIpCountry='" + customerIpCountry + "',successtimestamp='" + functions.getTimestamp() + "' where trackingid = " + marketPlaceVO.getTrackingid());
                                                transactionLogger.error("childDBBuffer--->"+childDBBuffer);
                                                Database.executeUpdate(childDBBuffer.toString(), con);
                                                paymentProcess.actionEntry(marketPlaceVO.getTrackingid(), marketPlaceVO.getAmount(), ActionEntry.ACTION_AUTHORISTION_SUCCESSFUL, ActionEntry.STATUS_AUTHORISTION_SUCCESSFUL, transRespDetails, null, auditTrailVO);
                                                statusSyncDAO.updateAllTransactionFlowFlag(marketPlaceVO.getTrackingid(), "authsuccessful");
                                            }
                                        }
                                    }
                                }
                                else if ("pending".equalsIgnoreCase(transactionStatus))
                                {
                                    transactionLogger.debug("-----inside pending-----");
                                    status          = "pending";
                                    responseStatus  = "Pending";
                                    message         = "FE:Transaction is pending";
                                    updatedStatus   = PZTransactionStatus.AUTH_STARTED.toString();
                                    dbBuffer.append("update transaction_common set paymentid='" + transactionId + "',eci='" + eci + "',customerIp='"+reqIp+"',customerIpCountry='"+customerIpCountry+"'");
                                    dbBuffer.append(" ,remark='" + message + "' where trackingid = " + trackingId);
                                    Database.executeUpdate(dbBuffer.toString(), con);
                                }
                                else if (PZTransactionStatus.AUTH_STARTED.toString().equals(dbStatus) || PZTransactionStatus.AUTHSTARTED_3D.toString().equals(dbStatus))
                                {
                                    transactionLogger.error("------------ INSIDE ELSE IF FAIL -----------");
                                    confirmStatus   = "N";
                                    status          = "fail";
                                    if(!functions.isValueNull(message)){
                                        message = "fail";
                                    }
                                    responseStatus = "Failed(" + message + ")";
                                    dbBuffer.append("update transaction_common set status='authfailed',paymentid='" + transactionId + "',eci='" + eci + "',customerIp='"+reqIp+"',customerIpCountry='"+customerIpCountry+"'" + ",failuretimestamp='" + functions.getTimestamp() +"'");
                                    dbBuffer.append(" ,remark='" + message + "' where trackingid = " + trackingId);
                                    transactionLogger.error("dbBuffer--->"+dbBuffer);
                                    Database.executeUpdate(dbBuffer.toString(), con);
                                    actionEntry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, transRespDetails, auditTrailVO, null);
                                    statusSyncDAO.updateAllTransactionFlowFlag(trackingId, "authfailed");
                                    updatedStatus=PZTransactionStatus.AUTH_FAILED.toString();
                                    if(!"N".equalsIgnoreCase(merchantDetailsVO.getMarketPlace()) && marketPlaceVOList!=null && marketPlaceVOList.size()>0)
                                    {
                                        for(int i=0;i<marketPlaceVOList.size();i++)
                                        {
                                            marketPlaceVO=marketPlaceVOList.get(i);
                                            childDBBuffer=new StringBuffer();
                                            childDBBuffer.append("update transaction_common set status='authfailed',paymentid='" + transactionId + "',eci='" + eci + "',remark='" + message + "',customerIp='"+reqIp+"',customerIpCountry='"+customerIpCountry + "',failuretimestamp='" + functions.getTimestamp() +"' where trackingid = " + marketPlaceVO.getTrackingid());
                                            transactionLogger.error("childDBBuffer--->"+childDBBuffer);
                                            Database.executeUpdate(childDBBuffer.toString(), con);
                                            actionEntry.actionEntryForCommon(marketPlaceVO.getTrackingid(), marketPlaceVO.getAmount(), ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, transRespDetails, auditTrailVO, null);
                                            statusSyncDAO.updateAllTransactionFlowFlag(marketPlaceVO.getTrackingid(), "authfailed");
                                        }
                                    }
                                }
                                else if (PZTransactionStatus.CAPTURE_SUCCESS.toString().equals(dbStatus) || PZTransactionStatus.AUTH_SUCCESS.toString().equals(dbStatus))
                                {
                                    billingDesc = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                                    status = "success";
                                    message = "Transaction Successful";
                                    responseStatus = "Successful";
                                    updatedStatus=PZTransactionStatus.CAPTURE_SUCCESS.toString();

                                }
                                else if (PZTransactionStatus.AUTH_SUCCESS.toString().equals(dbStatus))
                                {
                                    billingDesc = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                                    status = "success";
                                    message = "Transaction Successful";
                                    responseStatus = "Successful";
                                    updatedStatus=PZTransactionStatus.AUTH_SUCCESS.toString();
                                }
                                else if (PZTransactionStatus.AUTH_FAILED.toString().equals(dbStatus))
                                {
                                    status = "fail";
                                    //confirmStatus = "N";
                                    responseStatus = "Failed";
                                    message = "Failed";
                                    updatedStatus=PZTransactionStatus.AUTH_FAILED.toString();
                                }
                                else if (PZTransactionStatus.AUTHSTARTED_3D.toString().equals(dbStatus))
                                {
                                    status = "pending";
                                    //confirmStatus = "P";
                                    responseStatus = "Pending";
                                    message = "Transaction is in progress";
                                    updatedStatus=PZTransactionStatus.AUTHSTARTED_3D.toString();
                                }
                                else
                                {
                                    status = "fail";
                                    //confirmStatus = "N";
                                    responseStatus = "Failed(Transaction not found in correct status)";
                                    message = "Failed(Transaction not found in correct status)";
                                    updatedStatus=PZTransactionStatus.FAILED.toString();
                                }

                                transactionLogger.debug("-----dbBuffer-----" + dbBuffer);

                                /*AsynchronousMailService asynchronousMailService = AsynchronousMailService.getInstance();
                                asynchronousMailService.sendEmail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, message, billingDesc);

                                AsynchronousSmsService smsService = AsynchronousSmsService.getInstance();
                                smsService.sendSMS(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, message, billingDesc);
*/
                            }
                            else
                            {
                                transactionLogger.debug("-----inside pending-----");
                                status = "pending";
                                //confirmStatus = "P";
                                responseStatus = "Pending";
                                message = "FE:Transaction is pending";
                                updatedStatus=PZTransactionStatus.AUTH_STARTED.toString();
                            }
                        }
                        else
                        {
                            status = "pending";
                            //confirmStatus = "P";
                            responseStatus = "Pending";
                            message = "FE:Transaction is in progress";
                            updatedStatus=PZTransactionStatus.AUTH_STARTED.toString();
                        }
                    }
                    else
                    {
                        if (PZTransactionStatus.CAPTURE_SUCCESS.toString().equals(dbStatus) || PZTransactionStatus.AUTH_SUCCESS.toString().equals(dbStatus))
                        {
                            billingDesc = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                            status = "success";
                            message = "Transaction Successful";
                            responseStatus = "Successful";
                            updatedStatus=PZTransactionStatus.CAPTURE_SUCCESS.toString();

                        } else if (PZTransactionStatus.AUTH_SUCCESS.toString().equals(dbStatus))
                        {
                            billingDesc = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                            status = "success";
                            message = "Transaction Successful";
                            responseStatus = "Successful";
                            updatedStatus=PZTransactionStatus.AUTH_SUCCESS.toString();
                        }
                        else if (PZTransactionStatus.AUTH_FAILED.toString().equals(dbStatus))
                        {
                            status = "fail";
                            //confirmStatus = "N";
                            responseStatus = "Failed";
                            message = "Failed";
                            updatedStatus=PZTransactionStatus.AUTH_FAILED.toString();
                        }
                        else if (PZTransactionStatus.AUTHSTARTED_3D.toString().equals(dbStatus))
                        {
                            status = "pending";
                            //confirmStatus = "P";
                            responseStatus = "Pending";
                            message = "Transaction is in progress";
                            updatedStatus=PZTransactionStatus.AUTHSTARTED_3D.toString();
                        }
                        else
                        {
                            status = "fail";
                            //confirmStatus = "N";
                            responseStatus = "Failed(Transaction not found in correct status)";
                            message = "Failed(Transaction not found in correct status)";
                            updatedStatus=PZTransactionStatus.FAILED.toString();
                        }
                    }

                    commonValidatorVO.setTrackingid(trackingId);
                    transDetailsVO.setOrderId(description);
                    transDetailsVO.setOrderDesc(orderDesc);
                    transDetailsVO.setAmount(amount);
                    transDetailsVO.setCurrency(currency);
                    transDetailsVO.setRedirectUrl(redirectUrl);
                    transactionLogger.error("billingDesc--->"+billingDesc);
                    transDetailsVO.setBillingDiscriptor(billingDesc);
                    transDetailsVO.setNotificationUrl(notificationUrl);
                    transDetailsVO.setRedirectMethod(transactionDetailsVO.getRedirectMethod());

                    addressDetailsVO.setEmail(email);
                    addressDetailsVO.setFirstname(firstName);
                    addressDetailsVO.setLastname(lastName);
                    addressDetailsVO.setTmpl_amount(tmpl_amt);
                    addressDetailsVO.setTmpl_currency(tmpl_currency);
                    addressDetailsVO.setCity(city);
                    addressDetailsVO.setZipCode(zip);
                    addressDetailsVO.setStreet(street);
                    addressDetailsVO.setCountry(country);
                    addressDetailsVO.setTelnocc(telcc);
                    addressDetailsVO.setPhone(telno);
                    addressDetailsVO.setState(state);
                   if (session.getAttribute("language") !=null)
                   {
                       addressDetailsVO.setLanguage(session.getAttribute("language").toString());
                   }
                    else
                   {
                       addressDetailsVO.setLanguage("");
                   }
                    cardDetailsVO.setCardNum(ccnum);
                    cardDetailsVO.setExpMonth(expMonth);
                    cardDetailsVO.setExpYear(expYear);

                    commonValidatorVO.setErrorName(errorName);
                    commonValidatorVO.setReason(message);
                    commonValidatorVO.setLogoName(logoName);
                    commonValidatorVO.setPartnerName(partnerName);
                    commonValidatorVO.setPaymentType(paymodeid);
                    commonValidatorVO.setCardType(cardtypeid);
                    commonValidatorVO.setCustomerId(customerId);
                    commonValidatorVO.setCardDetailsVO(cardDetailsVO);
                    commonValidatorVO.setTransDetailsVO(transDetailsVO);
                    commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
                    commonValidatorVO.setAddressDetailsVO(addressDetailsVO);
                    commonValidatorVO.setEci(eci);
                    commonValidatorVO.setTerminalId(terminalid);
                    commonValidatorVO.setMarketPlaceVOList(marketPlaceVOList);

                    transactionUtility.setToken(commonValidatorVO, responseStatus);

                    if(!functions.isValueNull(notificationUrl) && functions.isValueNull(merchantDetailsVO.getNotificationUrl())){
                        notificationUrl = merchantDetailsVO.getNotificationUrl();
                    }

                    transactionLogger.error("TransactionNotification flag for ---"+toId+"---"+merchantDetailsVO.getTransactionNotification());
                    if(functions.isValueNull(notificationUrl)) //&& ("3D".equals(merchantDetailsVO.getTransactionNotification())||"Both".equals(merchantDetailsVO.getTransactionNotification()))
                    {
                        transactionLogger.error("inside sending notification---" + notificationUrl);
                        TransactionDetailsVO transactionDetailsVO1 = transactionUtility.getTransactionDetails(commonValidatorVO);
                        transactionDetailsVO1.setBankReferenceId(transactionId);
                        transactionDetailsVO1.setTransactionMode("3D");
                        AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                        asyncNotificationService.sendNotification(transactionDetailsVO1, trackingId, updatedStatus, message,"");
                    }

                    if ("Y".equalsIgnoreCase(autoRedirect))
                    {
                        transactionUtility.doAutoRedirect(commonValidatorVO, response, responseStatus, billingDesc);
                    }
                    else
                    {
                        request.setAttribute("responceStatus", responseStatus);
                        request.setAttribute("displayName", billingDesc);
                        request.setAttribute("remark", message);
                        request.setAttribute("errorName", errorName);
                        request.setAttribute("transDetail", commonValidatorVO);
                        session.setAttribute("ctoken", ctoken);

                        String confirmationPage = "";

                        if (functions.isValueNull(version) && version.equalsIgnoreCase("2"))
                            confirmationPage = "/confirmationCheckout.jsp?ctoken=";
                        else
                            confirmationPage = "/confirmationpage.jsp?ctoken=";

                        RequestDispatcher rd = request.getRequestDispatcher(confirmationPage + ctoken);
                        rd.forward(request, response);
                    }
                }
            }
        }
        catch (PZDBViolationException e)
        {
            transactionLogger.error("error::::", e);
            PZExceptionHandler.raiseAndHandleDBViolationException("Common3DFrontEndServlet.java", "doService()", null, "Transaction", null, PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause(), toId, null);
        }
        catch (SystemError e){
            transactionLogger.error("SystemError::::::",e);
            PZExceptionHandler.raiseAndHandleDBViolationException("Common3DFrontEndServlet.java", "doService()", null, "Transaction", null, PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause(), toId, null);

        }
        catch (PZTechnicalViolationException e)
        {
            transactionLogger.error("error::::", e);
            PZExceptionHandler.raiseAndHandleTechnicalViolationException("Common3DFrontEndServlet.java", "doService()", null, "Transaction", null, PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause(), toId, null);
        }
        catch (PZConstraintViolationException e)
        {
            transactionLogger.error("error::::", e);
            PZExceptionHandler.raiseAndHandleConstraintViolationException("Common3DFrontEndServlet.java", "doService()", null, "Transaction", null, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, null, e.getMessage(), e.getCause(), toId, null);
        }
        catch (PZGenericConstraintViolationException e)
        {
            transactionLogger.error("error::::", e);
            PZExceptionHandler.raiseAndHandleConstraintViolationException("Common3DFrontEndServlet.java", "doService()", null, "Transaction", null, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, null, e.getMessage(), e.getCause(), toId, null);
        }catch (JSONException e)
        {
            transactionLogger.error("JSONException::::", e);
            PZExceptionHandler.raiseAndHandleConstraintViolationException("Common3DFrontEndServlet.java", "doService()", null, "Transaction", null, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, null, e.getMessage(), e.getCause(), toId, null);
        }
        catch (org.json.JSONException e)
        {
            transactionLogger.error("JSONException::::", e);
            PZExceptionHandler.raiseAndHandleConstraintViolationException("Common3DFrontEndServlet.java", "doService()", null, "Transaction", null, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, null, e.getMessage(), e.getCause(), toId, null);
        }catch (Exception e){
            transactionLogger.error("Exception::::", e);
            PZExceptionHandler.raiseAndHandleConstraintViolationException("Common3DFrontEndServlet.java", "doService()", null, "Transaction", null, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, null, e.getMessage(), e.getCause(), toId, null);
        }
        finally
        {
            Database.closeConnection(con);
        }
    }
}
