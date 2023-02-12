package practice;

/**
 * Created by Admin on 2/10/2021.
 */
public class CallByValue_and_Ref
{
    int var1=50;
    int var2=60;
    public static void main(String[] args)
    {
        CallByValue_and_Ref callByValue_and_ref = new CallByValue_and_Ref();
        int x=10;
        int y=20;
        callByValue_and_ref.swap_callbyref(callByValue_and_ref);
        System.out.println(callByValue_and_ref.add(x,y));// Call by value
        System.out.println("var1--"+callByValue_and_ref.var1);
        System.out.println("var2--"+callByValue_and_ref.var2);
    }
    public int add(int a,int b)
    {

        int c;
        c=a+b;
        return c;
    }

    public void swap_callbyref(CallByValue_and_Ref obj)
    {
        int temp;
        temp=obj.var1;
        obj.var1=obj.var2;
        obj.var2=temp;
    }
}
