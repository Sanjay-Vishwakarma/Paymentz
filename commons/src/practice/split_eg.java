package practice;

/**
 * Created by Admin on 1/1/2021.
 */
public class split_eg
{
    public static void main(String[] args)
    {
            String arr ="10,20,100,30";
            String[] arrstr = arr.split(",");
        for(String foreachobj : arrstr)
        {
            System.out.println(foreachobj);
        }
    }
}
