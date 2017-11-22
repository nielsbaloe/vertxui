package live.connector.vertxui.samples.client.energyCalculator;

public class Surface {

	public double sizeX;

	public double sizeY;

	public double lambda = defaultLambda;

	/**
	 * thickness in meter
	 */
	public double thickness;

	public static double defaultLambda = 0.039;

	public double getR() {
		return thickness / lambda;
	}

	public double getU() {
		return 1.0 / getR();
	}

	public double getM2() {
		return sizeX * sizeY;
	}

}
