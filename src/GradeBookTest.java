//import java.util.Scanner;

public class GradeBookTest
{   // main method begins program execution
    public static void main(String[] args)
    {
        //create Scanner to obtain input from command window
        //Scanner input = new Scanner(System.in);
        // create a GradeBook object and assign it to myGradeBook
        //GradeBook myGradeBook = new GradeBook("Java");
        //GasMileage myGasMileage = new GasMileage();
        SalaryCalculator newSalaryCalculator = new SalaryCalculator();

        //prompt for input input course name
        //System.out.println("Please insert the course name:");
        //String theName =  input.nextLine(); // read a line of text
        //myGradeBook.setCourseName(theName); // set the course name
        //System.out.println(); // output an blank line



        // call myGradeBooks's displayMessage method
        //myGradeBook.displayMessage();
        //myGradeBook.determineClassAverage();
        //myGasMileage.getAveragePerKm();
        newSalaryCalculator.salesCommissionCalculator();
    }// end main
} // end class GradeBookTest
