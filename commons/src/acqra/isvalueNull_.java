package acqra;

import com.directi.pg.Functions;

/**
 * Created by Admin on 6/7/2021.
 */
public class isvalueNull_
{
    public static void main(String[] args)
    {

        Functions functions = new Functions();
        String str="";
        boolean b=true;
        if(functions.isValueNull(str))
        {
            System.out.println("Value is "+b);
        }
        else
        {
            System.out.println("Value is "+b);
        }

        double d=120.60;
        byte by=(byte)d;
        System.out.println(by);
    }
}
