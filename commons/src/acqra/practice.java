package acqra;

/**
 * Created by Admin on 4/1/2021.
 */
public class practice
{
    public static void main(String[] args)
    {
        /*int var1=108;
        int var2=20;
        int minimum=(var1>var2)?var1:var2;
        System.out.println(minimum);*/
        String form="<form name=\"launch3D\" method=\"GET\" action=\"response3\">"+
                "<input type=\"hidden\" name=\"mcTxId\" value=\"response3D\">"+
                "</form>"+ "<script language=\"javascript\"> document.launch3D.submit(); </script>";
        System.out.println(form);
    }
}
