import java.io.*;
class Rearrange{
    public static void main(String args[])
            throws IOException{
        InputStreamReader in = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(in);
        System.out.print("Enter the integer: ");
        long n = Long.parseLong(br.readLine());
        String s = Long.toString(n);
        if(s.length() <= 1){
            System.out.println("Invalid input!");
            return;
        }
        int i = 0;
        int j = 0;
        char a[] = new char[s.length()];
        for(i = 0; i < s.length(); i++)
            a[i] = s.charAt(i);
        for(i = 0; i < a.length; i++){
            for(j = 0; j < a.length - 1 - i; j++){
                if(a[j] > a[j + 1]){
                    char temp = a[j];
                    a[j] = a[j + 1];
                    a[j + 1] = temp;
                }
            }
        }
        i = 0;
        while(a[i] == '0')
            i++;
        if(a[0] == '0'){
            a[0] = a[i];
            a[i] = '0';
        }
        s = new String();
        for(i = 0; i < a.length; i++)
            s += a[i];
        long num = Long.parseLong(s);
        System.out.println("Rearranged number: " + num);
    }
}
