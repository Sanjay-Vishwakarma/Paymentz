package practice;

/**
 * Created by Admin on 3/16/2021.
 */
public class Print_Hello_World_without_using_semicolon
{
    public static void main(String[] args)
    {
        //sol1
        if(System.out.printf("Hello world")==null)
        {

        }
        //sol2
        if(System.out.append("Hello world")==null)
        {

        }
        //sol3
        if(System.out.printf("Hello world").equals(null))
        {

        }
        //sol4
        for(int i=0;i<1;System.out.print("Hello world"))
        {
            i++;
        }
    }
}
