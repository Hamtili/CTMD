package itec.fh.cmm.jpeg.stud;

import itec.fh.cmm.jpeg.impl.Component;
import itec.fh.cmm.jpeg.impl.YUVImage;
import itec.fh.cmm.jpeg.spec.ColorSpaceConverterI;
import itec.fh.cmm.jpeg.spec.SubSamplerI;
import itec.fh.cmm.jpeg.spec.YUVImageI;

import java.awt.Image;
import java.awt.image.PixelGrabber;

/**
 * Pattern implementation of interface ColorSpaceConverterI.
 * 
 * @author Roland Tusch
 * @version 1.0
 */

public class ColorSpaceConverter implements ColorSpaceConverterI {

	int imgWidth = 0;
	int imgHeight = 0;
	int[] imgData = null;

	public YUVImageI convertRGBToYUV(Image rgbImg) {
		try {
			imgWidth = rgbImg.getWidth(null);
			imgHeight = rgbImg.getHeight(null);
			imgData = new int[imgWidth * imgHeight];
			PixelGrabber grabber = new PixelGrabber(rgbImg, 0, 0, imgWidth,
					imgHeight, imgData, 0, imgWidth);
			if (grabber.grabPixels())
				return transformRGBtoYUV();
		} catch (Exception e) {
		}
		return null;
	}

	protected YUVImageI transformRGBtoYUV() {
		int[][] Y = new int[imgHeight][imgWidth];
		int[][] Cb = new int[imgHeight][imgWidth];
		int[][] Cr = new int[imgHeight][imgWidth];

		for (int i = 0; i < imgHeight; i++) {
			for (int j = 0; j < imgWidth; j++) {
				int blue = imgData[(i * imgWidth) + j] & 0xFF;
				int green = (imgData[(i * imgWidth) + j] >> 8) & 0xFF;
				int red = (imgData[(i * imgWidth) + j] >> 16) & 0xFF;

				Y[i][j] = (int) (0.299 * red + 0.587 * green + 0.114 * blue);
				Cb[i][j] = 128 + (int) (-0.16874 * red - 0.33126 * green + 0.5 * blue);
				Cr[i][j] = 128 + (int) (+0.5 * red - 0.41869 * green - 0.08131 * blue);

			}
		}
		
		return new YUVImage(new Component(Y, YUVImageI.Y), new Component(Cb,
				YUVImageI.Cb), new Component(Cr, YUVImageI.Cr),
				SubSamplerI.YUV_444);
	}
}
