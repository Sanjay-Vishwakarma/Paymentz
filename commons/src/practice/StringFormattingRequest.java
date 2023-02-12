package practice;

import bsh.StringUtil;

/**
 * Created by Admin on 9/1/2020.
 */
public class StringFormattingRequest
{
    public static void main(String[] args)
    {
        String str ="merchant_id=283475&merchant_site_id=89123755&total_amount=15&currency=USD&user_token_id=test@test.com&item_name_1=item1&item_amount_1=15&item_quantity_1=1&time_stamp=2011-01-056:04:26&version=4.0.0&notify_url=https%3A%2F%2Fnotify.merchant.com";
        String transArr[]={str};
        String response_="";
        for(String transData:transArr)
        {

            if (transData.contains("=") && transData.contains("&"))
            { for(int i=0;i<transArr.length;i++)
            {

                int indx_slash = transData.indexOf("=");
                int indx_at = transData.indexOf("&");
                 response_= String.format(transData.substring(indx_slash + 1, indx_at));

            }
                System.out.println(response_);
            }

            }
        }

  /*      for(int i=0;i<str.length();i++)
        {
            str = str.substring(str.indexOf("=") + 1);
            str = str.substring(0, str.indexOf("&"));
        }
        System.out.println("string " + str.lastIndexOf(str));*/

      /*  System.out.println(str.substring(str.indexOf("=")+1)+" "+str.substring(str.indexOf("&")-1));

        System.out.println();*/
      /*  String last_Cat = str.substring((str.lastIndexOf("cat-") - 1), (str.indexOf("attrib-") - 2));
        System.out.println(last_Cat);
        for (int i = 0; i < str.split("/").length; i++) {
            if (str.split("/")[i].toString().indexOf("attrib-") != -1) {
                String attri = str.split("/")[i].split("-")[1].split("_")[1].toString();
                System.out.println(attri);
            }
        }*/
/*        Pattern pattern = Pattern.compile("=&");
        String[] str1 = pattern.split(str);
        System.out.println(Arrays.toString(str1));*/
     /*   String[] spliteg=str.split("&");
        String[] spliteg1=str.split("=");
        String[] split= (str.split("=",str.indexOf("&")));
        System.out.println();
        for(int i=0;i<spliteg.length;i++)

        {

                System.out.println("Answer-> " + split[i]);

        }*/
    }

