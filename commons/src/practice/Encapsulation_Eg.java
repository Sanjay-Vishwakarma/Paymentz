package practice;

/**
 * Created by Admin on 3/9/2021.
 */
public class Encapsulation_Eg
{
    private String name;
    public String getName()
    {
        return name;
    }
    public void setName(String name)
    {
        this.name=name;
    }

}
class Encapsulation_Eg_Main
{
    public static void main(String[] args)
    {
        Encapsulation_Eg obj = new Encapsulation_Eg();
        obj.setName("Sagar");
        String name_var=obj.getName();
        System.out.println("Name---"+name_var);
        String var="uiuiuiuiuiuiuiuiuiuiuiuiuiuiuuuiuiuiuiuiuiuiuiuiuo";
        System.out.println(var.length());

    }
}
