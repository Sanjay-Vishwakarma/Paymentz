package acqra;

/**
 * Created by Admin on 4/26/2021.
 */
public class CallByVal_CallByRef
{
    int p;
    int q;
    public static void main(String[] args)
    {

        CallByVal_CallByRef callByVal_callByRefobj= new CallByVal_CallByRef();
        int x=89;
        int y=11;
        System.out.println(callByVal_callByRefobj.testsum(x,y));//Call By value and pass by value

        callByVal_callByRefobj.p=5;
        callByVal_callByRefobj.q=7;
        callByVal_callByRefobj.swap(callByVal_callByRefobj);
        //after swap using Call by ref or pass by ref
        System.out.println(callByVal_callByRefobj.p);
        System.out.println(callByVal_callByRefobj.q);

    }
    public int testsum(int a,int b)
    {
        int c =a+b;
        return c;
    }

    //Call by ref or pass by ref
    public void swap(CallByVal_CallByRef c)
    {
        int temp;
        temp=c.p;  //temp=5
        c.p=c.q;   //c.p=7
        c.q=temp;  //c.q=5
    }
}
