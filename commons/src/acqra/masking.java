package acqra;

/**
 * Created by Admin on 7/17/2021.
 */
public class masking
{
    public static void main(String[] args)
    {

//firstname
   /*     String str="sagar";
        String mask= "";
        if(str !=null && !(str.isEmpty())) {
            mask+=str.substring(0, 2);
            for (int i=3;i<=str.length();i++)
                mask+="*";
            mask+=str.substring(str.length());
        }
        System.out.println(mask);*/

//lastname
        String str="namrata";
        String mask= "";
        if(str !=null && !(str.isEmpty())) {
           // mask+=str.substring(0, 2);
            for (int i=0;i<=str.length()-2;i++)
                mask+="*";
            mask+=str.substring(str.length()-2);
        }
        System.out.println(mask);

}}
