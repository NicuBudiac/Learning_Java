public class PackageDataTest {
    public static void main(String[] args) {
        PackageData packageData = new PackageData();

        System.out.printf("After instantion: \n%s\n", packageData);

        packageData.number = 77;
        packageData.string = "Googbye";

        System.out.printf("\nAfter changing values:\n%s\n", packageData);


    }
}

class PackageData
{
    int number;   // package-access instance variable
    String string; // package-access instance variable

    // constructor
    public PackageData()
    {
        number = 0;
        string = "Hello";
    } // end Package constructor

    public String toString()
    {
        return String.format("number: %d; string: %s", number, string);
    } // end to string method
}

