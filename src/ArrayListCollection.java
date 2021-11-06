import java.util.ArrayList;

public class ArrayListCollection {
    public static void main(String[] args) {
        ArrayList < String > items = new ArrayList < String > ();

        items.add( "red" ); // append an item to the list
        items.add(0, "yellow"); // insert the value at the index 0

        // header
        System.out.print("Display list contents with counter-controlled loop: ");

        // display the color in the list

        for (int i = 0; i < items.size(); i++)
            System.out.printf("  %s", items.get( i ) );

        display(items, "Remove second list element (green):");

        items.add("green");   // add "green" to the end of the list
        items.add("yellow");  // add  "yellow" to the end of the list

        display(items, "List with two new elements: " );

        items.remove("yellow");  // remove first "yellow"
        display(items, "Remove first instance of yellow: ");

        items.remove(1); // remove item at index
        display(items, "Remove second list element (green):");

        // check if a value is in the List
        System.out.printf( "\"red\" is %sin the list\n", items.contains("red") ? "": "not");

        System.out.printf("Size: %s\n", items.size());




    }



    public static void display(ArrayList < String > items, String header){
        System.out.print(header);
        for (String item : items)
            System.out.printf(" %S", item);
    } // end method display
}
