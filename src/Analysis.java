import java.util.Scanner; // class uses class Scanner


public class Analysis
{
    public static void main( String[] args)
    {
        // Create Scanner to obtain input from command line
        Scanner input = new Scanner(System.in);

        //initialization phase
        int passes =  0;
        int failures = 0;
        int studentCounter = 1;
        int result;

        // Process 10 students using counter-controlled loop
        while (studentCounter <= 10)
        {
            System.out.print("Enter result (1 = pass, 2 = fail): ");
            result = input.nextInt();

            // if ... else nested in the while statement
            if (result == 1)
                passes = passes +1;
            else
                failures = failures + 1;

            // increment student counter
            studentCounter = studentCounter + 1;
        }
        System.out.printf("Passed: %d\nFailed: %d\n", passes , failures);
        if (passes > 8)
            System.out.println("Bonus to instructor!");


    }


}
