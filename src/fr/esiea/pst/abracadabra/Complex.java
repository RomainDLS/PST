package fr.esiea.pst.abracadabra;

public class Complex {
  private final double re;
  private final double im;

  public Complex(double re, double im) {
    this.re = re;
    this.im = im;
  }

  public double re() {
    return re;
  }

  public double im() {
    return im;
  }

  public double abs() {
    return Math.hypot(re, im); // magnitude
  }

  @Override
  public String toString() {
    return re + " + " + im + "i";
  }
}