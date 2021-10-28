// Class declaration with one method
import java.util.Scanner;
public class GradeBook {
    // display a welcome message to the GradeBook user
    private String courseName; // course name for This GradeBook
    private int[] grades;


    // constructor initializez courseNma with String Argument
    public GradeBook(String name, int[] gradesArray) // constructor name is class name
    {
        courseName = name;
        grades = gradesArray;
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

    public void processGrades()
    {
        outputGrades();
        System.out.printf("\nClass average is %.2f\n", getAverage());

        System.out.printf( "Lowest grade is %d\nHighest grade is %d\n\n", getMinimum(), getMaximum());

        outputChart();

    } // end method process Grades


    // find minimum grade
    public int getMinimum()
    {
        int lowGrade = grades[ 0 ]; // assume grade [0] is the smallest
        for (int grade: grades)
        {
            if (grade < lowGrade)
                lowGrade = grade;
        } // end for
        return lowGrade; // return lowest grade
    } // end method getMinimum



    // find maximum grade
    public int getMaximum()
    {
        int highGrade = grades [ 0 ]; // assume grades [ 0 ] is largest
        for (int grade : grades)
        {
            if (grade > highGrade)
                highGrade = grade;
        }
        return highGrade;
    }

    // determine average grade for test
    public double getAverage()
    {
        int total = 0;
        for (int grade: grades)
            total += grade;
        return (double)  total / grades.length;
    } // end method getAverage


    public void outputChart()
    {
        System.out.println("Grade Distribution: ");

        int[] frequency = new int[11];

        for (int grade: grades)
            ++frequency[grade / 10 ];

        for (int count = 0; count < frequency.length; count++)
        {
            if (count == 10)
                System.out.printf("%d: ", 100);
            else
                System.out.printf("%02d-%02d: ", count * 10, count * 10 + 9 );
            for (int stars = 0; stars < frequency.length; stars++)
                System.out.printf("*");

            System.out.println();
        }
    }

    public void outputGrades()
    {
        System.out.println("The grades are: \n");
        for (int student = 0; student < grades.length; student++)
            System.out.printf("Student %2d: %3d\n", student + 1, grades[student]);
    }





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
