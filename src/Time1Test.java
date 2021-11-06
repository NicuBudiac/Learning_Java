public class Time1Test {
    public static void main(String[] args) {
        // create and initialize a Time1 constructor
        Time1 time =  new Time1();

        // output string representation of the time
        System.out.print("The initial universal time is : ");
        System.out.println(time.toUniversalString() );
        System.out.print("The initial standard time is: ");
        System.out.println(time.toString() );
        System.out.println(); // output a blank line;

        // change time and output updated time
        time.setTime(13,27,6);
        System.out.print("The initial universal after setTime is : ");
        System.out.println(time.toUniversalString() );
        System.out.print("The initial standard after  setTime is: ");
        System.out.println(time.toString() );
        System.out.println(); // output a blank line;

        // attempt to set time and output updated time
        try {
            time.setTime(99,99,99);
        }
        catch (IllegalArgumentException e)
        {
            System.out.printf("Exception: %s\n\n", e.getMessage() );
        } // end catch

        System.out.println("After attempting invalid settings: ");
        System.out.print("Universal time: ");
        System.out.println(time.toUniversalString() );
        System.out.print("Standard time: ");
        System.out.println(time.toString() );
    } // end main
}  // end class Time1Test
