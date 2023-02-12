package practice;

/**
 * Created by Admin on 3/26/2021.
 */
public class StringToFloat
{
    public float convertStringToFloatMethod(String stringvar)
    {
        try
        {
            float floatvar=Float.parseFloat(stringvar);
            return floatvar;
        }
        catch (NumberFormatException nfe)
        {
            System.out.println ("NumberFormatException occurred.");
            System.out.println(stringvar+ " is not a valid number");

        }

        return 0;
    }

    public static void main(String[] args)
    {
        StringToFloat obj=new StringToFloat();
        String input="201.50";
        float output=obj.convertStringToFloatMethod("201.50");
        System.out.println("Float value of "+input +" is "+ output);

    }
}
