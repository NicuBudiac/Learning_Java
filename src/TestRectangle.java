public class TestRectangle {
    public static void main(String[] args) {
        Rectangle rectangle = new Rectangle(19.1, 4);

        System.out.printf("Length of the rectangle is: %.2f\n", rectangle.getLength() );
        System.out.printf("Width of the rectangle is: %.2f\n", rectangle.getWidth() );

        System.out.printf("Perimeter of rectangle is: %.2f\n", rectangle.getPerimeter() );
        System.out.printf("Area of rectangle is: %.2f\n", rectangle.getArea() );

    }
}
