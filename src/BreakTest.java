public class BreakTest {
    public static void main(String[] args) {
        int count;
        for (count = 1; count <= 10; count++)
        {
            if (count == 5)
                break; // terminate loop
            System.out.printf("%d", count);
        }
        System.out.printf("\nBroke out loop at count = %d\n", count);
    }
}
