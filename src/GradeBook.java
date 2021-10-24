// Class declaration with one method
import java.util.Scanner;
public class GradeBook {
    // display a welcome message to the GradeBook user
    private String courseName; // course name for This GradeBook
    private int total;
    private int gradeCounter;
    private int aCount;
    private int bCount;
    private int cCount;
    private int dCount;
    private int fCount;

    // constructor initializez courseNma with String Argument
    public GradeBook(String name) // constructor name is class name
    {
        courseName = name;
    }

    //method to set course name
    public void setCourseName(String name) {
        courseName = name; // store the course name
    } // end the method

    // method to retrive the course name
    public String getCourseName() {
        return courseName;
    } // end the method

    public void displayMessage() {
        System.out.printf("Welcome to the grade book for  \n%s!\n", getCourseName());

    }// end method displayMessage

    public void determineClassAverage() {
        // create Scanner to obtain input from command window
        Scanner input = new Scanner(System.in);
        int total;
        int gradeCounter;
        int grade;
        double average;

        // initialization phase
        total = 0;
        gradeCounter = 0;

        // Processing phase
        System.out.printf("Enter grade or -1 to quit:");
        grade = input.nextInt();

        // loop until sentinel value read from user
        while (grade != -1)  // loop 10 times
        {
            total = total + grade;
            gradeCounter = gradeCounter + 1;
            System.out.printf("Enter grade or -1 ro quit: "); // Enter grade on console
            grade = input.nextInt();
        }

        // termination phase
        if (gradeCounter != 0) {
            average = (double) total / gradeCounter;
            System.out.printf("\nTotal of all %d grades is %d\n", gradeCounter, total);
            System.out.printf("Class average is %.2f\n", average);
        } else
            System.out.println("No grades were entered");

    }




}// end class Grade Book
