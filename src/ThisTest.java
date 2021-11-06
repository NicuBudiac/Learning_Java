public class ThisTest {
    public static void main(String[] args) {
        SimpleTime time = new SimpleTime(15,30,19);
        System.out.println(time.buildString() );

    }
}
class SimpleTime{
    private int hour;
    private int minutes;
    private int second;

    //if constructor uses parameter names identical to
    // instance variable names the "this" reference is
    // required to distinguish between the names

    public SimpleTime(int hour, int minutes, int second)
    {
        this.hour = hour;
        this.minutes = minutes;
        this.second = second;
    } // simple constructor


    // use explicit and implicit "this" to call toUniversalString
    public String buildString()
    {
        return String.format("%24s: %s\n%24s: %s", "this.UniversalString()", this.toUniversalString(),
                "toUniversalString()", toUniversalString());
    }


    // "this" is not required here to access instance variable,
    // because method does not have local variables with same
    // names as instance variables
    public String toUniversalString()
    {
        return String.format("%02d:%02d:%02d",
                this.hour, this.minutes, this.second);
    } // end toUniversalString
}
