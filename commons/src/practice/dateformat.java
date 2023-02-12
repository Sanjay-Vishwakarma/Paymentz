package practice;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Created by Admin on 2/24/2021.
 */
public class dateformat
{
    public static void main(String[] args)
    {
        String date="2021-02-24 16:44:51";
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        System.out.println("Formated Date---"+dateFormat.format(date));
    }
}
