package practice;

/**
 * Created by Admin on 3/18/2021.
 */
public class static_eg
{

    int i=20;
    static int j=100;
    public static void main(String[] args)
    {

        static_eg obj= new static_eg();
        j=90;
        //i=1009;
        System.out.println(obj.i);
        System.out.println(obj.j);

    }
}
