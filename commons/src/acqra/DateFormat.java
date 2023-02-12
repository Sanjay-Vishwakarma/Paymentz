package acqra;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Admin on 4/29/2021.
 */
public class DateFormat
{
    public static void main(String[] args)
    {
        try
        {
            SimpleDateFormat format1 = new SimpleDateFormat("MM/dd/yyyy");
            SimpleDateFormat format2 = new SimpleDateFormat("dd-MMM-yy");
            Date date = null;
            date = format1.parse("02/02/2021");
            System.out.println(format2.format(date));
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }

    }
}