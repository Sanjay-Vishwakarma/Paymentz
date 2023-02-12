package practice;

import org.apache.tomcat.util.codec.binary.StringUtils;

import java.nio.charset.StandardCharsets;

/**
 * Created by Admin on 6/13/2020.
 */
public class UTFconverter_eg
{
    public static void main(String[] args)
    {//[B@4cdf35a9
        String key = "qwertyuiasdfghj12345t6y";
        String saleurl = "https://api.ecommpay.com/v2/payment/card/sale";
        int payment_type = 1;
        int amount = 500;
        int id = 129;
        int amount1 = 1;
        int rate = 1000;
        boolean fire_safety_act_indicator = true;
        int price = 1000;
        int tax = 8;
        int year = 2023;
        int month = 2;
        String returnUrl="http://localhost:8081/transaction/Common3DFrontEndServlet";
        String callbackUrl="https://staging.paymentz.com/transaction/CommonBackEndServlet";

        String str =  "{" +
                " \"general\":" + "{" +
                "\"project_id\":\"2345\"," +
                "\"payment_id\":\"231\"}," +
                "\"card\":{" +
                "\"pan\":\"4242424242424242\"," +
                "\"year\":" + year + "," +
                "\"month\":" + month + "," +
                "\"card_holder\":\"abc\"}," +
                "\"customer\":{" +
                "\"id\":\"254\"," +
                "\"country\":\"IND\"," +
                "\"city\":\"Mumbai\"," +
                "\"state\":\"MH\"," +
                "\"phone\":\"08987786576\"," +
                "\"home_phone\":\"09087786576\"," +
                "\"work_phone\":\"09087786575\"," +
                "\"day_of_birth\":\"2345\"," +
                "\"birthplace\":\"30-08-2000\"," +
                "\"first_name\":\"abc\"," +
                "\"middle_name\":\"def\"," +
                "\"last_name\":\"pqr\"," +
                "\"email\":\"abc@gmail.com\"," +
                "\"ip_address\":\"192.34.43.1\"," +
                "\"district\":\"Thane\"," +
                "\"street\":\"abcdef\"," +
                "\"building\":\"13\"}," +
                "\"avs_data\":{" +
                "\"avs_post_code\":\"1\"," +
                "\"avs_street_address\":\"2\"}," +
                "\"payment\":{" +
                "\"amount\":" + amount + "," +
                "\"currency\":\"EUR 421\"}," +
                "\"positions\":{" +
                "\"price\":" + price + "," +
                "\"position_description\":\"1\"," +
                "\"tax\":" + tax + "}," +
                "\"payments\":{" +
                "\"payment_type\":" + payment_type + "," +
                "\"amount\":" + amount + "}," +
                "\"interface_type\":{" +
                "\"id\":" + id + "}," +
                "\"receipt_data\":{" +
                "\"positions\":{" +
                "\"amount\":" + amount1 + "}" +
                "  }," +
                "\"addendum\":{" +
                "\"lodging\":{" +
                "\"customer_service_toll_free_number\":\"2345\"," +
                "\"check_in_date\":\"2031\"," +
                "\"check_out_date\":\"12\"," +
                "\"folio_number\":\"2345\"}" +
                "}," +
                "\"room\":{" +
                "\"rate\":" + rate + "," +
                "\"fire_safety_act_indicator\":" + fire_safety_act_indicator + "}," +
                "\"acs_return_url\":{"+
                "\"return_url\":\""+returnUrl+"\"," +
                "\"3ds_notification_url\":\""+callbackUrl+"\"" +
                " }}";

        byte[] bytevar = StringUtils.getBytesUtf8(str);
     byte[] b=str.getBytes(StandardCharsets.UTF_8);
        System.out.println(b.toString());
    }
}
