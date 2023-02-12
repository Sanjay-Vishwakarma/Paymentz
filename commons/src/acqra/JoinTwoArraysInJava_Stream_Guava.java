package acqra;

import com.google.common.collect.ObjectArrays;
import com.google.common.primitives.Ints;
import org.apache.xalan.lib.sql.ObjectArray;

import java.util.Arrays;
import java.util.stream.Stream;
public class JoinTwoArraysInJava_Stream_Guava
{
    public static void main(String[] args)
    {
        String batsmen[]={ "Rohit","KL Rahul",
                   "Kohli",
                   "Shreyas",
                   "Rishabh",
                   "Dhawan" };
        String bowlers[]={"Hardik","Jadega",
                        "Bumrah",
                        "B kumar",
                        "Axar" };
        // 1st way java 8 Stream
        Stream <String> sBat= Arrays.stream(batsmen);
        Stream <String> sBow= Arrays.stream(bowlers);
        String fullTeam[]=Stream.concat(sBat,sBow).toArray(size->new String[size]);
        for(String s:fullTeam)
        {
            System.out.println(s);
        }
        System.out.println("-----------------------------");
        //2nd way google guava
        String allteam[]= ObjectArrays.concat(batsmen,bowlers,String.class);
        for(String ss:allteam)
        {
            System.out.println(ss);
        }
        System.out.println("-----------------------------");
        //3rd way  Primitive types of arrays
        int p1[]={1,2,3,4,5};
        int p2[]={11,12,13,14,15};
        int p3[]= Ints.concat(p1,p2);
        for(int sss:p3)
        {
            System.out.println(sss);
        }
    }
}
