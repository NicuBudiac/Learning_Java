import java.util.Scanner;


public class GasMileage {
    public void getAveragePerKm()
    {
    Scanner input = new Scanner(System.in);

    int driven_mileage;
    int consumed_gallons;
    double galons_per_miles;

    System.out.print("Introduce number of driven miles if 0 will stop: ");
    driven_mileage = input.nextInt();

    System.out.print("Introduce number of consumed gallons if 0 will stop: ");
    consumed_gallons = input.nextInt();

    while(driven_mileage != 0 && consumed_gallons != 0 )
    {
        galons_per_miles = (double) consumed_gallons / driven_mileage;

        System.out.printf("\nConsumed gallons per miles is: %.2f", galons_per_miles);

        System.out.print("\nIntroduce number of driven miles: ");
        driven_mileage = input.nextInt();

        System.out.print("\nIntroduce number of consumed gallons: ");
        consumed_gallons = input.nextInt();

        if(driven_mileage != 0 && consumed_gallons != 0)
        {
            System.out.printf("\nConsumed gallons per mile is: %.2f: ", galons_per_miles);
        }
        else
            System.out.print("\nConsumed gallons or driven miles can't be 0");
    }


    }

}
