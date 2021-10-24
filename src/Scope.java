public class Scope {

    private static int x = 1;

    public static void main(String[] args)
    {
        int x = 5;

        System.out.printf("local x in main is %d\n", x);

        userLocalVariable();
        userField();
        userLocalVariable();
        userField();

        System.out.printf("\nlocal x is main is %d\n", x);
    } // end main

    public static void userLocalVariable()
    {
        int x = 25;

        System.out.printf("\nlocal on entering method userLocalVariable is %d\n ", x);
        ++x;

        System.out.printf("local x before exiting methid userLocalVariable is %d\n", x);
    } // end userLocalVariable

    public static void userField()
    {
        System.out.printf("\nfiled x on entering method userField is %d\n", x);
        x *= 10;

        System.out.printf("field x before exiting method userField is %d\n", x);
    }
}
