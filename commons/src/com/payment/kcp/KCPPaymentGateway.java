package com.payment.kcp;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.*;
import com.kcp.J_PP_CLI_N;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraint.PZGenericConstraint;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;

import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.ResourceBundle;

/**
 * Created by Sagar on 1/31/2020.
 */
public class KCPPaymentGateway extends AbstractPaymentGateway
{
     public static final String GATEWAY_TYPE = "kcp";
    private static TransactionLogger transactionLogger = new TransactionLogger(KCPPaymentGateway.class.getName());
    final static ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.kcp");
    public KCPPaymentGateway(String accountId)
    {
        this.accountId= accountId;
    }

    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        Functions functions = new Functions();
        CommTransactionDetailsVO transDetailsVO = commRequestVO.getTransDetailsVO();
        CommCardDetailsVO cardDetailsVO = commRequestVO.getCardDetailsVO();
        CommAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();
        CommMerchantVO commMerchantVO = commRequestVO.getCommMerchantVO();
        CommDeviceDetailsVO commDeviceDetailsVO = commRequestVO.getCommDeviceDetailsVO();

        String saleurl = "";

         GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest = gatewayAccount.isTest();
        if (isTest)
        {
            saleurl = RB.getString("TEST_URL");
        }
        else
        {
            saleurl = RB.getString("LIVE_URL");
        }
        J_PP_CLI_N c_PayPlus = new J_PP_CLI_N();

        String g_conf_site_cd = gatewayAccount.getMerchantId();//A52HN
        String g_conf_site_key = gatewayAccount.getFRAUD_FTP_PASSWORD();
        String g_conf_site_name = gatewayAccount.getFRAUD_FTP_USERNAME();
        String log_dir = RB.getString("LOG_PATH");
        String log_level = "3";
        String gw_port = RB.getString("GATEWAY_PORT");//gateway port
        int g_conf_tx_mode = 0;
        String cust_ip = addressDetailsVO.getCardHolderIpAddress();
        String tran_cd = "00100000";
        String amount = KCPUtils.getCentAmount(transDetailsVO.getAmount());
        String orderDescription = transDetailsVO.getOrderDesc();
        String buyr_name = addressDetailsVO.getFirstname() + " " + addressDetailsVO.getLastname();
        String buyr_mail = addressDetailsVO.getEmail();
        String phone = addressDetailsVO.getPhone();
        String zip = addressDetailsVO.getZipCode();
        String rcvr_add1 = addressDetailsVO.getStreet();
        String cardNumber = cardDetailsVO.getCardNum();//4546289950108110
        String expiryMonth = cardDetailsVO.getExpMonth();
        String expiryYear = cardDetailsVO.getExpYear();
        if(functions.isValueNull(cardDetailsVO.getExpYear()))
            expiryYear=expiryYear.substring(expiryYear.length()-2,expiryYear.length());
        transactionLogger.error("expiryYear------>"+expiryYear);
        String cvv = cardDetailsVO.getcVV();

        if("JPY".equalsIgnoreCase(transDetailsVO.getCurrency()))
            amount=KCPUtils.getJPYAmount(transDetailsVO.getAmount());
        else if("KWD".equalsIgnoreCase(transDetailsVO.getCurrency()))
            amount=KCPUtils.getKWDSupportedAmount(transDetailsVO.getAmount());
        else if("KRW".equalsIgnoreCase(transDetailsVO.getCurrency()))
            amount=KCPUtils.getKRWAmount(transDetailsVO.getAmount());
        else
            amount=KCPUtils.getCentAmount(transDetailsVO.getAmount());

        int payx_data_set;
        int common_data_set;

        c_PayPlus.mf_init("", saleurl, gw_port, g_conf_tx_mode, log_dir);
        c_PayPlus.mf_init_set();

        payx_data_set = c_PayPlus.mf_add_set("payx_data");
        common_data_set = c_PayPlus.mf_add_set("common");

        c_PayPlus.mf_set_us(common_data_set, "amount", amount);
        transactionLogger.error("common_data_set currency--->"+KCPUtils.getCurrency(transDetailsVO.getCurrency()));
        c_PayPlus.mf_set_us(common_data_set, "currency", KCPUtils.getCurrency(transDetailsVO.getCurrency())); // 840-USD JPY-JPY
        c_PayPlus.mf_set_us(common_data_set, "cust_ip", cust_ip);


        c_PayPlus.mf_add_rs(payx_data_set, common_data_set);


        int ordr_data_set;
        ordr_data_set = c_PayPlus.mf_add_set("ordr_data");
        c_PayPlus.mf_set_us(ordr_data_set, "ordr_mony", amount);
        c_PayPlus.mf_set_us(ordr_data_set, "ordr_idxx", trackingID);
        c_PayPlus.mf_set_us(ordr_data_set, "good_name", orderDescription);
        c_PayPlus.mf_set_us(ordr_data_set, "buyr_name", buyr_name);
        c_PayPlus.mf_set_us(ordr_data_set, "buyr_mail", buyr_mail);

        int rcvr_data_set;
        rcvr_data_set = c_PayPlus.mf_add_set("rcvr_data");

        c_PayPlus.mf_set_us(rcvr_data_set, "rcvr_name", buyr_name);
        c_PayPlus.mf_set_us(rcvr_data_set, "rcvr_tel1", phone);
        //c_PayPlus.mf_set_us( rcvr_data_set, "rcvr_tel2", rcvr_tel2 );
        c_PayPlus.mf_set_us(rcvr_data_set, "rcvr_mail", buyr_mail);
        c_PayPlus.mf_set_us(rcvr_data_set, "rcvr_zipx", zip);
        c_PayPlus.mf_set_us(rcvr_data_set, "rcvr_add1", rcvr_add1);
        //c_PayPlus.mf_set_us( rcvr_data_set, "rcvr_add2", rcvr_add2 );

        int card_data_set;

        card_data_set = c_PayPlus.mf_add_set("card");

        c_PayPlus.mf_set_us(card_data_set, "card_tx_type", "11111000");
        c_PayPlus.mf_set_us(card_data_set, "card_mny", amount);
        c_PayPlus.mf_set_us(card_data_set, "quota", "00");
        c_PayPlus.mf_set_us(card_data_set, "card_no", cardNumber);
        c_PayPlus.mf_set_us(card_data_set, "card_expiry", expiryYear + expiryMonth);
        c_PayPlus.mf_set_us(card_data_set, "card_cvn", cvv);

        c_PayPlus.mf_add_rs(payx_data_set, card_data_set);

        transactionLogger.error("saleUrl--->"+saleurl);
        transactionLogger.error("Sale Request----"+trackingID+"--->amount="+ amount+",currency="+ KCPUtils.getCurrency(transDetailsVO.getCurrency())+", cust_ip="+ cust_ip+ "ordr_mony="+ amount+", ordr_idxx="+trackingID+", good_name="+ orderDescription+", buyr_name="+ buyr_name+", buyr_mail="+ buyr_mail+", rcvr_name="+ buyr_name+", rcvr_tel1="+ phone+", rcvr_mail="+ buyr_mail+", rcvr_zipx="+ zip+", rcvr_add1="+ rcvr_add1+ ", card_tx_type=11111000, card_mny="+ amount +", quota=00, card_no="+functions.maskingPan(cardNumber)+", card_expiry="+ functions.maskingNumber(expiryYear) + functions.maskingNumber(expiryMonth)+", card_cvn="+ functions.maskingNumber(cvv));

        transactionLogger.error("currency--->"+transDetailsVO.getCurrency());

        if (tran_cd.length() > 0)
        {
            c_PayPlus.mf_do_tx(g_conf_site_cd, g_conf_site_key, tran_cd, cust_ip, trackingID, log_level, "1");
        }
        else
        {
            c_PayPlus.m_res_cd = "9562";
            c_PayPlus.m_res_msg = "연�?� 오류(Interlocking error)";
        }



        String res_code  = c_PayPlus.m_res_cd;  //response Code
        String res_msg = c_PayPlus.m_res_msg;  //response description
        HashMap<String,String> res= KCPUtils.getResponseMap(c_PayPlus.LogRecvMsg);
        transactionLogger.error("res_code---"+trackingID+"---->"+res_code);
        transactionLogger.error("res_msg----"+trackingID+"---->"+res.get("res_en_msg"));
        if("0000".equals(res_code))
        {
            String paymentid = c_PayPlus.mf_get_res("tno");
            String app_time = c_PayPlus.mf_get_res("app_time");
            String app_no = c_PayPlus.mf_get_res("app_no");
            commResponseVO.setStatus("success");
            if(functions.isValueNull(commMerchantVO.getSupportName()) && functions.isValueNull(res.get("res_en_msg")) && (res.get("res_en_msg").contains("KCP") || res.get("res_en_msg").contains("kcp")))
            {
                String msg=res.get("res_en_msg").replaceAll("kcp",commMerchantVO.getSupportName());
                commResponseVO.setDescription(res.get(msg));
                commResponseVO.setRemark(res.get(msg));
            }else
            {
                commResponseVO.setDescription(res.get("res_en_msg"));
                commResponseVO.setRemark(res.get("res_en_msg"));
            }
            commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
            commResponseVO.setTransactionId(paymentid);
            commResponseVO.setBankTransactionDate(app_time);
            commResponseVO.setResponseHashInfo(app_no);
            commResponseVO.setErrorCode(res_code);
        }
        else
        {
            commResponseVO.setStatus("failed");
            commResponseVO.setErrorCode(res_code);
            if(functions.isValueNull(res.get("res_en_msg")) && (res.get("res_en_msg").contains("KCP") || res.get("res_en_msg").contains("kcp")))
            {
                String msg=res.get("res_en_msg").replaceAll("kcp",commMerchantVO.getSupportName());
                commResponseVO.setDescription(res.get(msg));
                commResponseVO.setRemark(res.get(msg));
            }else
            {
                commResponseVO.setDescription(res.get("res_en_msg"));
                commResponseVO.setRemark(res.get("res_en_msg"));
            }


        }
        return commResponseVO;
    }
    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        errorCodeListVO.addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_FUNCTIONALITY_NOT_ALLOWED));
        PZGenericConstraint genConstraint = new PZGenericConstraint("BillDeskPaymentGateway", "processAuthentication", null, "common", "This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::", errorCodeListVO);
        throw new PZGenericConstraintViolationException(genConstraint, "This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::", null);
    }
    public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommResponseVO commResponseVO = new CommResponseVO();
        CommTransactionDetailsVO transDetailsVO = commRequestVO.getTransDetailsVO();
        CommAddressDetailsVO addressDetailsVO=commRequestVO.getAddressDetailsVO();
        Functions functions = new Functions();
        String refundUrl="";
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest = gatewayAccount.isTest();
        String g_conf_site_cd = gatewayAccount.getMerchantId();//A52HN
        String g_conf_site_key = gatewayAccount.getFRAUD_FTP_PASSWORD();
        String g_conf_site_name = gatewayAccount.getFRAUD_FTP_USERNAME();
        String ip=addressDetailsVO.getIp();
        String desc=transDetailsVO.getOrderDesc();
        String log_dir = RB.getString("LOG_PATH");
        String log_level = "3";
        String gw_port = RB.getString("GATEWAY_PORT");//gateway port
        String res_code="";
        String res_msg="";
        String mod_type="";
        String rem_mny="";
        String mod_mny=transDetailsVO.getAmount();

        rem_mny=transDetailsVO.getRemainingAmount();
        if("JPY".equalsIgnoreCase(transDetailsVO.getCurrency()))
        {
            mod_mny = KCPUtils.getJPYAmount(transDetailsVO.getAmount());
            if(functions.isValueNull(transDetailsVO.getRemainingAmount()));
                rem_mny = KCPUtils.getJPYAmount(transDetailsVO.getRemainingAmount());
        }
        else if("KWD".equalsIgnoreCase(transDetailsVO.getCurrency()))
        {
            mod_mny = KCPUtils.getKWDSupportedAmount(transDetailsVO.getAmount());
            if(functions.isValueNull(transDetailsVO.getRemainingAmount()));
                rem_mny = KCPUtils.getKWDSupportedAmount(transDetailsVO.getRemainingAmount());
        }
        else if("KRW".equalsIgnoreCase(transDetailsVO.getCurrency()))
        {
            mod_mny = KCPUtils.getKRWAmount(transDetailsVO.getAmount());
            if(functions.isValueNull(transDetailsVO.getRemainingAmount()));
                rem_mny = KCPUtils.getKRWAmount(transDetailsVO.getRemainingAmount());
        }
        else
        {
            mod_mny = KCPUtils.getCentAmount(transDetailsVO.getAmount());
            if(functions.isValueNull(transDetailsVO.getRemainingAmount()));
                rem_mny = KCPUtils.getCentAmount(transDetailsVO.getRemainingAmount());
        }
        transactionLogger.error("res_msg--->"+res_msg);
        double transactionAmount= Double.parseDouble(transDetailsVO.getPreviousTransactionAmount());
        double refundAmount= Double.parseDouble(transDetailsVO.getAmount());
        if(refundAmount==transactionAmount)
            mod_type="STSC";
        else
            mod_type="STPC";

        J_PP_CLI_N c_PayPlus = new J_PP_CLI_N();
        int g_conf_tx_mode = 0;
        if (isTest)
        {
            refundUrl = RB.getString("TEST_URL");
        }
        else
        {
            refundUrl = RB.getString("LIVE_URL");
        }
        c_PayPlus.mf_init("", refundUrl, gw_port, g_conf_tx_mode, log_dir);
        c_PayPlus.mf_init_set();

        int     mod_data_set_no;

        String tran_cd = "00200000";
        mod_data_set_no = c_PayPlus.mf_add_set( "mod_data" );

        c_PayPlus.mf_set_us( mod_data_set_no, "req_tx", "mod");
        c_PayPlus.mf_set_us( mod_data_set_no, "tno", transDetailsVO.getPreviousTransactionId());
        c_PayPlus.mf_set_us( mod_data_set_no, "mod_type",  mod_type);      // Type of original transaction change request
        c_PayPlus.mf_set_us( mod_data_set_no, "mod_ip", ip);      // refund requester IP
        c_PayPlus.mf_set_us( mod_data_set_no, "mod_desc",desc);      // refund for change
        c_PayPlus.mf_set_us( mod_data_set_no, "mod_mny",mod_mny);
        c_PayPlus.mf_set_us( mod_data_set_no, "rem_mny",rem_mny);

        //c_PayPlus.mf_set_us( mod_data_set_no, "mod_desc",desc);      // refund for change

        transactionLogger.error("--- Refund request parameter ----"+trackingID+"--> req_tx=mod, tno="+transDetailsVO.getPreviousTransactionId()+", mod_type="+mod_type+", mod_ip="+ip+", mod_desc="+desc+", mod_mny="+mod_mny+", rem_mny="+rem_mny);


        if ( tran_cd.length() > 0 )
        {
            c_PayPlus.mf_do_tx( g_conf_site_cd, g_conf_site_key, tran_cd, ip, trackingID, log_level, "1" );
        }
        else
        {
            c_PayPlus.m_res_cd  = "9562";
            c_PayPlus.m_res_msg = "연�?� 오류";
        }
        res_code  = c_PayPlus.m_res_cd;                      // res_code
        res_msg = c_PayPlus.m_res_msg;
        HashMap<String,String> res= KCPUtils.getResponseMap(c_PayPlus.LogRecvMsg);// res_msg
        if (res != null)
            res_msg=res.get("res_en_msg");
        transactionLogger.error("refund res_code--"+trackingID+"-->"+res_code);
        transactionLogger.error("refund res_code-"+trackingID+"-->"+URLEncoder.encode(res_msg));
        if("0000".equals(res_code))
        {
            commResponseVO.setStatus("success");
            commResponseVO.setErrorCode(res_code);
            commResponseVO.setRemark("Refund Successful (" + res_msg + ")");
            commResponseVO.setDescription("Refund Successful (" + res_msg + ")");
            commResponseVO.setDescription("Successful("+res.get("res_en_msg")+")");
            commResponseVO.setRemark("Successful("+res.get("res_en_msg")+")");
        }
        else
        {
            commResponseVO.setStatus("failed");
            commResponseVO.setErrorCode(res_code);
            commResponseVO.setRemark("Refund Failed (" + res_msg + ")");
            commResponseVO.setDescription("Refund Failed (" + res_msg + ")");
            commResponseVO.setDescription("Failed("+res.get("res_en_msg") +")");
            commResponseVO.setRemark("Failed("+res.get("res_en_msg")+")");

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
        CommCardDetailsVO cardDetailsVO = commRequestVO.getCardDetailsVO();
        CommAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();
        CommMerchantVO commMerchantVO = commRequestVO.getCommMerchantVO();
        CommDeviceDetailsVO commDeviceDetailsVO = commRequestVO.getCommDeviceDetailsVO();
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
            transactionLogger.error("Transaction Successfull");
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

    /*public static void main(String[] args)
    {
        int g_conf_tx_mode = 0;
        J_PP_CLI_N c_PayPlus = new J_PP_CLI_N();
        c_PayPlus.mf_init("", RB.getString("LIVE_URL"), RB.getString("GATEWAY_PORT"), g_conf_tx_mode, RB.getString("LOG_PATH"));
        c_PayPlus.mf_init_set();
        int     mod_data_set_no;
        *//*8/11/2020 20:28*//*

        String tran_cd = "00200000";
        mod_data_set_no = c_PayPlus.mf_add_set( "mod_data" );
        c_PayPlus.mf_set_us(mod_data_set_no, "req_tx", "mod");
        c_PayPlus.mf_set_us( mod_data_set_no, "tno","20200815000000");
        c_PayPlus.mf_set_us( mod_data_set_no, "mod_type", "STSQ");      // Query Mode
        c_PayPlus.mf_set_us( mod_data_set_no, "pay_type", "PACA");      // PACA Credit Card
        c_PayPlus.mf_set_us( mod_data_set_no, "mod_ordr_idxx","6356442");      // refund for change 6285902

        //c_PayPlus.mf_set_us( mod_data_set_no, "mod_desc",desc);      // refund for change
        if ( tran_cd.length() > 0 )
        {
            c_PayPlus.mf_do_tx( "A916Z", "0GnBl1O4TfOcw6sW.KTipxx__", tran_cd, "", "6356442", "3", "1" );
        }
        else
        {
            c_PayPlus.m_res_cd  = "9562";
            c_PayPlus.m_res_msg = "연�?� 오류";
        }
        String res_code  = c_PayPlus.m_res_cd;                      // res_code
        String res_msg = c_PayPlus.m_res_msg;
        HashMap<String,String> res= KCPUtils.getResponseMap(c_PayPlus.LogRecvMsg);
        System.out.println("res_code STATUS CODE===================" + res_code);
        if (res != null)
            res_msg=res.get("res_en_msg");

        System.out.println("res_msg remark===================" + res_msg);
        System.out.println("status===================" + c_PayPlus.mf_get_res("stat_ca_cd"));
        System.out.println("transactionid===================" + c_PayPlus.mf_get_res("pg_txid"));


    }*/
}