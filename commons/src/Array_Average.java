import com.google.common.math.DoubleMath;

import java.util.Arrays;
import java.util.OptionalDouble;

/**
 * Created by Admin on 5/19/2021.
 */
public class Array_Average
{
    public static void main(String[] args)
    {
        int num[]={10,2,89,100,22,15};
        int total=0;
        for(int i:num)//Because the data is of integer type
        {
            total=total+i;
        }
        System.out.println("total is "+total);
        System.out.println("Avg is "+(total/num.length));

        System.out.println("-------------------------------------");

        //Java 8 : stream
        OptionalDouble optionalDouble= Arrays.stream(num).average();
        System.out.println(optionalDouble.getAsDouble());

        System.out.println("-------------------------------------");

        //Google guava

        //double avg1=DoubleMath.mean(num);
        //System.out.println(avg1);
    }
}
