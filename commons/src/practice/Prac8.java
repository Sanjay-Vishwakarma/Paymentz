package practice;

import com.payment.PZTransactionStatus;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Scanner;
import java.util.Set;

/**
 * Created by Admin on 6/20/2020.
 */
public class Prac8
{
    public static void main(String[] args)
    {


        //Eg.of substring for backend code use
       /* String var="IND1395@03041994/sagar";
        String country=var.substring(0,3);
        System.out.println("Country->"+country);
        String password=var.substring(3,var.indexOf("@"));
        System.out.println(("Password->"+password));
        String birthdate=var.substring(var.indexOf("@")+1,var.indexOf("/"));
        System.out.println("Birthdate->"+birthdate);
        String name=var.substring(var.indexOf("/")+1,var.length());
        System.out.println("Name->"+name);
*/
        //enum example
        /*String status="success";
        if(PZTransactionStatus.AUTH_SUCCESS.toString().equalsIgnoreCase(status))
        {
            System.out.println("status is "+PZTransactionStatus.AUTH_SUCCESS.toString());
        }
        else if(PZTransactionStatus.AUTH_FAILED.toString().equalsIgnoreCase(status))
        {
            System.out.println("status is "+PZTransactionStatus.AUTH_FAILED.toString());
        }*/
      /* StringBuilder str=new StringBuilder("sagar ");
        String str1="sonar";
        System.out.println( str.append(str1));
      */
        //do while loop with break
/*        int i;
        Scanner sc = new Scanner(System.in);
        i=sc.nextInt();
        do
        {
            System.out.println(i);
            i++;
            if (i == 9)
            {
                System.out.println("break");
                break;
            }
            while(i<=10)*/




        //typecast int to double
        /*int num =90;
        double myint =num;
        System.out.println(myint);*/
//typecast int to string
       /* int num =9;
        String var = String.valueOf(num);
        System.out.println(var);
*/
        //join example
/*
        String date="03041994";
        String datenew = String.join("-","03","04","1994");
        System.out.println("Date->"+datenew);
*/

        //code to read json and parameters in it
/*
        try
        {

            String id="";
            String response = "{\n" +
                    "\t\"project_id\": 15561,\n" +
                    "\t\"payment\": {\n" +
                    "\t\t\"id\": \"173917\",\n" +
                    "\t\t\"type\": \"purchase\",\n" +
                    "\t\t\"status\": \"awaiting 3ds result\",\n" +
                    "\t\t\"date\": \"2020-06-20T09:36:40+0000\",\n" +
                    "\t\t\"method\": \"card\",\n" +
                    "\t\t\"sum\": {\n" +
                    "\t\t\t\"amount\": 90,\n" +
                    "\t\t\t\"currency\": \"JPY\"\n" +
                    "\t\t},\n" +
                    "\t\t\"description\": \"\"\n" +
                    "\t},\n" +
                    "\t\"account\": {\n" +
                    "\t\t\"number\": \"431422******0072\",\n" +
                    "\t\t\"type\": \"visa\",\n" +
                    "\t\t\"card_holder\": \"TEST BHBHB\",\n" +
                    "\t\t\"expiry_month\": \"02\",\n" +
                    "\t\t\"expiry_year\": \"2025\"\n" +
                    "\t},\n" +
                    "\t\"customer\": {\n" +
                    "\t\t\"id\": \"12596\",\n" +
                    "\t\t\"phone\": \"7715934032\"\n" +
                    "\t},\n" +
                    "\t\"operation\": {\n" +
                    "\t\t\"id\": 5001729000032832,\n" +
                    "\t\t\"type\": \"sale\",\n" +
                    "\t\t\"status\": \"awaiting 3ds result\",\n" +
                    "\t\t\"date\": \"2020-06-20T09:36:40+0000\",\n" +
                    "\t\t\"created_date\": \"2020-06-20T09:36:40+0000\",\n" +
                    "\t\t\"request_id\": \"0c36fc2b82090a9c26465f0eda6798b251aa6a29-12513a7034269d8a3d90e575a8ee78b68d7c296c-05001730\",\n" +
                    "\t\t\"sum_initial\": {\n" +
                    "\t\t\t\"amount\": 90,\n" +
                    "\t\t\t\"currency\": \"JPY\"\n" +
                    "\t\t},\n" +
                    "\t\t\"sum_converted\": {\n" +
                    "\t\t\t\"amount\": 84,\n" +
                    "\t\t\t\"currency\": \"USD\"\n" +
                    "\t\t},\n" +
                    "\t\t\"code\": \"9999\",\n" +
                    "\t\t\"message\": \"Awaiting processing\",\n" +
                    "\t\t\"eci\": \"07\",\n" +
                    "\t\t\"provider\": {\n" +
                    "\t\t\t\"id\": 6,\n" +
                    "\t\t\t\"payment_id\": \"\",\n" +
                    "\t\t\t\"endpoint_id\": 6\n" +
                    "\t\t}\n" +
                    "\t},\n" +
                    "\t\"acs\": {\n" +
                    "\t\t\"pa_req\": \"967571051\",\n" +
                    "\t\t\"acs_url\": \"https:\\/\\/mock01.ecpdss.net\\/MPI\\/support\\/asc_merchant\",\n" +
                    "\t\t\"md\": \"eyJwdXJjaGFzZV9vcGVyYXRpb25faWQiOjUwMDE3MjkwMDAwMzI4MzIsInByb2plY3RfaWQiOjE1NTYxLCJwYXltZW50X2lkIjoiMTczOTE3IiwicGx1c19tZCI6IiJ9\",\n" +
                    "\t\t\"term_url\": \"http:\\/\\/localhost:8081\\/transaction\\/Common3DFrontEndServlet?trackingId=173917&status=success\"\n" +
                    "\t},\n" +
                    "\t\"signature\": \"ZVR5Yv9K8istHFq3R\\/Vwyq5yfLG5b0VWYtBfF4L7lQarkckz8uJicvqEwjvE3ZYyE0yQ0I0lh09QkjRrBFRphg==\"\n" +
                    "}";
            JSONObject jsonObject = new JSONObject(response);
            if(jsonObject.has("payment"))
            {
                if(jsonObject.getJSONObject("payment").has("id"))
                {
                   id= jsonObject.getJSONObject("payment").getString("id");
                }
            }
            System.out.println("id--"+id);

        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
*/
    }
}
