public class Rectangle {

    private double length;
    private double width;

    public Rectangle() {
        length = 1.0;
        width = 1.0;
    }

    public Rectangle(double l, double w)
    {
        setLength(l);
        setWidth(w);
    }
    public void setLength(double l )
    {
        if (l > 0 && l < 20 )
            length = l;
        else
            throw new IllegalArgumentException("Length must be 0 and 20");
    }

    public void setWidth(double w)
    {
        if (w > 0 && w < 20 )
            width = w;
        else
            throw new IllegalArgumentException("Width must be between 0 and 20");
    }

    public double getLength()
    {
        return length;
    }

    public double getWidth()
    {
        return width;
    }

    public double getPerimeter()
    {
        return (2 * width)+ (2 * length);
    }


    public double getArea()
    {
        return length * width;
    }

}
