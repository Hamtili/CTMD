package itec.fh.cmm.jpeg.stud;

import java.util.ArrayList;

import itec.fh.cmm.jpeg.impl.AbstractHuffmanCoder;
import itec.fh.cmm.jpeg.spec.BlockI;
import itec.fh.cmm.jpeg.spec.RunLevelI;

/**
 * HuffmanCoder class
 */

public class HuffmanCoder extends AbstractHuffmanCoder {

	public RunLevelI[] runLengthEncode(BlockI quantBlock) {
		int[] linearBlock = new int[BlockI.N * BlockI.N];
		int[][] matrixBlock = quantBlock.getData();
		ArrayList<RunLevel> rlcList = new ArrayList<RunLevel>();

		for (int i = 0; i < BlockI.N; i++) {
			for (int j = 0; j < BlockI.N; j++) {
				linearBlock[(i * BlockI.N) + j] = matrixBlock[i][j];
			}
		}

		int run = 0;
		for (int i = 1; i < linearBlock.length; i++) {
			if (linearBlock[ZIGZAG_ORDER[i]] == 0)
				run++;
			else {
				rlcList.add(new RunLevel(run, linearBlock[ZIGZAG_ORDER[i]]));
				run = 0;
			}
		}

		if (run > 0)
			rlcList.add(new RunLevel(0, 0));
		
		return rlcList.toArray(new RunLevelI[rlcList.size()]);
	}
}