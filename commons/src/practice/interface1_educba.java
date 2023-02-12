package practice;

/**
 * Created by Admin on 3/26/2021.
 */


     interface Animal
    {
        public void animalRuns();

        public void Sleep();
    }
 class interface1_educba1 implements Animal
{
    public void animalRuns ()
    {
        System.out.println("The cat says meow meow");
    }
    public void Sleep()
    {
        System.out.println("cat sleeps");
    }

    public static void main(String[] args)
    {
        interface1_educba1 obj=new interface1_educba1();
        obj.animalRuns();
        obj.Sleep();
    }
}

