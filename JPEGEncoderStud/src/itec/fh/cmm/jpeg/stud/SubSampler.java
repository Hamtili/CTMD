package itec.fh.cmm.jpeg.stud;

import java.awt.Dimension;

import itec.fh.cmm.jpeg.impl.Component;
import itec.fh.cmm.jpeg.impl.YUVImage;
import itec.fh.cmm.jpeg.spec.ComponentI;
import itec.fh.cmm.jpeg.spec.SubSamplerI;
import itec.fh.cmm.jpeg.spec.YUVImageI;

/**
 * Pattern implementation of interface SubSamplerI.
 * 
 * @author Roland Tusch
 * @author Christian Timmerer
 * @version 1.0
 */

public class SubSampler implements SubSamplerI {

	public YUVImageI downSample(YUVImageI yuvImg, int samplingRatio) {
		if (yuvImg.getSamplingRatio() == YUV_444) {
			// image has not been downsampled so far
			switch (samplingRatio) {
			case YUV_444: // nothing to downsample
				return yuvImg;
			case YUV_422:
				return downSample422(yuvImg);
			case YUV_420:
				return downSample420(yuvImg);
			default:
				throw new IllegalArgumentException("Invalid sampling ratio.");
			}
		} else
			throw new IllegalArgumentException(
					"YUV image is already downsampled!");
	}

	protected YUVImageI downSample422(YUVImageI yuvImg) {
		ComponentI Cb = yuvImg.getComponent(YUVImageI.Cb);
		ComponentI Cr = yuvImg.getComponent(YUVImageI.Cr);
		Dimension dim = Cb.getSize();

		int[][] Cbdata = Cb.getData();
		int[][] Crdata = Cr.getData();
		int[][] CbdataReduced = new int[dim.height][dim.width / 2];
		int[][] CrdataReduced = new int[dim.height][dim.width / 2];

		for (int i = 0; i < dim.height; i++) {
			for (int j = 0; j < dim.width; j += 2) {
				CbdataReduced[i][j/2] = Cbdata[i][j];
				CrdataReduced[i][j/2] = Crdata[i][j];
			}
		}
		return (new YUVImage((Component) yuvImg.getComponent(YUVImageI.Y),
				new Component(CbdataReduced, YUVImageI.Cb), new Component(
						CrdataReduced, YUVImageI.Cr), YUV_422));
	}

	protected YUVImageI downSample420(YUVImageI yuvImg) {
		ComponentI Cb = yuvImg.getComponent(YUVImageI.Cb);
		ComponentI Cr = yuvImg.getComponent(YUVImageI.Cr);
		Dimension dim = Cb.getSize();

		int[][] Cbdata = Cb.getData();
		int[][] Crdata = Cr.getData();
		int[][] CbdataReduced = new int[dim.height / 2][dim.width / 2];
		int[][] CrdataReduced = new int[dim.height / 2][dim.width / 2];

		for (int i = 0; i < dim.height; i++) {
			for (int j = 0; j < dim.width; j++) {
				CbdataReduced[i/2][j/2] += Cbdata[i][j];
				CrdataReduced[i/2][j/2] += Crdata[i][j];
			}	
		}
		
		for (int i = 0; i< dim.height/2;i++) {
			for (int j = 0; j<dim.width/2;j++) {
				CbdataReduced[i][j] = CbdataReduced[i][j]/4;
				CrdataReduced[i][j] = CrdataReduced[i][j]/4;
			}
		}

		return (new YUVImage((Component) yuvImg.getComponent(YUVImageI.Y),
				new Component(CbdataReduced, YUVImageI.Cb), new Component(
						CrdataReduced, YUVImageI.Cr), YUV_420));
	}
}
