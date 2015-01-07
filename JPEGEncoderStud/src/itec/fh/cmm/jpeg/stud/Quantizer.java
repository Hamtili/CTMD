package itec.fh.cmm.jpeg.stud;

import itec.fh.cmm.jpeg.impl.Block;
import itec.fh.cmm.jpeg.spec.BlockI;
import itec.fh.cmm.jpeg.spec.DCTBlockI;
import itec.fh.cmm.jpeg.spec.QuantizationI;
import itec.fh.cmm.jpeg.spec.YUVImageI;

/**
 * Full pattern implementation of interface QuantizationI.
 * 
 * @author Roland Tusch
 * @version 1.0
 */

public class Quantizer implements QuantizationI {

	int qualityScalingFactor;
	int[] scaledQuantumLuminance;
	int[] scaledQuantumChrominance;

	/**
	 * Creates a new quantizer using the default quality factor.
	 */
	public Quantizer() {
		this(DEFAULT_QUALITY_FACTOR);
	}

	/**
	 * Creates a new quantizer using the given quality factor. The given factor
	 * is transformed to a scaling factor, which is used for scaling
	 * quantization coefficients.
	 * 
	 * @param qualityFactor
	 *            the quality factor to use for scaling quantization
	 *            coefficients. Must be a value within [1 .. 100]. If this is
	 *            not the case, the given value is automatically rounded to its
	 *            nearest value within this interval.
	 */
	public Quantizer(int qualityFactor) {
		scaledQuantumLuminance = new int[QUANTUM_LUMINANCE.length];
		scaledQuantumChrominance = new int[QUANTUM_CHROMINANCE.length];
		setQualityFactor(qualityFactor);
	}

	public void setQualityFactor(int qualityFactor) {
		qualityScalingFactor = scaleQuality(qualityFactor);
		scaleQuantizationValues();
	}

	/**
	 * Calculates the quality scaling factor from the given quality factor. The
	 * quality scaling factor is used for scaling quantization coefficients
	 * during block quantization.
	 * 
	 * @param qualityFactor
	 *            the quality factor to use for computing the scaling factor
	 */
	protected int scaleQuality(int qualityFactor) {
		qualityFactor = Math.min(100, Math.max(1, qualityFactor));
		return (qualityFactor < 50 ? 5000 / qualityFactor
				: 200 - 2 * qualityFactor);
	}

	/**
	 * Computes the scaled quantization values in order to increase overall
	 * quantization performance.
	 */
	protected void scaleQuantizationValues() {
		for (int i = 0; i < 64; i++) {
			scaledQuantumLuminance[i] = Math.min(255, Math.max(1,
					(QUANTUM_LUMINANCE[i] * qualityScalingFactor + 50) / 100));
			scaledQuantumChrominance[i] = Math
					.min(255,
							Math.max(1, (QUANTUM_CHROMINANCE[i]
									* qualityScalingFactor + 50) / 100));
		}
	}

	public BlockI quantizeBlock(DCTBlockI dctBlock, int compType) {
		if (compType == YUVImageI.Y)
			return quantizeBlock(dctBlock, scaledQuantumLuminance);
		// quantize chrominance block
		return quantizeBlock(dctBlock, scaledQuantumChrominance);
	}

	protected BlockI quantizeBlock(DCTBlockI dctBlock, int[] quantum) {
		double[][] dctCoeff = dctBlock.getData();
		int blockSize = dctCoeff[0].length;
		int[][] quantBlock = new int[blockSize][blockSize];

		for (int i = 0; i < blockSize; i++) {
			for (int j = 0; j < blockSize; j++) {
				quantBlock[i][j] = (int) (dctCoeff[i][j]
						/ quantum[i * blockSize + j]);
			}
		}

		return new Block(quantBlock);
	}

	public int[] getQuantumLuminance() {
		return scaledQuantumLuminance;
	}

	public int[] getQuantumChrominance() {
		return scaledQuantumChrominance;
	}
}
