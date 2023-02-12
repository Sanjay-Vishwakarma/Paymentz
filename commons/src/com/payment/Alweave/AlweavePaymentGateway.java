package com.payment.Alweave;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.GenericRequestVO;
import com.directi.pg.core.valueObjects.GenericResponseVO;
import com.kcp.J_PP_CLI_N;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraint.PZGenericConstraint;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import com.payment.kcp.KCPUtils;
import com.payment.validators.vo.CommonValidatorVO;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.ResourceBundle;

/**
 * Created by Admin on 10/27/2020.
 */
public class AlweavePaymentGateway extends AbstractPaymentGateway
{
    private static TransactionLogger transactionLogger=new TransactionLogger(AlweavePaymentGateway.class.getName());
    public static final String GATEWAY_TYPE="alweave";
    final static ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.alweave");
    public AlweavePaymentGateway(String accountId){this.accountId=accountId;}
    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException
    {
        Functions functions=new Functions();
        Comm3DResponseVO comm3DResponseVO=new Comm3DResponseVO();
        CommRequestVO commRequestVO= (CommRequestVO) requestVO;
        CommTransactionDetailsVO transDetailsVO=commRequestVO.getTransDetailsVO();
        CommAddressDetailsVO addressDetailsVO=commRequestVO.getAddressDetailsVO();
        CommMerchantVO commMerchantVO=commRequestVO.getCommMerchantVO();
        CommDeviceDetailsVO commDeviceDetailsVO=commRequestVO.getCommDeviceDetailsVO();
        GatewayAccount gatewayAccount= GatewayAccountService.getGatewayAccount(accountId);
        StringBuffer html = new StringBuffer("");
        String sid=gatewayAccount.getMerchantId();
        String shop_user_id=addressDetailsVO.getCustomerid();
        String amount="";
        String lang="";
        if(!functions.isValueNull(shop_user_id))
            shop_user_id=trackingID;
        String orderDescription = "";
        if(functions.isValueNull(transDetailsVO.getOrderDesc()))
            orderDescription=transDetailsVO.getOrderDesc();
        else
            orderDescription=transDetailsVO.getOrderId();

        if("JPY".equalsIgnoreCase(transDetailsVO.getCurrency()))
            amount= AlweaveUtils.getJPYAmount(transDetailsVO.getAmount());
        else if("KWD".equalsIgnoreCase(transDetailsVO.getCurrency()))
            amount=AlweaveUtils.getKWDSupportedAmount(transDetailsVO.getAmount());
        else if("KRW".equalsIgnoreCase(transDetailsVO.getCurrency()))
            amount=AlweaveUtils.getKRWAmount(transDetailsVO.getAmount());
        else
            amount=AlweaveUtils.getCentAmount(transDetailsVO.getAmount());

        if(functions.isValueNull(addressDetailsVO.getLanguage()) && "ko".equalsIgnoreCase(addressDetailsVO.getLanguage()))
            lang="ko";
        else
            lang="en";
        String async_url="";
        if(commDeviceDetailsVO!=null && functions.isValueNull(commDeviceDetailsVO.getUser_Agent()) && commDeviceDetailsVO.getUser_Agent().contains("Mobile"))
            async_url=RB.getString("ASYNC_MOBILE_SALE_URL");
        else
            async_url=RB.getString("ASYNC_SALE_URL");
        String notify_url=RB.getString("NOTIFY_URL");
        String termUrl="";
        if(functions.isValueNull(commMerchantVO.getHostUrl()))
            termUrl="https://"+commMerchantVO.getHostUrl()+RB.getString("HOST_URL")+trackingID;
        else
            termUrl=RB.getString("TERM_URL")+trackingID;
        html.append("<form name=\"form1\" action=\""+async_url+"\" method=\"post\">");
        html.append("<input type=\"hidden\" name=\"sid\" value=\""+sid+"\">");
        html.append("<input type=\"hidden\" name=\"ordr_idxx\" value=\""+trackingID+"\">");
        html.append("<input type=\"hidden\" name=\"shop_user_id\" value=\""+shop_user_id+"\">");
        html.append("<input type=\"hidden\" name=\"good_name\" value=\""+orderDescription+"\">");
        html.append("<input type=\"hidden\" name=\"good_mny\" value=\""+amount+"\">");
        html.append("<input type=\"hidden\" name=\"return_url\" value=\""+notify_url+"\">");
        html.append("<input type=\"hidden\" name=\"user_url\" value=\""+termUrl+"\">");
        html.append("<input type=\"hidden\" name=\"lang_chk\" value=\""+lang+"\">");
        html.append("<input type=\"hidden\" name=\"used_card_YN\" value=\"Y\">");
        html.append("<input type=\"hidden\" name=\"used_card\" value=\"CCXB:CCXA\">");//CCXC
        html.append("</form><script>" +
                "document.form1.submit();</script>");
        transactionLogger.error("html---->"+html);
        comm3DResponseVO.setUrlFor3DRedirect(html.toString());
        comm3DResponseVO.setStatus("pending");
        return comm3DResponseVO;
    }
    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        errorCodeListVO.addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_FUNCTIONALITY_NOT_ALLOWED));
        PZGenericConstraint genConstraint = new PZGenericConstraint("AlweavePaymentGateway", "processAuthentication", null, "common", "This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::", errorCodeListVO);
        throw new PZGenericConstraintViolationException(genConstraint, "This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::", null);

    }
    public String processAutoRedirect(CommonValidatorVO commonValidatorVO) throws PZTechnicalViolationException, PZDBViolationException
    {
        transactionLogger.error(":::::Entered into processAutoRedirect for KCP:::::");
        CommRequestVO commRequestVO = null;
        Functions functions=new Functions();
        String html = "";
        try
        {
            commRequestVO=AlweaveUtils.getAlweaveRequestVO(commonValidatorVO);
            Comm3DResponseVO comm3DResponseVO= (Comm3DResponseVO) this.processSale(commonValidatorVO.getTrackingid(),commRequestVO);
            html=comm3DResponseVO.getUrlFor3DRedirect();
        }
        catch (PZConstraintViolationException e)
        {
            transactionLogger.error("PZConstraintViolationException----->",e);
        }
        return html;
    }
    public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException
    {
        transactionLogger.error("Inside processRefund ---");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommResponseVO commResponseVO = new CommResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();

        Functions functions = new Functions();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        String sid = gatewayAccount.getMerchantId();
        String res_cd="";
        String res_msg="";
        String refundUrl=RB.getString("REFUND_URL");
        String paymentId=commTransactionDetailsVO.getPreviousTransactionId();
        try
        {
            String request = "sid=" + sid + "&tno=" + paymentId + "&mod_desc=" + commTransactionDetailsVO.getOrderDesc();
            transactionLogger.error("Refund request---" + trackingID + "--->" + request);
            String response = AlweaveUtils.doHttpPostConnection(refundUrl, request);
            transactionLogger.error("Refund response---" + trackingID + "--->" + request);
            if (functions.isValueNull(response))
            {
                JSONObject responseJSON = new JSONObject(response);
                if(responseJSON.has("res_cd"))
                    res_cd=responseJSON.getString("res_cd");
                if(responseJSON.has("res_msg"))
                    res_msg=responseJSON.getString("res_msg");

                if("0000".equalsIgnoreCase(res_cd))
                {
                    commResponseVO.setStatus("success");
                    if(!functions.isValueNull(res_msg))
                        res_msg="Refund Successful";
                }else
                {
                    commResponseVO.setStatus("failed");
                    if(!functions.isValueNull(res_msg))
                        res_msg="Refund Failed";
                }
                commResponseVO.setRemark(res_msg);
                commResponseVO.setDescription(res_msg);
            }
        }
        catch (JSONException e)
        {
            transactionLogger.error("JSONException----->",e);
        }
        return commResponseVO;
    }
    public GenericResponseVO processQuery(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionLogger.error("inside processQuery()");

        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        Functions functions = new Functions();
        CommTransactionDetailsVO transDetailsVO = commRequestVO.getTransDetailsVO();
        CommAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();
        SimpleDateFormat simpleDateFormat1=new SimpleDateFormat("yyMMddHHmmss");
        SimpleDateFormat simpleDateFormat2=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String enquiryUrl = "";

        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest = gatewayAccount.isTest();
        String g_conf_site_cd = gatewayAccount.getMerchantId();//A52HN
        String g_conf_site_key = gatewayAccount.getFRAUD_FTP_PASSWORD();
        String g_conf_site_name = gatewayAccount.getFRAUD_FTP_USERNAME();
        String ip=addressDetailsVO.getIp();
        String desc=transDetailsVO.getOrderDesc();
        String transactionId=transDetailsVO.getPreviousTransactionId();
        String timestamp=transDetailsVO.getResponsetime();
        String log_dir = RB.getString("LOG_PATH");
        String log_level = "3";
        String gw_port = RB.getString("GATEWAY_PORT");//gateway port
        int g_conf_tx_mode = 0;
        if (isTest)
        {

            enquiryUrl = RB.getString("TEST_URL");
            transactionLogger.error("Inquiry TESTURL::::::::" + enquiryUrl);

        }
        else
        {
            enquiryUrl = RB.getString("LIVE_URL");
            transactionLogger.error("Inquiry LiveURL::::::::" + enquiryUrl);

        }
        if(!functions.isValueNull(transactionId) && functions.isValueNull(timestamp))
        {
            try
            {
                SimpleDateFormat simpleDateFormat3=new SimpleDateFormat("yyyyMMdd");
                SimpleDateFormat simpleDateFormat4=new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
                String date=functions.convertDateTimeToTimeZone(timestamp, "Asia/Seoul");
                transactionId=simpleDateFormat3.format(simpleDateFormat4.parse(date))+"000000";
            }
            catch (ParseException e)
            {
                transactionLogger.error("ParseException---"+trackingID+"---",e);
            }
        }
        J_PP_CLI_N c_PayPlus = new J_PP_CLI_N();
        c_PayPlus.mf_init("", enquiryUrl, gw_port, g_conf_tx_mode, log_dir);
        c_PayPlus.mf_init_set();
        int mod_data_set_no;

        String tran_cd = "00200000";
        mod_data_set_no = c_PayPlus.mf_add_set( "mod_data" );
        transactionLogger.error("tno---->"+transDetailsVO.getPreviousTransactionId());
        c_PayPlus.mf_set_us(mod_data_set_no, "req_tx", "mod");
        c_PayPlus.mf_set_us( mod_data_set_no, "tno",transactionId);
        c_PayPlus.mf_set_us( mod_data_set_no, "mod_type", "STSQ");      // Query Mode
        c_PayPlus.mf_set_us( mod_data_set_no, "pay_type", "PACA");      // PACA Credit Card
        c_PayPlus.mf_set_us( mod_data_set_no, "mod_ordr_idxx",trackingID);
        //c_PayPlus.mf_set_us( mod_data_set_no, "mod_desc",desc);
        transactionLogger.error("Inquiry request----"+trackingID+"--"+"req_tx=mod, tno="+transactionId+", mod_type=STSQ, mod_ordr_idxx="+trackingID);


        if ( tran_cd.length() > 0 )
        {
            c_PayPlus.mf_do_tx( g_conf_site_cd, g_conf_site_key, tran_cd, ip, trackingID, log_level, "1" );
        }
        else
        {
            c_PayPlus.m_res_cd  = "9562";
            c_PayPlus.m_res_msg = "연�?� 오류";
        }
        String res_code  = c_PayPlus.m_res_cd;                      // res_code
        String res_msg = c_PayPlus.m_res_msg;
        HashMap<String,String> res= KCPUtils.getResponseMap(c_PayPlus.LogRecvMsg);
        transactionLogger.error("res_code STATUS CODE===================" + res_code);
        if (res != null)
            res_msg=res.get("res_en_msg");

        transactionLogger.error("res_msg remark============"+trackingID+"=======" + res_msg);
        transactionLogger.error("status========"+trackingID+"===========" + c_PayPlus.mf_get_res("stat_ca_cd"));
        transactionLogger.error("transactionid========"+trackingID+"===========" + c_PayPlus.mf_get_res("pg_txid"));


        if("0000".equals(res_code))
        {
            transactionLogger.error("Transaction Successful");
            //commResponseVO.setTransactionType("SALE");
            commResponseVO.setStatus("success");
            //commResponseVO.setTransactionStatus(c_PayPlus.mf_get_res("stat_ca_cd"));
            commResponseVO.setTransactionStatus("success");
            commResponseVO.setAuthCode(c_PayPlus.mf_get_res("app_no"));
            commResponseVO.setDescription(res_msg);
            commResponseVO.setRemark(res_msg);
            try
            {
                commResponseVO.setBankTransactionDate(simpleDateFormat2.format(simpleDateFormat1.parse(c_PayPlus.mf_get_res("app_time"))));
            }
            catch (ParseException e)
            {
                transactionLogger.error("Date parsing exception---"+trackingID+"----",e);
            }
        }
        else
        {
            transactionLogger.error("Transaction failed");
            commResponseVO.setStatus("failed");
            commResponseVO.setErrorCode(res_code);
            commResponseVO.setTransactionStatus("failed");
            commResponseVO.setRemark(res_msg);
            commResponseVO.setDescription(res_msg);
        }
        commResponseVO.setAmount(transDetailsVO.getAmount());
        commResponseVO.setTransactionId(c_PayPlus.mf_get_res("pg_txid"));
        commResponseVO.setCurrency(transDetailsVO.getCurrency());
        commResponseVO.setMerchantId(GatewayAccountService.getGatewayAccount(accountId).getMerchantId());

        return commResponseVO;
    }

    @Override
    public String getMaxWaitDays()
    {
        return null;
    }
}
