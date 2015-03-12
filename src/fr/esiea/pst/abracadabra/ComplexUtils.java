package fr.esiea.pst.abracadabra;



public class ComplexUtils {


  /**
   * Compute max amplitude of the fft sample
   */
  public static double getMaxMagnitude(Complex[][] matrix) {
    double max = 0.0;
    for(int i = 0; i < matrix.length; i++) {
      double amp = getMaxMagnitude(matrix[i]);
      if(amp > max) {
        max = amp;
      }
    }
    return max;
  }

  
  public static double getMaxMagnitude(Complex[] array) {
    double max = 0.0;
    for(int i = 1; i < array.length; i++) { //1 because the first row is not significant (convolutionwise)
      double amp = array[i].abs(); //magnitude of the sound at a given frequency/slice
      if(amp > max) {
        max = amp;
      }
    }
    return max;
  }
}
