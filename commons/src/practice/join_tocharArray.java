package practice;

/**
 * Created by Admin on 2/26/2021.
 */
public class join_tocharArray
{
    public static void main(String[] args)
    {
        //tocharArray
        String str1="example";
        char ch[]=str1.toCharArray();
        System.out.println(ch);
        for(char i:ch)
        {
            System.out.println(i);
        }

        //join
        String str=String.join("Indian Player"," kohli "," Rishabh "," Axar "," Ashwin ");
        System.out.println(str);
    }
}
