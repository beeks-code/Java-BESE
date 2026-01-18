package Basics;
public class typeCasting {
    public static void main(String[] args) {
        byte b;
        int a=110;
        double d=232.33;

        System.out.println("\nConversion of int to byte.");  
        b=(byte)a;
        System.out.println(a);  
        a=(int) d;
        // Int part of d is stored in a
        System.out.println(a);
        


        // Automatic Type Conversion
        byte val1 = 40; 
        byte  val2= 50;  
        byte val3 = 100;  
        int val4 = val1* val2 / val3; // ofc these operation are out of range of Byte type 
        System.out.println(val4);

    }
}
