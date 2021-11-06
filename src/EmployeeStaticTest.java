public class EmployeeStaticTest {
    public static void main(String[] args) {
        // show that counter is 0 before creating Employees
        System.out.printf("Employees before instantiation: %d\n", EmployeeStatic.getCount() );


        EmployeeStatic e1 = new EmployeeStatic("Susan", "Baker");
        EmployeeStatic e2 = new EmployeeStatic("Bob", "Blue");

        // show that employee is 2 after creating two Employees
        System.out.println("\nEmployees after instantiation: ");
        System.out.printf( "via e1.getCount(): %d\n", e1.getCount());
        System.out.printf( "via e2.getCount(): %d\n", e2.getCount());
        System.out.printf("via Employee.getCount(): %d\n", EmployeeStatic.getCount() );

        // get names of Employees
        System.out.printf("Employee 1: %s %s\nEmployee 2 : %s %s\n",
                e1.getFirstName(), e1.getLastName(),
                e2.getLastName(), e2.getFirstName());


        e1 = null;
        e2 = null;
    }
}
