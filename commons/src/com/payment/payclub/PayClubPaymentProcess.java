package com.payment.payclub;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.directi.pg.core.valueObjects.GenericCardDetailsVO;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.manager.vo.AsyncParameterVO;
import com.manager.vo.DirectKitResponseVO;
import com.manager.vo.MerchantDetailsVO;
import com.payment.common.core.*;
import com.payment.validators.vo.CommonValidatorVO;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

/**
 * Created by Admin on 12/10/2020.
 */
public class PayClubPaymentProcess extends CommonPaymentProcess
{
    private static TransactionLogger transactionLogger = new TransactionLogger(PayClubPaymentProcess.class.getName());

//    public String get3DConfirmationForm(String trackingId, String ctoken, Comm3DResponseVO response3D)
//    {
//        Functions functions=new Functions();
//      //  transactionLogger.error("3d page displayed....." + response3D.getUrlFor3DRedirect());
//
//        return response3D.getUrlFor3DRedirect();
//    }


    public String get3DConfirmationForm(String trackingId, String ctoken, Comm3DResponseVO response3D)
    {
        Functions functions=new Functions();
        transactionLogger.error("3d page displayed....." + response3D.getUrlFor3DRedirect());
        String target = "target=\"\"";

        StringBuffer form=new StringBuffer("<form name=\"launch3D\" method=\"post\" action=\""+response3D.getUrlFor3DRedirect()+"\" ");
        form.append("</form>"+
                "<script language=\"javascript\"> document.launch3D.submit(); </script>");
        transactionLogger.error("form---->"+form);
        return form.toString();
    }

    public String get3DConfirmationFormVT(String trackingId, String ctoken, Comm3DResponseVO response3D)
    {
        transactionLogger.error("3d page displayed....." +trackingId+ "----" + response3D.getUrlFor3DRedirect());
        String target = "target=_blank";
        String form="<form name=\"launch3D\" method=\"POST\" action=\""+response3D.getUrlFor3DRedirect()+"\""+target+">"+
                "<input type=\"hidden\" name=\"PaReq\" value=\""+response3D.getPaReq()+"\">"+
                "<input type=\"hidden\" name=\"TermUrl\" value=\""+response3D.getTerURL()+"\">"+
                "<input type=\"hidden\" name=\"MD\" value=\""+response3D.getMd()+"\">"+
                "</form>"+
                "<script language=\"javascript\"> document.launch3D.submit(); </script>";
        transactionLogger.error("form....." +trackingId+ "----" + form);
        transactionLogger.error("launch3D....."+ response3D.getUrlFor3DRedirect());
        transactionLogger.error("PaReq....." + response3D.getPaReq());
        transactionLogger.error("TermUrl....." + response3D.getTerURL());
        transactionLogger.error("MD....."  + response3D.getMd());

        return form;
    }

    public void set3DResponseVO(DirectKitResponseVO directKitResponseVO, Comm3DResponseVO response3D)
    {
        AsyncParameterVO asyncParameterVO = null;
        transactionLogger.debug("inside payclub payment process---" + response3D.getUrlFor3DRedirect());

        directKitResponseVO.setBankRedirectionUrl(response3D.getUrlFor3DRedirect());

    }

  /*  public String get3DConfirmationForm(String trackingId, String ctoken, Comm3DResponseVO response3D)
    {
        transactionLogger.error("3d page displayed....." + response3D.getUrlFor3DRedirect());

        String form="<form name=\"launch3D\" method=\"POST\" action=\""+response3D.getUrlFor3DRedirect()+"\">"+
                "<input type=\"hidden\" name=\"PaReq\" value=\""+response3D.getPaReq()+"\">"+
                "<input type=\"hidden\" name=\"TermUrl\" value=\""+response3D.getTerURL()+"\">"+
                "<input type=\"hidden\" name=\"MD\" value=\""+response3D.getMd()+"\">"+
                "</form>"+
                "<script language=\"javascript\"> document.launch3D.submit(); </script>";
        return form;
    }*/



  /*  public String get3DConfirmationForm(String trackingId, String ctoken, Comm3DResponseVO response3D)
    {
        Functions functions = new Functions();
        transactionLogger.error("3d page displayed....." + response3D.getUrlFor3DRedirect());

        StringBuffer html = new StringBuffer(response3D.getUrlFor3DRedirect());

        transactionLogger.debug("form----"+html.toString());
        return html.toString();
    }*/

   /* public String get3DConfirmationForm(CommonValidatorVO commonValidatorVO, Comm3DResponseVO response3D)
    {

        transactionLogger.error("inside  PayclubPaymentProcess Form---");
        ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.payclub");

        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(commonValidatorVO.getMerchantDetailsVO().getAccountId());
        PayClubUtils payClubUtils= new PayClubUtils();
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        Functions functions = new Functions();
        StringBuffer html = new StringBuffer("");
        CommTransactionDetailsVO commTransactionDetailsVO = new CommTransactionDetailsVO();
        GenericCardDetailsVO genericCardDetailsVO = commonValidatorVO.getCardDetailsVO();
        MerchantDetailsVO merchantDetailsVO = commonValidatorVO.getMerchantDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = commonValidatorVO.getAddressDetailsVO();
        String is3dSupported = gatewayAccount.get_3DSupportAccount();
    try
   {
    String merchantId = gatewayAccount.getMerchantId();             // provided by payclub
    String accountNumber = gatewayAccount.getFRAUD_FTP_USERNAME();  // provided by payclub
    String signKey = gatewayAccount.getFRAUD_FTP_PASSWORD();         // provided by payclub

    String p_transaction_type = "3DSALE";
    String orderId = commonValidatorVO.getTrackingid();
    String amount = commonValidatorVO.getTransDetailsVO().getAmount();
    String cardNumber = genericCardDetailsVO.getCardNum();
    String expiryMonth = genericCardDetailsVO.getExpMonth();
    String expiryYear = genericCardDetailsVO.getExpYear();
    String cvv = genericCardDetailsVO.getcVV();
    String issuingBank = "issuingBank";
    String firstname = addressDetailsVO.getFirstname();
    String lastname = addressDetailsVO.getLastname();
    String email = addressDetailsVO.getEmail();
    String phone = addressDetailsVO.getPhone();
    String ip = "";
    boolean isTest = gatewayAccount.isTest();
    if (functions.isValueNull(addressDetailsVO.getCardHolderIpAddress()))
        ip = addressDetailsVO.getCardHolderIpAddress();
    else
        ip = addressDetailsVO.getIp();

    String transUrl = "";
    String returnUrl = "";
    String country = addressDetailsVO.getCountry();
    String state = addressDetailsVO.getState();
    String city = addressDetailsVO.getCity();
    String address = addressDetailsVO.getStreet();
    String zip = addressDetailsVO.getZipCode();
    String dispalyName = "";
    if (functions.isValueNull(gatewayAccount.getDisplayName()))
    {
        dispalyName = gatewayAccount.getDisplayName();
    }

    String productName = orderId;
    String productDesc = commonValidatorVO.getTransDetailsVO().getOrderDesc();
    String saleResponse = "";
    String currency = "";
    String tmpl_amount = "";
    String tmpl_currency = "";
    if (functions.isValueNull(commonValidatorVO.getTransDetailsVO().getCurrency()))
    {
        currency = commonValidatorVO.getTransDetailsVO().getCurrency();
    }
    if (functions.isValueNull(addressDetailsVO.getTmpl_amount()))
    {
        tmpl_amount = addressDetailsVO.getTmpl_amount();
    }
    if (functions.isValueNull(addressDetailsVO.getTmpl_currency()))
    {
        tmpl_currency = addressDetailsVO.getTmpl_currency();
    }
    //Parameters for 3Dsale
    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    Date date = new Date();
    String timestamp = String.valueOf(dateFormat.format(date));
    String p_mid = gatewayAccount.getMerchantId();
    String p_payment_type = "Credit Card";
    String p_order_num = timestamp;
    String p_remark = "test remartk";
    String p_return_url = "";
    String p_trans_url = RB.getString("NOTIFY_URL");
    String p_signmsg = PayClubUtils.SHA256forSales(merchantId, accountNumber, p_order_num, currency, amount, signKey);
    html.append("<html> <head> </head> <body onLoad='demo_submit();'>");
    html.append("<html>");
    html.append("<head></head>");
    html.append("<body>");
    html.append("<form name=\"creditcard_checkout\" method=\"POST\" action=\"" + response3D.getUrlFor3DRedirect() + "\">");
    html.append("<input type=\"hidden\" name=\"p_payment_type\"  value=\"" + p_payment_type + "\">");
    html.append("<input type=\"hidden\" name=\"p_transaction_type\" value=\"" + p_transaction_type + "\">");
    html.append("<input type=\"hidden\" name=\"p_mid\" value=\"" + p_mid + "\">");
    html.append("<input type=\"hidden\" name=\"p_account_num\" value=\"" + accountNumber + "\">");
    html.append("<input type=\"hidden\" name=\"signkey\" value=\"" + signKey + "\">");
    html.append("<input type=\"hidden\" name=\"p_order_num\" value=\"" + p_order_num + "\">");
    html.append("<input type=\"hidden\" name=\"p_currency\" value=\"" + currency + "\">");
    html.append("<input type=\"hidden\" name=\"p_amount\" value=\"" + amount + "\">");
    html.append("<input type=\"hidden\" name=\"p_card_num\" value=\"" + cardNumber + "\">");
    html.append("<input type=\"hidden\" name=\"p_card_expmonth\" value=\"" + expiryMonth + "\">");
    html.append("<input type=\"hidden\" name=\"p_card_expyear\" value=\"" + expiryYear + "\">");
    html.append("<input type=\"hidden\" name=\"p_card_csc\" value=\"" + cvv + "\">");
    html.append("<input type=\"hidden\" name=\"p_user_ipaddress\" value=\"" + ip + "\">");
    html.append("<input type=\"hidden\" name=\"p_card_issuingbank\" value=\"" + issuingBank + "\">");
    html.append("<input type=\"hidden\" name=\"p_signmsg\" value=\"" + p_signmsg + "\">");
    html.append("<input type=\"hidden\" name=\"p_firstname\" value=\"" + firstname + "\">");
    html.append("<input type=\"hidden\" name=\"p_lastname\" value=\"" + lastname + "\">");
    html.append("<input type=\"hidden\" name=\"p_user_email\" value=\"" + email + "\">");
    html.append("<input type=\"hidden\" name=\"p_user_phone\" value=\"" + phone + "\">");
    html.append("<input type=\"hidden\" name=\"p_bill_country\" value=\"" + country + "\">");
    html.append("<input type=\"hidden\" name=\"p_bill_state\" value=\"" + state + "\">");
    html.append("<input type=\"hidden\" name=\"p_bill_city\" value=\"" + city + "\">");
    html.append("<input type=\"hidden\" name=\"p_bill_address\" value=\"" + address + "\">");
    html.append("<input type=\"hidden\" name=\"p_bill_zip\" value=\"" + zip + "\">");
    html.append("<input type=\"hidden\" name=\"p_ship_firstname\" value=\"" + firstname + "\">");
    html.append("<input type=\"hidden\" name=\"p_ship_lastname\" value=\"" + lastname + "\">");
    html.append("<input type=\"hidden\" name=\"p_ship_country\" value=\"" + country + "\">");
    html.append("<input type=\"hidden\" name=\"p_ship_state\" value=\"" + state + "\">");
    html.append("<input type=\"hidden\" name=\"p_ship_city\" value=\"" + city + "\">");
    html.append("<input type=\"hidden\" name=\"p_ship_address\" value=\"" + address + "\">");
    html.append("<input type=\"hidden\" name=\"p_ship_zip\" value=\"" + zip + "\">");
    html.append("<input type=\"hidden\" name=\"p_product_name\" value=\"" + productName + "\">");
    html.append("<input type=\"hidden\" name=\"p_product_num\" value=\"" + productName + "\">");
    html.append("<input type=\"hidden\" name=\"p_product_desc\" value=\"" + productDesc + "\">");
    html.append("<input type=\"hidden\" name=\"p_return_url\" value=\"" + p_return_url + "\">");
    html.append("<input type=\"hidden\" name=\"p_remark\" value=\"" + p_remark + "\">");
    html.append("<input type=\"hidden\" name=\"p_trans_url\" value=\"" + p_trans_url + "\">");
    html.append("<input type=\"hidden\" name=\"p_client_id\" value=\"csid\">");

    html.append("<script type=\"text/javascript\" src=\"http://cm.js.dl.saferconnectdirect.com/csid.js\" charset=\"UTF-8\"></script>\n" +
            "<script type  = \"text/javascript\">\n" +
            "function demo_submit(){\n" +
            "document.creditcard_checkout.action = '"+RB.getString("TEST_3DSALE_URL")+"';\n" +
            "document.creditcard_checkout.submit();\n" +
            "document.creditcard_checkout.action='wade';\n" +
            "}\n" +
            "</script>"+
            "</body>"+
       "</html>"
    );
    transactionLogger.error("PayclubPaymentProcess Form---" + html.toString());

   }
    catch (Exception e)
   {
    transactionLogger.error("Exception inside get3DConfirmationForm"+e);
   }
        return html.toString();

    }
*/


 /*   public void set3DResponseVO(DirectKitResponseVO directKitResponseVO, Comm3DResponseVO response3D)
    {
        AsyncParameterVO asyncParameterVO = null;
        transactionLogger.debug("inside Payclub payment process---"+response3D.getUrlFor3DRedirect());
        directKitResponseVO.setBankRedirectionUrl(response3D.getUrlFor3DRedirect());

        asyncParameterVO = new AsyncParameterVO();
        asyncParameterVO.setName("launch3D");
        asyncParameterVO.setValue(response3D.getUrlFor3DRedirect());
        directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

        asyncParameterVO = new AsyncParameterVO();
        asyncParameterVO.setName("PaReq");
        asyncParameterVO.setValue(response3D.getPaReq());
        directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

        asyncParameterVO = new AsyncParameterVO();
        asyncParameterVO.setName("TermUrl");
        asyncParameterVO.setValue(response3D.getTerURL());
        directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

        asyncParameterVO = new AsyncParameterVO();
        asyncParameterVO.setName("MD");
        asyncParameterVO.setValue(response3D.getMd());
        directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);
    }
*/

}
