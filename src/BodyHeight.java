import java.util.Scanner;

public class BodyHeight {
    public void calculateBMI(){
        Scanner input = new Scanner(System.in);
        float weight;
        float height;
        float BMI;

        System.out.print("Introduce your weight : ");
        weight = input.nextInt();

        System.out.print("Introduce your height: ");
        height = input.nextInt();



        BMI = weight/((height/100)*(height/100));
        System.out.println("Result BMI: ");

        if (BMI < 18.5)
            System.out.printf("%.2f\nUnderweight:\n",BMI);
        if (BMI >= 18.5 & BMI <= 24.9)
            System.out.printf("%.2f\nNormal\n", BMI);
        if (BMI >= 25 & BMI <= 29.9 )
            System.out.printf("%.2f\nOverweight\n", BMI);
        if (BMI > 30 )
            System.out.printf("%.2f\nObese\n",BMI);

    }
}
