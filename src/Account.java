



public class Account {
    private double balance; // instace variable that stores the balance

    // constructor
    public Account(double initialBalance)
    {
        //validate that initialBalance is greater then 0.0
        // of is not , balance is initialized to the default value 0.0
        if(initialBalance > 0.0)
            balance = initialBalance;
    } // end Account Constructor
    // credit (add) an amount to the account
    public void credit(double amount)
    {
        balance = balance + amount; // add amount to balance
    } // end method credit
    public double getBalance() {
        return balance+10;

    }
}
