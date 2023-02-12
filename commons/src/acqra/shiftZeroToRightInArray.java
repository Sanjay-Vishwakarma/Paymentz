package acqra;

import java.lang.reflect.Array;
import java.util.Arrays;

/**
 * Created by Admin on 5/30/2021.
 */
public class shiftZeroToRightInArray
{
    private static int[] shiftZeroToRightInArrayMethod(int[] array)
    {
        if(array.length==1)
        {
            return array;
        }
        int newArray[]=new int[array.length];
        int count=0;
        for(int number:array)
        {
            if(number!=0)//non zero numbers
            {
                newArray[count]=number;
                count++;
            }
        }
        return newArray;
    }

    public static void main(String[] args)
    {
        int[] i={0,1,0,2,0,8,0,0,9,8,0,8};
        System.out.println(Arrays.toString(shiftZeroToRightInArrayMethod(i)));
    }
}
