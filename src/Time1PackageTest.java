import com.deitel.jhtp.ch08.Time1;

public class Time1PackageTest {
    public static void main(String[] args) {
        // create and initialize a Time1 object
        Time1 time = new Time1();

        // output string representation as the time
        System.out.print("The initial universal time is: ");
        System.out.println(time.toUniversalString() );
        System.out.print("The initial standart time is: ");
        System.out.println(time.toString() );
        System.out.println();


        // change time and output updated time
        time.setTime(13,27,6);
        System.out.print("Universal time after setTime is: " );
        System.out.println(time.toUniversalString() );
        System.out.print("Standart time after setTime is: ");
        System.out.println(time.toString() );
        System.out.println();

        try
        {
            time.setTime(99,99,99);
        }
        catch (IllegalArgumentException e)
        {
            System.out.printf("Exception: %s\n\n", e.getMessage() );
        } // end catch

        System.out.println("After attempting invalid setting: ");
        System.out.print("Universal time");
        System.out.println(time.toUniversalString() );
        System.out.print("Standart time: ");
        System.out.println(time.toString() );
    }
}
