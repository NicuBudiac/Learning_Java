import java.util.Scanner;

public class SalaryCalculator {
    public void salesCommissionCalculator(){
        Scanner input = new Scanner(System.in);

        int salary = 200;
        float gross_sales = 0;
        float total_salary;
        float bonus;
        float item_value;

        System.out.print("Enter sells item in dollars if -1 will calculate salary : ");
        item_value = input.nextFloat();


        while (item_value != -1)
        {
            System.out.print("Enter sells items in dollars if -1 will calculate salary : ");
            item_value = input.nextFloat();
            gross_sales += item_value;
        }

        if (gross_sales != 0)
            {
            bonus =  (gross_sales * 9) / 100;
            total_salary = salary + bonus;
            System.out.printf("\nSalary per week is %.2f", total_salary);
            }

        else
            {
            total_salary = salary;
            System.out.printf("\nSalary per week is %.2f:", total_salary);
            }
    }


}
