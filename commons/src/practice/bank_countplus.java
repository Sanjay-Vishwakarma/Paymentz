package practice;

/**
 * Created by Admin on 3/22/2021.
 */
public class bank_countplus
{
    public static void main(String[] args)
    {
        int count=0;
        int num =10;
        int bal=0;
        for(int i=0;i<=num;i++)
        {
            bal=num*i;
           count++;
      //      System.out.println(bal);
        }

        System.out.println(bal);
        System.out.println(count);

    }

}
