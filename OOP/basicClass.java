package OOP;
public class basicClass {
    public static void main(String[] args) {
        student std1= new student();
        std1.name="Beeks";
        std1.age=21;
        std1.height=1.61f; // in meter
        int height_cm=(int)(std1.height *100);
        System.out.printf("%s who studies in %s is %dcm tall and %d yrs old",std1.name,std1.school,height_cm,std1.age);

    }
}

class student{
    int age;
    String name, school="BMSS";
    float height;

}