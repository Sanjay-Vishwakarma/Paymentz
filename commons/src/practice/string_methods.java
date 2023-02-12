package practice;

/**
 * Created by Admin on 3/19/2021.
 */
public class string_methods
{
    public static void main(String[] args)
    {
        //join
        String var = String.join(".", "sagar", "pradeep", "sonar");
        System.out.println(var);
        //split
        String topplayers = "Rohit Dhawan Surya kohli shreyas pant";
        String topplayerssplit[]=topplayers.split(" ");
        for (String foreachvar : topplayerssplit)
        {
            System.out.print(foreachvar);
        }
        System.out.println("\n");
        //tocharArray
        String str="sagar";
        char ch[]=str.toCharArray();
        for(int i=0;i<ch.length;i++)
        {
            System.out.print(ch[i]);
        }
    }
}
