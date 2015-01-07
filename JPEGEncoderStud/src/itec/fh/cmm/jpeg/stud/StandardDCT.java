package itec.fh.cmm.jpeg.stud;

import itec.fh.cmm.jpeg.impl.DCTBlock;
import itec.fh.cmm.jpeg.spec.BlockI;
import itec.fh.cmm.jpeg.spec.DCTBlockI;
import itec.fh.cmm.jpeg.spec.DCTI;

/**
 * Pattern implementation of interface DCTI. Uses the standard 2D DCT algorithm.
 * 
 * @author Roland Tusch
 * @author Christian Timmerer
 * @version 1.0
 */

public class StandardDCT implements DCTI {
	
	private static final double ONEOVERSQRT2 = 1d/Math.sqrt(2d);

	/**
	 * Performs a forward DCT (also known as FDCT) on the given block of data.
	 * 
	 * @param the
	 *            component block to transform into the frequency domain
	 * @return a new data block containing the corresponding DCT coefficients
	 *         for the given block. The returned block is not of type BlockI,
	 *         since the DCT coefficients are values of type double, not int.
	 */
	public DCTBlockI forward(BlockI b) {
		int[][] blockData = b.getData();
		int blockSize = blockData[0].length;
		double[][] dctCoeff = new double[blockSize][blockSize];

		levelShift(blockData);

		for (int u = 0; u < blockSize; u++) {
			for (int v = 0; v < blockSize; v++) {
				double sum = 0;
				for (int i = 0; i < blockSize; i++) {
					for (int j = 0; j < blockSize; j++) {
						sum += blockData[i][j]
								* Math.cos((2 * i + 1) * u * Math.PI / 16)
								* Math.cos((2 * j + 1) * v * Math.PI / 16);
					}
				}
				dctCoeff[u][v] = ((C(u) * C(v)) / 4) * sum;
			}
		}

		return new DCTBlock(dctCoeff);
	}

	private double C(int i) {
		return (i == 0 ? ONEOVERSQRT2 : 1);
	}

	/**
	 * Performs a level shift of the block samples to a signed representation.
	 */
	protected void levelShift(int[][] blockData) {
		for (int i = 0; i < blockData.length; i++) {
			for (int j = 0; j < blockData[i].length; j++) {
				blockData[i][j] -= 128;
			}
		}
	}
}