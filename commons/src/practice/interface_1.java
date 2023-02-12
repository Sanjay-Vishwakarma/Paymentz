package practice;

/**
 * Created by Admin on 1/1/2021.
 */
interface myfirstinterface
{
    void showmethod();
}
/*class myfirstinterfaceIMPL implements myfirstinterface
{
    @Override
    public void showmethod()
    {
        System.out.println("Inside show method");
    }
}*/

// Anonymous class eg
/*
public class interface_1
{
    public static void main(String[] args)
    {
        myfirstinterface obj = new myfirstinterface()
        {
            public void showmethod()
            {
                System.out.println("Inside show method using Anonymous");
            }
        };
        obj.showmethod();

    }
}
*/

//lambda expresssion eg
public class interface_1
{
    public static void main(String[] args)
    {
        myfirstinterface obj =() ->
                System.out.println("Inside show method using Anonymous");

        obj.showmethod();

    }
}
