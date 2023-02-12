package acqra;

import java.util.Arrays;
import java.util.BitSet;

/**
 * Created by Admin on 5/25/2021.
 */
public class PrintNumberWithLoopRecursion
{
    public static void main(String[] args)
    {
        //1st way
        Object number[] = new Object[50];
        Arrays.fill(number, new Object()
        {
            int count=0;

            public String toString()
            {
                return Integer.toString(++count);
            }
        });
        System.out.println(Arrays.toString(number));

        //2nd way
        String set = new BitSet() {{set(1,51);}}.toString();
        System.out.append(set, 1,set.length());
    }
}
