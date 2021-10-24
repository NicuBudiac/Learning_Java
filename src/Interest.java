public class Interest {
    public static void main(String[] args)
    {
        double amount; // amound on deposit et the end of each year
        double principal = 1000.0; // initial amount before interest
        double rate = 0.05; // interest rate

        System.out.printf("%s%30s\n", "Year", "Amount of deposit");

        // calculate amount on deposit for specified year
        for(int year = 1; year <= 10; year++ )
        {
            // calculate new amount for specified year
            amount = principal * Math.pow(1.0 + rate, year );

            // display the year and the amount
            System.out.printf("%4d%,20.2f\n", year, amount);

        }
    }
}
