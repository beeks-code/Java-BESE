package Basics;
// Block scope
public class scope {
    public static void main(String args[]){
        int x=10;
        if(true)
        {
            // int x=20; // redeclaration of variable  throws error
            int localVar=100; // limited within this block 
            x=20; // x is already declared outside block so, it is possible to get it inside the block
            System.out.println("Local value of x is "+x);
        }
        System.out.println("Outside block value of x is"+x); //20
        // System.out.println(localvar);  
    }

}