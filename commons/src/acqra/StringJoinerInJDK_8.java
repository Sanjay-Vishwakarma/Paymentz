package acqra;

import java.util.StringJoiner;

/**
 * Created by Admin on 5/10/2021.
 */
public class StringJoinerInJDK_8
{
    public static void main(String[] args)
    {
        StringJoiner stringJoiner = new StringJoiner(";","{","}");
        stringJoiner.add("Dhoni");
        stringJoiner.add("Kohli");
        stringJoiner.add("Pandya");
        stringJoiner.add("Raina");
        System.out.println(stringJoiner);
    }
}
