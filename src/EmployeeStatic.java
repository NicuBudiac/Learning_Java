public class EmployeeStatic {
    private String firstName;
    private String lastName;
    private static int count = 0 ;

    public EmployeeStatic(String first, String last)
    {
        firstName = first;
        lastName = last;

        ++count; // increment static count of employee
        System.out.printf("Employee constructor: %S %s; count = %d\n", firstName, lastName, count);

    } // end Employee


    // get first name
    public String getFirstName()
    {
        return firstName;
    }


    // get Last Name
    public String getLastName()
    {
        return lastName;
    }


    // static method to get static count value

    public static int getCount()
    {
        return count;
    }


}
