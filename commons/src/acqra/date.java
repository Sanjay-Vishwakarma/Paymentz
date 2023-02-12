package acqra;

import com.payment.validators.vo.CommonValidatorVO;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by Admin on 3/9/2020.
 */
class date
{
    public static void main(String[] args)
    {

        String startDate="2020/03/09";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        try
        {
            System.out.println("date--->"+simpleDateFormat.parse(startDate));
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }

    }
}