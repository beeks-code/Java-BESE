public class basic {
    public static void main(String[] args) {
        float price=(float) 10.2; // casting to float type
        float val=10.2f; // alternative of above statement

        System.out.println("First price value is "+price);
        System.out.println("Second price value is "+val);

        String text=new String("Price is");
        String concat= "Hi" + text + val;
        System.out.println(concat);

    }
}
