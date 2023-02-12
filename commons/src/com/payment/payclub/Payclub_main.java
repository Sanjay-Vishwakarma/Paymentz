package com.payment.payclub;

import com.payment.exceptionHandler.PZTechnicalViolationException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Admin on 12/10/2020.
 */
public class Payclub_main
{
    public static void main(String[] args)
    {
 // refund();
//sale3D();
//  saleNon3D();
 signmsg();
    }
    public static void signmsg()
    {
        try
        {

            String p_mid = "81094";
            String p_account_num = "40001800";
            String signkey = "0fb80L6P0j80v22";
            String p_order_num = "148290";
            String p_amount = "10.00";
            String p_currency = "USD";
            String p_signmsg = PayClubUtils.SHA256forSales(p_mid, p_account_num, p_order_num, p_currency, p_amount, signkey);
            System.out.println("p_order_num--"+p_order_num);
            System.out.println("p_signmsg---"+p_signmsg);
        }
        catch (PZTechnicalViolationException e)
        {
            e.printStackTrace();
        }
    }
  public static void sale3D()
  {
      try
      {
          PayClubUtils payClubUtils = new PayClubUtils();
          String p_mid ="81094";
          String p_payment_type = "MC";
          String p_transaction_type = "3DSALE";
          String p_account_num = "40001800";
          String signkey = "0fb80L6P0j80v22";
          String p_order_num = "182499";
          String p_amount = "11.00";
          String p_currency = "USD";
          String p_card_num = "5136333333333335";
          String p_card_expmonth = "03";
          String p_card_expyear = "2030";
          String p_card_csc = "737";
          String p_user_ipaddress = "128.0.0.1";
          String p_card_issuingbank = "Bankname";
          String p_signmsg = payClubUtils.SHA256forSales(p_mid, p_account_num, p_order_num, p_currency, p_amount, signkey);
          String p_firstname = "Toms";
          String p_lastname = "Ding";
          String p_user_email = "toms.ding@gmail.com";
          String p_user_phone = "9162397406";
          String p_bill_country = "UK";
          String p_bill_state = "Londons";
          String p_bill_city = "Londons";
          String p_bill_address = "A360 Londons";
          String p_bill_zip = "302012";
          String p_ship_firstname = "aDeepak";
          String p_ship_lastname = "Siangh";
          String p_ship_country = "Unitaed Kingdom";
          String p_ship_state = "London";
          String p_ship_city = "London";
          String p_ship_address = "A360 Londons";
          String p_ship_zip = "302012";
          String p_product_name = "84433";
          String p_product_num = p_order_num;
          String p_product_desc = "Test";
          String p_remark = p_order_num;
          String p_return_url = "http://localhost:8081/transaction/PayClubFrontEndServlet";
          String p_trans_url = "https://staging.paymentz.com/transaction/CommonBackEndServlet";
      //    String url = "https://test.payclub.com/payment.jsp";
          String url = "https://int1.payclub.com/payment.jsp";
          String p_client_id="csid";

          //String signMsg = payClubUtils.SHA256forSales("81094", "40000148", order, "USD", "10.00", "vf6nljz0f0x08N4");
         StringBuffer saleRequest = new StringBuffer();
          saleRequest.append("p_mid=" + p_mid +
                  "&p_payment_type=" + p_payment_type +
                  "&p_transaction_type=" + p_transaction_type +
                  "&p_account_num=" + p_account_num +
                  "&signkey=" + signkey +
                  "&p_order_num=" + p_order_num +
                  "&p_currency=" + p_currency +
                  "&p_amount=" + p_amount +
                  "&p_card_num=" + p_card_num +
                  "&p_card_expmonth=" + p_card_expmonth +
                  "&p_card_expyear=" + p_card_expyear +
                  "&p_card_csc=" + p_card_csc +
                  "&p_user_ipaddress=" + p_user_ipaddress +
                  "&p_card_issuingbank=" + p_card_issuingbank +
                  "&p_signmsg=" + p_signmsg +
                  "&p_firstname=" + p_firstname +
                  "&p_lastname=" + p_lastname +
                  "&p_user_email=" + p_user_email +
                  "&p_user_phone=" + p_user_phone +
                  "&p_bill_country=" + p_bill_country +
                  "&p_bill_state=" + p_bill_state +
                  "&p_bill_city=" + p_bill_city +
                  "&p_bill_address=" + p_bill_address +
                  "&p_bill_zip=" + p_bill_zip +
                  "&p_ship_firstname=" + p_ship_firstname +
                  "&p_ship_lastname=" + p_ship_lastname +
                  "&p_ship_country=" + p_ship_country +
                  "&p_ship_state=" + p_ship_state +
                  "&p_ship_city=" + p_ship_city +
                  "&p_ship_address=" + p_ship_address +
                  "&p_ship_zip=" + p_ship_zip +
                  "&p_product_name=" + p_product_name +
                  "&p_product_num=" + p_product_num +
                  "&p_product_desc=" + p_product_desc +
                  "&p_remark=" + p_remark +
                  "&p_return_url=" + p_return_url +
                  "&p_trans_url=" + p_trans_url +
                  "&p_client_id="+p_client_id
                  );
          System.out.println("3D sale Request ---" +saleRequest );

          String saleResponse = PayClubUtils.sendPOST(url, saleRequest.toString());
          System.out.println("3D sale Response ---" + saleResponse);

       /*   StringBuffer html=new StringBuffer("");
          html.append("<form name=\"creditcard_checkout\" method=\"POST\" action=\"" + url + "\">");
          html.append("<input type=\"hidden\" name=\"p_payment_type\"  value=\"" + p_payment_type + "\">");
          html.append("<input type=\"hidden\" name=\"p_transaction_type\" value=\"" + p_transaction_type + "\">");
          html.append("<input type=\"hidden\" name=\"p_mid\" value=\"" + p_mid + "\">");
          html.append("<input type=\"hidden\" name=\"p_account_num\" value=\"" + p_account_num + "\">");
          html.append("<input type=\"hidden\" name=\"signkey\" value=\"" + signkey + "\">");
          html.append("<input type=\"hidden\" name=\"p_order_num\" value=\"" + p_order_num + "\">");
          html.append("<input type=\"hidden\" name=\"p_currency\" value=\"" + p_currency + "\">");
          html.append("<input type=\"hidden\" name=\"p_amount\" value=\"" + p_amount + "\">");
          html.append("<input type=\"hidden\" name=\"p_card_num\" value=\"" + p_card_num + "\">");
          html.append("<input type=\"hidden\" name=\"p_card_expmonth\" value=\"" + p_card_expmonth + "\">");
          html.append("<input type=\"hidden\" name=\"p_card_expyear\" value=\"" + p_card_expyear + "\">");
          html.append("<input type=\"hidden\" name=\"p_card_csc\" value=\"" + p_card_csc + "\">");
          html.append("<input type=\"hidden\" name=\"p_user_ipaddress\" value=\"" + p_user_ipaddress + "\">");
          html.append("<input type=\"hidden\" name=\"p_card_issuingbank\" value=\"" +  p_card_issuingbank+ "\">");
          html.append("<input type=\"hidden\" name=\"p_signmsg\" value=\"" + p_signmsg + "\">");
          html.append("<input type=\"hidden\" name=\"p_firstname\" value=\"" + p_firstname + "\">");
          html.append("<input type=\"hidden\" name=\"p_lastname\" value=\"" + p_lastname + "\">");
          html.append("<input type=\"hidden\" name=\"p_user_email\" value=\"" + p_user_email + "\">");
          html.append("<input type=\"hidden\" name=\"p_user_phone\" value=\"" + p_user_phone + "\">");
          html.append("<input type=\"hidden\" name=\"p_bill_country\" value=\"" + p_bill_country + "\">");
          html.append("<input type=\"hidden\" name=\"p_bill_state\" value=\"" + p_bill_state + "\">");
          html.append("<input type=\"hidden\" name=\"p_bill_city\" value=\"" + p_bill_city + "\">");
          html.append("<input type=\"hidden\" name=\"p_bill_address\" value=\"" + p_bill_address + "\">");
          html.append("<input type=\"hidden\" name=\"p_bill_zip\" value=\"" + p_bill_zip + "\">");
          html.append("<input type=\"hidden\" name=\"p_ship_firstname\" value=\"" + p_ship_firstname + "\">");
          html.append("<input type=\"hidden\" name=\"p_ship_lastname\" value=\"" + p_ship_lastname + "\">");
          html.append("<input type=\"hidden\" name=\"p_ship_country\" value=\"" + p_ship_country + "\">");
          html.append("<input type=\"hidden\" name=\"p_ship_state\" value=\"" + p_ship_state + "\">");
          html.append("<input type=\"hidden\" name=\"p_ship_city\" value=\"" + p_ship_city + "\">");
          html.append("<input type=\"hidden\" name=\"p_ship_address\" value=\"" + p_ship_address + "\">");
          html.append("<input type=\"hidden\" name=\"p_ship_zip\" value=\"" + p_ship_zip + "\">");
          html.append("<input type=\"hidden\" name=\"p_product_name\" value=\"" + p_product_name + "\">");
          html.append("<input type=\"hidden\" name=\"p_product_num\" value=\"" + p_product_num + "\">");
          html.append("<input type=\"hidden\" name=\"p_product_desc\" value=\"" + p_product_desc + "\">");
          html.append("<input type=\"hidden\" name=\"p_return_url\" value=\"" + p_return_url + "\">");
          html.append("<input type=\"hidden\" name=\"p_remark\" value=\"" + p_remark + "\">");
          html.append("<input type=\"hidden\" name=\"p_trans_url\" value=\"" + p_trans_url + "\">");
          html.append("<input type=\"hidden\" name=\"p_client_id\" value=\""+p_client_id+"\">");
*/




      }
      catch (Exception e)
      {
          e.printStackTrace();
      }
  }
    public static void saleNon3D()
    {try
    {
        PayClubUtils payClubUtils = new PayClubUtils();
        String p_mid = "60022";
        String account_num = "60022003";
        String p_amount = "10.00";
        String p_currency = "USD";
        String p_card_num = "5105105105105100";
        String p_card_expmonth = "12";
        String p_card_expyear = "2028";
        String p_card_csc = "789";
        String p_user_ipaddress = "127.0.0.1";
        String p_firstname = "Tom";
        String p_lastname = "Ding";
        String p_user_email = "tom.ding@gmail.com";
        String p_user_phone = "9162597406";
        String p_bill_country = "UK";
        String p_bill_state = "London";
        String p_bill_city = "London";
        String p_bill_address = "A360 London";
        String p_bill_zip = "302011";
        String p_ship_firstname = "Deepak";
        String p_ship_lastname = "Singh";
        String p_ship_country = "United Kingdom";
        String p_ship_state = "London";
        String p_ship_city = "London";
        String p_ship_address = "A360 London";
        String p_ship_zip = "302011";
        String p_product_name = "84933";

        String p_return_url = "http://localhost:8081/transaction/Common3DFrontEndServlet?trackingId=";
        String p_trans_url = "https://staging.paymentz.com/transaction/CommonBackEndServlet";

        String url = "https://test.payclub.com/payment.jsp";
      //  String url = "https://int1.payclub.com/payment_test.jsp";
        //String url = "https://int1.payclub.com/payment.jsp";


        String transactionType ="SALE";
        String issuingBank="issuingBank";
        String order="T2019040314583660520886";
        String signKey="v8Hz44tn04j08Dv";
        String signmsg = payClubUtils.SHA256forSales(p_mid, account_num, order, p_currency, p_amount, signKey);
        String saleRequest = "p_mid=" + p_mid +
                "&p_account_num=" + account_num +
                "&p_transaction_type=" + transactionType +
                "&p_order_num=" + order +
                "&p_currency=" + p_currency +
                "&p_amount=" + p_amount +
                "&p_card_num=" + p_card_num +      // card detail
                "&p_card_expmonth=" + p_card_expmonth +
                "&p_card_expyear=" + p_card_expyear +
                "&p_card_csc=" + p_card_csc +
                "&p_card_issuingbank=" + issuingBank +
                "&p_firstname=" + p_firstname +
                "&p_lastname=" + p_lastname +
                "&p_user_email=" + p_user_email +
                "&p_user_phone=" + p_user_phone +
                "&p_user_ipaddress=" + p_user_ipaddress +
                "&p_trans_url=" + "" +
                "&p_return_url=" + "" +
                "&p_bill_country=" + p_bill_country +     // billing detail
                "&p_bill_state=" + p_bill_state +
                "&p_bill_city=" + p_bill_city +
                "&p_bill_address=" + p_bill_address +
                "&p_bill_zip=" + p_bill_zip +
                "&p_ship_firstname=" + p_ship_firstname +     // shipping detail
                "&p_ship_lastname=" + p_ship_lastname +
                "&p_ship_country=" + p_ship_country +
                "&p_ship_state=" + p_ship_state +
                "&p_ship_city=" + p_ship_city +
                "&p_ship_address=" + p_ship_address +
                "&p_ship_zip=" + p_ship_zip +
                "&p_product_name=" + "84933" +     // product detail
                "&p_product_num=" + "84933" +
                "&p_product_desc=" + "Test" +
                "&p_signmsg=" + signmsg;


        System.out.println("Sale Request ---" + saleRequest);

        String saleResponse = PayClubUtils.sendPOST(url, saleRequest);
        System.out.println("Sale Response  ---- " + saleResponse);

    }catch (Exception e)
    {
        e.printStackTrace();
    }

    }
    public static void refund()
    {
        PayClubUtils payClubUtils = new PayClubUtils();
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        String timestamp = String.valueOf(dateFormat.format(date));

        String p_mid = "60022";
        String p_account_num = "60022003";
        String p_transaction_type = "REFUND";
        String p_refund_type = "1";
        String p_trans_num = "20201222154724653318381";
        String p_refund_amount = "10.00";
        String p_refund_reason = "Refund reson";
        String p_order_currency = "USD";
        String p_order_amount = "11.00";
        String p_remark = timestamp;
        String p_signmsg = "";
        String signkey = "v8Hz44tn04j08Dv";
        String requestParameter = "";
//String refundRequestURL = "https://ac1.payclub.com /order_list.jsp";
        String refundRequestURL = "https://test.payclub.com/order_list.jsp";
        try
        {
            p_signmsg = payClubUtils.SHA256forRefund(p_mid,p_account_num,p_trans_num,p_refund_type,signkey);

            requestParameter = "p_mid="+p_mid
                    + "&p_account_num="+p_account_num
                    + "&p_transaction_type="+p_transaction_type
                    + "&p_refund_type="+p_refund_type
                    + "&p_trans_num="+p_trans_num
                    + "&p_refund_amount="+p_refund_amount
                    + "&p_refund_reason="+p_refund_reason
                    + "&p_order_currency="+p_order_currency
                    + "&p_order_amount="+p_order_amount
                    + "&p_remark="+p_remark
                    + "&p_signmsg="+p_signmsg;

            System.out.println("refund Sale Request ---" + requestParameter);

            String saleResponse = PayClubUtils.sendPOST(refundRequestURL, requestParameter);
            System.out.println("refund Sale Response ---- " + saleResponse);

        }catch (Exception e){
            e.printStackTrace();
        }

    }
}