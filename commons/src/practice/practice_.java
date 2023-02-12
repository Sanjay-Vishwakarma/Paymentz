package practice;

import com.directi.pg.Database;
import com.directi.pg.Functions;
import com.directi.pg.SystemError;
import com.manager.dao.WLPartnerInvoiceDAO;
import com.manager.vo.WLPartnerInvoiceVO;
import com.manager.vo.payoutVOs.WLPartnerCommissionDetailsVO;
import com.payment.Ecommpay.EcommpayPaymentGateway;
import javafx.util.converter.DateTimeStringConverter;
import org.apache.commons.net.ntp.TimeStamp;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.ValidationErrorList;
import org.owasp.esapi.errors.ValidationException;
import org.owasp.esapi.reference.DefaultEncoder;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;


class practice_
{
    public static void main(String[] args)
    {

       /* List <String> list= new ArrayList<>();
        list.add("ABD");
        list.add("steyn");
        list.add("Burns");
        list.add("kohli");


        Collections.sort(list);
        System.out.println(list);
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < list.size(); i++)
        {
            sb.append(list.get(i));
            if (i < list.size() - 1)
            {
                sb.append("|");
            }
        }
        System.out.println(sb);*/
    }}

      /*  String str="PvpTest-112334545454545454545445554454545676766767777yyyyyyyyyyyyyyyykjkjkjkjkjnjhhbgcfxddddddddddz";
        System.out.println(str.length());*/
       /* for(int i=1;i<=5;i++)
        {
            for(int j=1;j<=10;j++)
            {
                System.out.print("i :"+ i +" , j :"+j);
            }
        }*/

//BUBBLESORT example
/*class practice_main
{
    public void sort(int[] array)
    {
        for(int i=0;i<array.length;i++)
        {
            for (int j = 1; j < array.length; j++)
            {
                if (array[j] < array[j - 1])
                {
                    swapmethod(array, j, j - 1);
                }
            }
        }
    }

            private void swapmethod(int[] array,int index1,int index2)

{
    int temp = array[index1];
    array[index1] = array[index2];
    array[index2] = temp;
}
        }
class practice__
{
    public static void main(String[] args)
    {
        int[] numbers={8,10,1,20,67};
        practice_main obj = new practice_main();
        obj.sort(numbers);
        System.out.println(Arrays.toString(numbers));

    }

}*/


// Eg of LinkedHashSet prints element in same order of insertion
        /*LinkedHashSet linkedHashSet = new LinkedHashSet();
        linkedHashSet.add(1);
        linkedHashSet.add("190");
        linkedHashSet.add(5000);
        linkedHashSet.add(4);
        System.out.println(linkedHashSet);
*/

/*int a=10;
        int b=20;
       a*=9;
        System.out.println(a);*/

    /* StringBuffer st = new StringBuffer("sagar ");
        st.append(" sonar");
        System.out.println(st);*/


        /*long tempdt = 0;
        Date date = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        tempdt=c.getTime().getTime();
        tempdt=tempdt/1000;
        String str =" "+tempdt;

        SimpleDateFormat sdf =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        str=sdf.format(str);
        System.out.println("date "+str);*/
    /*
    public static String getFormattedDate(String format)
    {
        TimeZone time = TimeZone.getTimeZone("Indian");
        Calendar cal = Calendar.getInstance(time);

        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        Date currentDate = cal.getTime();
        return dateFormat.format(currentDate);
    }

    public static void main(String[] args)
    {
practice_ obj = new practice_();
        System.out.println(obj.getFormattedDate("yyyy-MM-dd HH:mm:ss"));
*/


            /* public static String convertDtstampToDateTimeforTimezone(String str)
    {
        String dt = null;
        SimpleDateFormat dbFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss" );
        try
        {
            long longdt = Long.parseLong(str);
            Date date = new Date(longdt * 1000);
            dt=dbFormatter.format(date);

        }
        catch (NumberFormatException ne)
        {
            System.out.println(ne);
        }
        if (dt == null) dt = "";
        return dt;
    }*/



       /* String fdt="03,04,1999 12:12:03";
        long milliSeconds = Long.parseLong( fdt+ "000");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        DateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String startDate = formatter1.format(calendar.getTime());
        System.out.println(startDate);*/


/*        long tempdt = 0;
        Date date = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        tempdt=c.getTime().getTime();
        tempdt=tempdt/1000;
        String str =" "+tempdt;

        SimpleDateFormat sdf =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
         str=sdf.format(str);
        System.out.println("date "+str);*/

/* Giving wrong date
        String date = "2020-12-11";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar c = Calendar.getInstance();
        try
        {
            c.setTime(sdf.parse(date));
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        //c.add(Calendar.DATE,0);
        date=sdf.format(c.getTime());
        System.out.println(date);
*/


       /* long eg= 1364639872;
        Date  d = new Date(eg/1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT-4"));
        String formattedDate=sdf.format(d);
        System.out.println(formattedDate);*/


                // Book1 Eg og simpledate format
    /* LocalDateTime localDateTime =LocalDateTime.now();
        System.out.println(localDateTime);
        DateTimeFormatter dateTimeStringConverter =DateTimeFormatter.ofPattern("yyyy-mm-dd hh:mm:ss");
        String string =localDateTime.format(dateTimeStringConverter);
        System.out.println("Date -> "+string);
*/

/*        Date date = new Date();

        System.out.println(new Date(new Date().getTime()));*/

                //    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                //  String mydate=sdf.format(d);
                //System.out.println(mydate);




       /* String date = String.valueOf(new Date());
        long milliSeconds = Long.parseLong(date + "000");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        DateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        date = formatter2.format(calendar.getTime());
        System.out.println(date);
       String youtstring="22-2010-7 12:10:12:23";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = sdf.parse(youtstring);
        TimeStamp stamp = new TimeStamp(date.getTime());
        System.out.println(stamp);
*/

/*
        int a=10;
        int b=3;
        System.out.println("Answer- "+(a+b));
*/


     /*   Date date = new Date();
        SimpleDateFormat formatter1 =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println(formatter1.format(date));
*/


// Example of hashtable in details
/*
class practice_
{
    public static void main(String[] args)
    {

        //Example of Hashtable, Clone, Enumeration, EntrySet
        // Null key or values in Hashtable
        Hashtable hashtable1=new Hashtable();
        hashtable1.put(1,"kobe");
        hashtable1.put(9,"kevin");
        hashtable1.put(7,"rohit");
        hashtable1.put(60,"chris");
        hashtable1.put("Abc","XYZ");//If generics are not defined while creating object this can be applicable

        Hashtable hashtable2= new Hashtable();
        hashtable2=(Hashtable)hashtable1.clone(); // Now hashtable2 will have all values of hashtable1

        System.out.println("hashtable1->"+hashtable1);
        System.out.println("hashtable2->"+hashtable2);

        System.out.println(hashtable1.hashCode());
        System.out.println(hashtable2.hashCode());

       // hashtable1.clear();  // All values of hashtable1 will be removed be hashtable2 values which are clone will remain same

        System.out.println("hashtable1->"+hashtable1);
        System.out.println("hashtable2->"+hashtable2);

        Enumeration enumerationobj = hashtable1.elements();
        System.out.println("Below are values using enumeration");
         while(enumerationobj.hasMoreElements())
         {
             System.out.println(enumerationobj.nextElement());
         }

        System.out.println("Below are values using entrySet");
        Set s = hashtable1.entrySet();
        System.out.println(s);

        // You can also check of both Hashtable are equal or not

        Hashtable hashtablenew =new Hashtable();
        hashtablenew.put(1,"kobe");
        hashtablenew.put(9,"kevin");
        hashtablenew.put(7,"rohit");
        hashtablenew.put(60,"chris");
        hashtablenew.put(60,"chris");// It will only print unique elements


        if(hashtable1.equals(hashtablenew))
        {
            System.out.println("Both hashtables are equal");
        }
        else
        {
            System.out.println("Not equal");
        }

        //Get the value from key
        System.out.println(hashtablenew.get(7));
    }
}
*/


                //Example to get hashcode

 /*   practice_ obj = new practice_();
        practice_ obj1= new practice_();
        System.out.println(obj.hashCode());
        System.out.println(obj1.hashCode());*/

  /*  {
    //String var="2020-08-01 23:59:59";
        Date date = new Date();

        DateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
       String  startDate = formatter1.format(date.getTime());
        System.out.println(startDate);
    }
*/


//swap using function
/*class practice_
{
    public static void main(String[] args)
    {
        int a=10;
        int b=20;
        System.out.println("Before a="+a +" and "+"b="+b);
      swapfunction(a,b);


    }
    public static void swapfunction(int a,int b)

    {
       int c = a;
        a=b;
        b=c;
        System.out.println("After a="+a +" and "+"b="+b);
    }
}*/
// Method example for marks
/*class practice_
{
    public static void main(String[] args)
    {
        marksobtained(99.78);
    }
    public static void marksobtained(double marks)
    {
        if(marks>=80)
        {
            System.out.println("A+");
        }
        else if(marks<=80 && marks>=60)
        {
            System.out.println("A");
        }

    }
}*/


//Method in class Eg for minimum number between two
/*
 class practice_
{
    public static void main(String[] args)
    {
        int var1=1;
        int var2=20;
        int var3=minbetween2number(var1,var2);
        System.out.println("Minimum is ->"+var3);
    }
    public static int minbetween2number(int num1,int num2)
    {
        if(num1<num2)
        {
            return num1;
        }
        else
        {
            return num2;
        }

    }
}
*/


   /* public static void main(String[] args)
    {
        Calendar calendar=Calendar.getInstance();
        System.out.println(calendar.toString());
    }*/

//Thread Example
/*public class practice_ extends Thread

{

    public void run()
    {
        int x = 1;
        while (true)
        {
            System.out.println(x);
            x++;
            try
            {
                Thread.sleep(10000);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args)
    {
        practice_ obj = new practice_();
        obj.start();
        practice_ obj1 = new practice_();
        obj1.start();
        practice_ obj2 = new practice_();
        obj2.start();
    }

}*/


         /*   char i;
        char j;
        for(i='a';i<='e';i++)
        {
            for(j='a';j<='e';j++)
            {

                System.out.print(j +" ");

            }
            System.out.println();

        }*/

      /*  Date date3=new Date();
        System.out.println("startTime---" + date3.getTime());*/

       /* String str="";
        Functions functions = new Functions();
        if(functions.isValueNull(str))
        {
            System.out.println(str);
        }
        else
        {
            System.out.println("-");
        }
*/

    /*
        String str ="sagarsonar1234567890";
        System.out.println(str.substring(0,5));
        System.out.println(str.substring(3));*/




/*    public  String checkStringNullDash(String checkstr)
    {
        if (checkstr != null)
        {
            checkstr = checkstr.trim();

            if (checkstr.equals("null"))
            {
                checkstr = "-";
            }

            if (checkstr.equals(""))
            {
                checkstr = "-";
            }

        }
        return checkstr;
    }*/
/*String response ="{
	"project_id": 15561,
	"payment": {
		"id": "173893",
		"type": "purchase",
		"status": "awaiting 3ds result",
		"date": "2020-06-18T14:52:40+0000",
		"method": "card",
		"sum": {
			"amount": 90,
			"currency": "JPY"
		},
		"description": ""
	},
	"account": {
		"number": "424242******4242",
		"type": "visa",
		"card_holder": "TEST BHBHB",
		"expiry_month": "03",
		"expiry_year": "2023"
	},
	"customer": {
		"id": "12596",
		"phone": "7715934032"
	},
	"operations": [{
		"id": 5001855000032182,
		"type": "sale",
		"status": "awaiting 3ds result",
		"date": "2020-06-18T14:52:40+0000",
		"created_date": "2020-06-18T14:52:39+0000",
		"request_id": "5c87ac4d81b7727800aeb5703eff0432df4aed2f-e70a72cdd8fab629d2c8d4302066ab9cd4c8eff4-05001856",
		"sum_initial": {
			"amount": 90,
			"currency": "JPY"
		},
		"sum_converted": {
			"amount": 84,
			"currency": "USD"
		},
		"code": "9999",
		"message": "Awaiting processing",
		"eci": "07",
		"provider": {
			"id": 6,
			"payment_id": "",
			"endpoint_id": 6
		}

	}],
	"acs": {
		"pa_req": "764947656",
		"acs_url": "https:\/\/mock01.ecpdss.net\/MPI\/support\/asc_merchant",
		"md": "eyJwdXJjaGFzZV9vcGVyYXRpb25faWQiOjUwMDE4NTUwMDAwMzIxODIsInByb2plY3RfaWQiOjE1NTYxLCJwYXltZW50X2lkIjoiMTczODkzIiwicGx1c19tZCI6IiJ9",
		"term_url": "https:\/\/staging.paymentz.com\/transaction\/Common3DFrontEndServlet?trackingId=173893&status=success"
	},
	"signature": "2vb40DihANg+1o8pPEJYM6cJIwOi3AkkyOPCLimXHjSrJcGveO3lVF3ratE2GX7Fikp+ojy4Yup+FEia45J3Zg=="
}"; */
                //Abstract example
   /*     abstract class BankofIndia
        {
            abstract void DepositMoney();
        }
        class customer1 extends BankofIndia
        {

            public void DepositMoney()
            {
                System.out.println("customer1 deposits 1200rs");
            }
        }
        class customer2 extends BankofIndia
        {
            public void DepositMoney()
            {
                System.out.println("customer2 deposits 10000rs");
            }

        }
    class example
    {
        public static void main(String[] args)
        {

            customer1 obj1 = new customer1();
            obj1.DepositMoney();

            customer2 obj2 = new customer2();
            obj2.DepositMoney();
        }
    }
*/
//This show is boolean if(false) that code doesnot print its else part will be printed
       /* boolean b=true;
        boolean c=false;
        if(b)
        {
            System.out.println("PRint 1");
        }
        if(c)
        {
            System.out.println("PRint 2");
        }
        else
        {
            System.out.println("This is false");
        }
        if(b)
        {
            System.out.println("Print 3");
        }
*/


                //Array eg to find divisible of number
       /* int arr[] = new int[10];
        arr = new int[]{1, 20, 30, 4, 80, 56, 54, 32, 9, 11};
        for (int i = 0; i < arr.length; i++)
        {
            if (arr[i]%10 == 0)
            {
                System.out.print(arr[i] + " ");
            }
            System.out.println();
        }*/

                //how to format date
       /* DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date date = new Date();
        System.out.println(dateFormat.format(date));*/

/*  String var = "tcAzbuYTNRCjoUOihe3IvH0r9BNEQsjhgfdsfghjhgfghjkhgioioihjkhgfhjkhghjkhghjhgjkhjhgjhgjkghjhghgjhgjhhjhgjMSx";
        System.out.println(var.length());*/

    /*public static Hashtable listtransactions(String trackingid)
    {
        Hashtable hashtable = null;
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        try
        {
            conn = Database.getConnection();
            StringBuilder selectquery = new StringBuilder("SELECT tf1.*, from unixtime(tf1.dtstamp) as TransactionTime,m.* from transaction_fail_log as tf1 left join members as m on tf1.toid=m.memberid where id=?");
            preparedStatement = conn.prepareStatement(selectquery.toString());
            preparedStatement.setString(1, trackingid);
            hashtable = Database.getHashFromResultSetForTransactionEntry(preparedStatement.executeQuery());
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        catch (SystemError systemError)
        {
            systemError.printStackTrace();
        }
        return hashtable;
    }
    public static void main(String[] args)

    {
        String trackingid = "";
        Hashtable hashtable = listtransactions(trackingid);
        System.out.println(hashtable);
    }*/
       /* Date date1=new Date();
        System.out.println("Time---" + date1.getTime());*/


    /*public String checkStringNullDash(String checkstr)
    {
        if (checkstr != null)
        {
            checkstr = checkstr.trim();

            if (checkstr.equals("null"))
            {
                checkstr = "-";
            }

            if (checkstr.equals(""))
            {
                checkstr = "-";
            }

        }
        return checkstr;
    }
    public static void main(String[] args)
    {

practice_ obj =new practice_();
String var = "sagar";
        System.out.println(obj.checkStringNullDash(var));


    }
}*/

// How to create token
/*String ctoken=ESAPI.randomizer().getRandomString(100, DefaultEncoder.CHAR_ALPHANUMERICS);
System.out.println("ctoken->"+ctoken.toString());*/

                //isEmpty or null check with error message accordingly also enum can be used here
  /*  public boolean isEmptyOrNull(String str)
    {
        if(str == null || str.equals("null") || str.trim().equals(""))
        {
            System.out.println("success");
            return true;

        }
        System.out.println("failed");
        return false;
    }
    public static void main(String[] args)
    {
        practice_ obj = new practice_();
        System.out.println(obj.isEmptyOrNull("abc"));*/


                //Arraylist example by refercing other class values
     /*   ArrayList<String> arrayList=new ArrayList<>();
        arrayList.add(EcommpayPaymentGateway.amethod());
        System.out.println(arrayList);*/
                //Treemap example
       /* TreeMap<Integer,String> treeMap = new TreeMap<>();
        treeMap.put(10,"india");
        treeMap.put(20,"russia");
        treeMap.put(56,"england");
        treeMap.put(2,"america");
        treeMap.put(1,"korea");
        treeMap.put(40,"australia");
       // System.out.println(treeMap);
        Set set =treeMap.entrySet();
        Iterator iterator =set.iterator();
        while(iterator.hasNext())
        {
        Map.Entry mobj =(Map.Entry)iterator.next();
            System.out.println(mobj.getKey()+" <- key-value -> "+mobj.getValue()+"\n");
        }
*/
                //total occurnece of char
        /*String var="Teeeerttt";
        System.out.println("Length of var->" + var.length());
        System.out.println("length of var without E->"+var.replaceAll("e","").length());
        int charcount=var.length()-var.replaceAll("e","").length();
        System.out.println(charcount+1);
*/


    /*public static String hashSHA256(String plainText)
    {
        StringBuffer ciphertext = new StringBuffer();
        try
        {
            MessageDigest messageDigest =MessageDigest.getInstance("SHA-256");
            byte[] hash =messageDigest.digest(plainText.getBytes("UTF-8"));
            for(int i=0;i<hash.length;i++)
            {
                String hex=Integer.toHexString(0xff & hash[i]);
                if(hex.length()==1)ciphertext.append('0');
                ciphertext.append(hex);
            }
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }*/


/*
         private static Hashtable hashtable = new Hashtable();
        static
        {

            hashtable.put("123","kohli");
            hashtable.put("51111","Payment failure");
            hashtable.put("50004","Email error");
            hashtable.put("50006","security_code error");


        }
    public static String getStatusDescription(String responseCode)
    {
        String description="";
        hashtable.get(responseCode);
       return description;

    }

    public static void main(String[] args)
    {
        practice_ practice_obj = new practice_();
        System.out.println(practice_obj.getStatusDescription("Email error"));
    }

*/

