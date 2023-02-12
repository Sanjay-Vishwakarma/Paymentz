package practice;

import com.payment.zotapay.ZotaPayUtils;

/**
 * Created by Admin on 2/26/2021.
 */
public class zotapay_main
{

    public static void main(String[] args)
    {
        sale();
    }
        public static void sale()
        {
        try
        {

            String test_url = "https://sandbox.zotapay.com/paynet/api/v2/sale/4043";
            String merchant_control = ZotaPayUtils.getControl("4043" + "54568" + "1000" + "rihen.dedhia@pz.com" + "C6B5F75C-8A47-415D-BC58-E606B9CE1838");
            System.out.println("merchant control-----" + merchant_control);
            String request =
                    "client_orderid=54568\n" +
                            "&order_desc=Test Order Description\n" +
                            "&first_name=Rihen\n" +
                            "&last_name=Dedhia\n" +
                            "&address1=Malad\n" +
                            "&city=Mumbai\n" +
                            "&state=MH\n" +
                            "&zip_code=400065\n" +
                            "&country=IN\n" +
                            "&phone=%2B12063582043\n" +
                            "&cell_phone=%2B19023384543\n" +
                            "&amount=10.00\n" +
                            "&email=rihen.dedhia@pz.com\n" +
                            "&currency=USD\n" +
                            "&ipaddress=115.96.19.10\n" +
                            "&credit_card_number=4222222489425\n" +
                            "&card_printed_name=RIHEN DEDHIA\n" +
                            "&expire_month=12\n" +
                            "&expire_year=2099\n" +
                            "&cvv2=321\n" +
                            //"&redirect_url=http://localhost:8081/transaction/PVFrontEndServlet\n" +
                            "&server_callback_url=https://staging.pz.com/transaction/ZotapayFrontEndServlet\n" +
                            "&control=" + merchant_control + "";

            System.out.println("Request----" + request);
            String response = ZotaPayUtils.doPostHTTPSURLConnectionClient(test_url, request);
            System.out.println("Response-----" + response);


/*            String status_test_url = "https://sandbox.zotapay.com/paynet/api/v2/status/4190";
            String status_control = ZotaPayUtils.getControl("70Trades"+"56551"+"961379"+"C6B5F75C-8A47-415D-BC58-E606B9CE1838");
            String status_request = "login=70Trades\n" +
                    "&client_orderid=56551\n" +
                    "&orderid=961379\n" +
                    "&by-request-sn=00000000-0000-0000-0000-0000016ca835\n" +
                    "&control="+status_control+"";
            System.out.println("status_request-----"+status_request);
            String status_response=ZotaPayUtils.doPostHTTPSURLConnectionClient(status_test_url,status_request);
            System.out.println("status-----"+status_response);*/

        }
        catch (Exception e)
        {
            System.out.println("error----------------" + e);
        }}
    }