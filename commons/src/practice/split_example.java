package practice;

import com.directi.pg.Functions;

/**
 * Created by Admin on 1/11/2021.
 */
public class split_example
{
    public static void main(String[] args)
    {
        Functions functions = new Functions();
        String url="";
        String transactionId="";
        String response3D="https:\\/\\/payments.cardpayz.com\\/api\\/paymentForm?transaction_id=a98f948a-7a08-4748-8739-65898e5b24c4";
        if(functions.isValueNull(response3D))
        {
            url=response3D.split("\\?")[0];
            transactionId=response3D.split("\\?")[1];
            transactionId=transactionId.split("=")[1];
        }
        else
        {
            url=response3D;
        }
        System.out.println("url-----------"+url);
        System.out.println("transactionId-----------"+transactionId);

    }

}
