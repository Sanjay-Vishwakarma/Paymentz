
package practice;


public class practice2
{
    public static void main(String[] args)
    {

        String data = "20200522PI00006/500@100660";
        String transArr[] = {};

            String date = data.substring(0, 8);
            //  String paymentid = data.substring(8, data.indexOf("/")); changes
            String paymentid = data.substring(0, data.indexOf("/"));
            //String slash_to_attherate=var.split(var.contains("/"),var.contains("@"));

          /*  System.out.println("date->" + date);
            System.out.println("paymentid->" + paymentid);*/
            //String string_before_slash = "";
            String response_amount = "";
           // String responsetrackingId = "";
            //  String date="";
            String transactionId = "";
            if (data.contains("/") && data.contains("@"))
            {
                int indx_slash = data.indexOf("/");
                int indx_at = data.indexOf("@");
             //   string_before_slash = transData.substring(0, indx_slash);
                response_amount = String.format("%.2f", Double.parseDouble(data.substring(indx_slash + 1, indx_at)));
               transactionId = data.split("@")[1]+"-"+paymentid;
               // date = string_before_slash.substring(0, 8);
                //transactionId = data.substring(data.indexOf("@")+1, data.length());

            }

       //     System.out.println("date->" + date);
            System.out.println("paymentid->" + paymentid);
            System.out.println("response_amount->"+response_amount);
            System.out.println("transactionId->"+transactionId);
        }
    }