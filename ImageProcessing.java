import java.awt.image.BufferedImage;  // Describing an Image with an accessible buffer of image data
import java.io.File;  // User interfaces and operating systems use system-dependent pathname strings to name files and directories.
import java.io.IOException;  // Signals that an I/O exception of some sort has occurred.
import javax.imageio.ImageIO;  // Locating ImageReaders and ImageWriters, and performing simple encoding and decoding
import java.util.Arrays;  // This class contains various methods for manipulating arrays (such as sorting and searching).
import java.util.Random;  // An instance of this class is used to generate a stream of pseudorandom numbers.

public class ImageProcessing {
	// Gray Scale
	public static int[] GrayScale(BufferedImage img_orgi, int h, int w) {
		/*
			public int[] getRGB(int startX,
					int startY,
					int w,
					int h,
					int[] rgbArray,
					int offset,
					int scansize)

			@param startX - the starting X coordinate
			@param startY - the starting Y coordinate
			@param w - width of region
			@param h - height of region
			@param rgbArray - if not null, the rgb pixels are written here
			@param offset - offset into the rgbArray
			@param scansize - scanline stride for the rgbArray
		*/
		int[] pixel = new int[h * w];  // Array of the image
		img_orgi.getRGB(0, 0, w, h, pixel, 0, 512);
		// Mean-value method
		for(int i = 0; i < w * h; i++) {
			int c = pixel[i];  // All of the color of image to be processed
			// Value to RGB
			// 0xff is a hexadecimal number of decimal number 255
			int a = (c >> 24) & 0xff;  // alpha
			int r = (c >> 16) & 0xff;  // red
			int g = (c >> 8) & 0xff;  // green
			int b = (c >> 0) & 0xff;  // blue
			
			// Calculating the Average
			int c_new = (r + g + b) / 3;  // Mean-value method
			
			// RGB to Value
			pixel[i] = (a << 24) | (c_new << 16) | (c_new << 8) | c_new;
		}
		try {	
			BufferedImage img_grayscale = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
			img_grayscale.setRGB(0, 0, w, h, pixel, 0, 512);
			File out_f = new File("lenna-grayscale.bmp");
			ImageIO.write(img_grayscale, "bmp", out_f);
			System.out.println("Gray scale: Done!");
			System.out.println("----------------");
		}
		catch (Exception e)
		{
			System.out.println("An error " + e + " occurred while processing the image!");
		}
		return pixel;
	}
	// Negative
	public static int[] Negative(BufferedImage img_gs, int h, int w) {
		int[] pixel = new int[h * w];  // Array of the image
		img_gs.getRGB(0, 0, w, h, pixel, 0, 512);
		// Negative method
		for(int i = 0; i < w * h; i++) {
			int c = pixel[i];  // All of the color of image to be processed
			// Value to RGB
			int a = (c >> 24) & 0xff;  // alpha
			int r = (c >> 16) & 0xff;  // red
			int g = (c >> 8) & 0xff;  // green
			int b = (c >> 0) & 0xff;  // blue
			
			// Subtracting RGB from 255: An Inversion
			r = 255 - r;
            g = 255 - g;
            b = 255 - b;
			
			// RGB to Value
			pixel[i] = (a << 24 | r << 16 | g << 8 | b);
		}
		try {	
			BufferedImage img_negative = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
			img_negative.setRGB(0, 0, w, h, pixel, 0, 512);
			File out_f = new File("lenna-negative.bmp");
			ImageIO.write(img_negative, "bmp", out_f);
			System.out.println("Negative: Done!");
			System.out.println("----------------");
		}
		catch (Exception e)
		{
			System.out.println("An error " + e + " occurred while processing the image!");
		}
		return pixel;
	}
	// Gamma Correction: Gamma = 1 (Contrast Stretching), Gamma < 1, Gamma > 1
	public static int[] Contrast(BufferedImage img_gs, int h, int w, double gamma, String n) {
		// Setting the lower and upper bounds for each pixel of the image
		int a_min = 255, r_min = 255, g_min = 255, b_min = 255,
			a_max = 0, r_max = 0, g_max = 0, b_max = 0;
		int[] pixel = new int[h * w];  // Array of the image
		img_gs.getRGB(0, 0, w, h, pixel, 0, 512);
		// Gamma Correction method
		for(int i = 0; i < w * h; i++) {
			int c = pixel[i];  // All of the color of image to be processed
			// Value to RGB
			int a = (c >> 24) & 0xff;  // alpha
			int r = (c >> 16) & 0xff;  // red
			int g = (c >> 8) & 0xff;  // green
			int b = (c >> 0) & 0xff;  // blue
			
			// RGB to Max and Min
			if (a < a_min) {
				a_min = a;
			}
			if (r < r_min) {
				r_min = r;
			}
			if (g < g_min) {
				g_min = g;
			}
			if (b < b_min) {
				b_min = b;
			}
			if (a > a_max) {
				a_max = a;
			}
			if (r > r_max) {
				r_max = r;
			}
			if (g > g_max) {
				g_max = g;
			}
			if (b > b_max) {
				b_max = b;
			}
		}
		// Normalization
		float a_rto = a_max - a_min, r_rto = r_max - r_min, g_rto = g_max - g_min, b_rto = r_max - r_min;
		for(int i = 0; i < w * h; i++) {
			int c = pixel[i];  // All of the color of image to be processed
			// Value to RGB
			int a = (c >> 24) & 0xff;  // alpha
			int r = (c >> 16) & 0xff;  // red
			int g = (c >> 8) & 0xff;  // green
			int b = (c >> 0) & 0xff;  // blue
			
			// Calculating new RGB using the gamma correction formula
			a = Math.round(((a - a_min) / a_rto) * 255);
			r = Math.round(((r - r_min) / r_rto) * 255);
			g = Math.round(((g - g_min) / g_rto) * 255);
			b = Math.round(((b - b_min) / b_rto) * 255);
			
			float a_new = a / 255f;
			float r_new = r / 255f;
			float g_new = g / 255f;
			float b_new = b / 255f;
			
			double a_new_double = Math.pow((double)a_new, gamma) * 255;
			double r_new_double = Math.pow((double)r_new, gamma) * 255;
			double g_new_double = Math.pow((double)g_new, gamma) * 255;
			double b_new_double = Math.pow((double)b_new, gamma) * 255;
			
			int a_gamma_int = (int)a_new_double;
			int r_gamma_int = (int)r_new_double;
			int g_gamma_int = (int)g_new_double;
			int b_gamma_int = (int)b_new_double;
			
			pixel[i] = (a_gamma_int << 24 | r_gamma_int << 16 | g_gamma_int << 8 | b_gamma_int);
		}
		try {
			System.out.println("Gamma = " + gamma);
			img_gs.setRGB(0, 0, w, h, pixel, 0, 512);
			File out_f = new File("lenna-gamma"+ n +".bmp");
			ImageIO.write(img_gs, "bmp", out_f);
			System.out.println("Gamma Correction: Done!");
			System.out.println("----------------");
		}
		catch (Exception e)
		{
			System.out.println("An error " + e + " occurred while processing the image!");
		}
		return pixel;
	}
	// Salt-and-Pepper Noise
	public static int[] SaltnPepper(BufferedImage img_g_b, int h, int w, float rt) {
		int[] pixel = new int[h * w];  // Array of the image
		img_g_b.getRGB(0, 0, w, h, pixel, 0, 512);
		// Add Salt-and-Pepper Noise
		Random random = new Random();
		for(int i = 0; i < w * h; i++) {
			int c = pixel[i];  // All of the color of image to be processed
			// Value to RGB
			int a = (c >> 24) & 0xff;  // alpha
			int r = (c >> 16) & 0xff;  // red
			int g = (c >> 8) & 0xff;  // green
			int b = (c >> 0) & 0xff;  // blue
			
			if(random.nextFloat() > rt) {
				// 50% Black and 50% white
				if(random.nextFloat() > 0.5f) {
					r = 255;
					g = 255;
					b = 255;
				}
				else {
					r = 0;
					g = 0;
					b = 0;
				}
			}
			pixel[i] = (a << 24 | r << 16 | g << 8 | b);
		}
		try {
			img_g_b.setRGB(0, 0, w, h, pixel, 0, 512);
			File out_f = new File("lenna-saltnpepper.bmp");
			ImageIO.write(img_g_b, "bmp", out_f);
			System.out.println("Salt-and-pepper noise: Done!");
			System.out.println("----------------");
		}
		catch (Exception e)
		{
			System.out.println("An error " + e + " occurred while processing the image!");
		}
		return pixel;
	}
	// 3X3 Median Filter
	public static int[][] Median(BufferedImage img_m, int h, int w) {
		int[][] pixel = new int[h][w];  // Array of the image
		for (int i = 0; i < h; i++) {
			for (int j = 0; j < w; j++) {
				pixel[i][j] = img_m.getRGB(i, j);
			}
		}
		int[][] pixel_m = new int[h][w];  // Array of the new image
		for (int i = 1; i < h - 1; i++) {
			for (int j = 1; j < w - 1; j++) {
				int[] num = new int[9];
				int k = 0;
				for (int x = -1; x <= 1; x++) {
					for (int y = -1; y <= 1; y++) {
						num[k++] = pixel[i + x][j + y];
					}
				}
				Arrays.sort(num);  // Sorting elements of the array
				pixel_m[i][j] = num[4];  // Assigning the median to the array of the new image
			}
		}
		// Pixel to image
		for (int i = 0; i < h; i++) {
			for (int j = 0; j < w; j++) {
				img_m.setRGB(i, j, pixel_m[i][j]);
			}
		}
		try {
			File out_f = new File("lenna-median.bmp");
			ImageIO.write(img_m, "bmp", out_f);
			System.out.println("3X3 Median filter: Done!");
			System.out.println("----------------");
		}
		catch (Exception e)
		{
			System.out.println("An error " + e + " occurred while processing the image!");
		}
		return pixel_m;
	}
	// Laplacian Operator
	public static int[][] Laplacian(BufferedImage img_lpo, int h, int w) {
		int[][] pixel = new int[h][w];  // Array of the original image
		for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
				// Value to RGB
                int c = img_lpo.getRGB(x, y);
				pixel[y][x] = c;
			}
		}
		// Applying Laplacian Operator
		int[][] lpo = {{0, -1, 0}, {-1, 4, -1}, {0, -1, 0}};  // Laplacian Operator
		int[][] pixel_proc = new int[h][w];  // Array of the processed image
		for (int x = 1; x < w - 1; x++) {
			for (int y = 1; y < h - 1; y++) {
				int sum = 0;
				for (int i = -1; i <= 1; i++) {
					for (int j = -1; j <= 1; j++) {
						sum += pixel[x + i][y + j] * lpo[i + 1][j + 1];
					}
				}
				pixel_proc[x][y] = Math.abs(sum);  // Taking the absolute value
			}
		}
		// Pixel to image
		int[] pixel_new = new int[w * h];
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				int c_new = pixel_proc[y][x];
				pixel_new[y * w + x] = (c_new << 24) | (c_new << 16) | (c_new << 8) | c_new;
			}
		}
		try {
			File out_f = new File("lenna-laplacian.bmp");
			img_lpo.setRGB(0, 0, w, h, pixel_new, 0, 512);
			ImageIO.write(img_lpo, "bmp", out_f);
			System.out.println("Laplacian filter: Done!");
			System.out.println("----------------");
		}
		catch (Exception e)
		{
			System.out.println("An error " + e + " occurred while processing the image!");
		}
		return pixel_proc;
	}
	// 3X3 Maximum filter
	public static int[][] Max(BufferedImage img_mx, int h, int w) {
		int[][] pixel = new int[h][w];  // Array of the image
		for (int i = 0; i < h; i++) {
			for (int j = 0; j < w; j++) {
				pixel[i][j] = img_mx.getRGB(i, j);
			}
		}
		int[][] pixel_new = new int[h][w];  // Array of the new image
		for (int i = 1; i < h - 1; i++) {
			for (int j = 1; j < w - 1; j++) {
				int[] num = new int[9];
				int k = 0;
				for (int x = -1; x <= 1; x++) {
					for (int y = -1; y <= 1; y++) {
						num[k++] = pixel[i + x][j + y];
					}
				}
				Arrays.sort(num);  // Sorting elements of the array
				pixel_new[i][j] = num[num.length - 1];  // Assigning the maximum to the array of the new image
			}
		}
		// Pixel to image
		for (int i = 0; i < h; i++) {
			for (int j = 0; j < w; j++) {
				img_mx.setRGB(i, j, pixel_new[i][j]);
			}
		}
		try {
			File out_f = new File("lenna-max.bmp");
			ImageIO.write(img_mx, "bmp", out_f);
			System.out.println("3X3 Maximum filter: Done!");
			System.out.println("----------------");
		}
		catch (Exception e)
		{
			System.out.println("An error " + e + " occurred while processing the image!");
		}
		return pixel_new;
	}
	// Otsu
	public static int[] Otsu(BufferedImage img_gc_ot, int h, int w) {
		int[] pixel = new int[h * w];  // Array of the image
		img_gc_ot.getRGB(0, 0, w, h, pixel, 0, 512);
		// Value to RGB
		int sum = 0;
		for(int i = 0; i < w * h; i++) {
			int c = (pixel[i] >> 0) & 0xff;
			sum += c;
		}
		
		// Calculating threshold using mean-value
		int threshold = sum / (w * h);
		
		// Otsu method
		for (int i = 0; i < w * h; i++) {
			int c = (pixel[i] >> 0) & 0xff;
			if (c > threshold) {
				pixel[i] = 0xffffffff;  // To white
			} else {
				pixel[i] = 0xff000000;  // To black
			}
    }
		try {
			System.out.println("Threshold of Otsu = " + threshold);
			img_gc_ot.setRGB(0, 0, w, h, pixel, 0, 512);
			File out_f = new File("lenna-otsu.bmp");
			ImageIO.write(img_gc_ot, "bmp", out_f);
			System.out.println("Otsu filter: Done!");
			System.out.println("----------------");
		}
		catch (Exception e)
		{
			System.out.println("An error " + e + " occurred while processing the image!");
		}
		return pixel;
	}
	
	// Main
	public static void main(String args[]) throws Exception {
		// Orgi to GrayScale
		File input_f = new File("lenna-orgi.bmp");
		BufferedImage img_orgi = ImageIO.read(input_f);
		int h = img_orgi.getHeight();
		int w = img_orgi.getWidth();
		int[] img_grayscale = new int[h * w];  // Array of the image
		img_grayscale = GrayScale(img_orgi, h, w);  // Invoking the function of Gray scale
		
		// GrayScale to Negative
		File input_f_grayscale = new File("lenna-grayscale.bmp");
		BufferedImage img_gs = ImageIO.read(input_f_grayscale);
		int[] img_negative = new int[h * w];  // Array of the image
		img_negative = Negative(img_gs, h, w);  // Invoking the function of Negative
		
		// Gamma Correction
		int[] img_gamma_cs = new int[h * w];  // Gamma = 1
		int[] img_gamma_abv = new int[h * w];  // Gamma > 1
		int[] img_gamma_bel = new int[h * w];  // Gamma < 1
		img_gamma_cs = Contrast(img_gs, h, w, 1.0, "-cs");  // Invoking the function of Gamma Correction which Gamma = 1
		img_gamma_abv = Contrast(img_gs, h, w, 2.0, "-abv");  // Invoking the function of Gamma Correction which Gamma > 1
		img_gamma_bel = Contrast(img_gs, h, w, 0.2, "-bel");  // Invoking the function of Gamma Correction which Gamma < 1
		
		// Salt-and-Pepper Noise
		File input_f_gamma_bel = new File("lenna-gamma-bel.bmp");
		BufferedImage img_g_b = ImageIO.read(input_f_gamma_bel);
		int[] img_saltnpepper = new int[h * w];
		img_saltnpepper = SaltnPepper(img_g_b, h, w, 0.8f);  // Invoking the function of Salt-and-pepper noise
		
		// 3X3 Median Filter
		File input_f_median = new File("lenna-saltnpepper.bmp");
		BufferedImage img_m = ImageIO.read(input_f_median);
		int[][] img_median = new int[h][w];
		img_median = Median(img_m, h, w);  // Invoking the function of Median filter
		
		// Laplacian Operator (Laplacian Filter)
		File input_f_lpo_temp = new File("lenna-gamma-cs.bmp");
		BufferedImage img_lpo = ImageIO.read(input_f_lpo_temp);
		int[][] img_laplacian = new int[h][w];
		img_laplacian = Laplacian(img_lpo, h, w);  // Invoking the function of Laplacian operator (Laplacian filter)
		
		// 3X3 Maximum Filter
		File input_f_max = new File("lenna-laplacian.bmp");
		BufferedImage img_mx = ImageIO.read(input_f_max);
		int[][] img_max = new int[h][w];
		img_max = Max(img_mx, h, w);  // Invoking the function of Max filter
		
		// Otsu
		File input_f_gamma_abv_otsu = new File("lenna-gamma-abv.bmp");
		BufferedImage img_gc_ot = ImageIO.read(input_f_gamma_abv_otsu);
		int[] img_otsu = new int[h * w];  // Array of the image
		img_otsu = Otsu(img_gc_ot, h, w);  // Invoking the function of Otsu
	}
}