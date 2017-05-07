package com.cv4j.core.binary;

import com.cv4j.core.datamodel.ByteProcessor;
import com.cv4j.core.datamodel.Line;

import java.util.List;

public class HoughLinesP {
	private double[] coslut;
	private double[] sinlut;
	private int accSize;
	private int width;
	private int height;
	private int accThreshold;

	public HoughLinesP(){
		setupCosLUT();
		setupSinLUT();
	}

	/**
	 * 1. 初始化霍夫变换空间
	 * 2. 将图像的2D空间转换到霍夫空间,每个像素坐标都要转换到霍夫极坐标的对应强度值
	 * 3. 找出霍夫极坐标空间的最大强度值
	 * 4. 根据最大强度值归一化,范围为0 ~ 255
	 * 5. 根据输入前accSize值,画出前accSize个信号最强的直线
	 * @return
	 */
	public void process(ByteProcessor binary, int accSize, int minGap, int minAcc, List<Line> lines) {
		this.width = binary.getWidth();
		this.height = binary.getHeight();
		this.accSize = accSize; // 前K=accSize个累积值
		this.accThreshold = minAcc;// 最小累积值
		int rmax = (int) Math.sqrt(width * width + height * height);
		int[] acc = new int[rmax * 180]; // 0 ~ 180角度范围
		int r;
		byte[] input = binary.getGray();

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {

				if ((input[y * width + x] & 0xff) == 255) {

					for (int theta = 0; theta < 180; theta++) {
						r = (int) (x * coslut[theta] + y * sinlut[theta]); // 计算出极坐标
						if ((r > 0) && (r <= rmax))
							acc[r * 180 + theta] = acc[r * 180 + theta] + 1; // 在斜率范围内的点，极坐标相同
					}
				}
			}
		}

		// 寻找最大值
		int max = 0;
		for (r = 0; r < rmax; r++) {
			for (int theta = 0; theta < 180; theta++) {

				if (acc[r * 180 + theta] > max) {
					// swap the max value
					max = acc[r * 180 + theta];
				}
			}
		}

		// normalization all the values,
		int value;
		for (r = 0; r < rmax; r++) {
			for (int theta = 0; theta < 180; theta++) {

				value = (int) (((double) acc[r * 180 + theta] / (double) max) * 255.0);
				acc[r * 180 + theta] = 0xff000000 | (value << 16 | value << 8 | value);
			}
		}

		// 发现前N个信号最强的点，转换为平面坐标，得到直线
		findMaxima(acc, lines);

		// filter by min gap
		// TODO: zhigaang


	}

	private int[] findMaxima(int[] acc, List<Line> lines) {

		// 初始化
		int rmax = (int) Math.sqrt(width * width + height * height);
		int[] results = new int[accSize * 3];
		int[] output = new int[width * height];
		// 开始寻找前N个最强信号点，记录极坐标坐标位置
		for (int r = 0; r < rmax; r++) {
			for (int theta = 0; theta < 180; theta++) {
				int value = (acc[r * 180 + theta] & 0xff);

				// if its higher than lowest value add it and then sort
				if (value > results[(accSize - 1) * 3]) {

					// add to bottom of array
					results[(accSize - 1) * 3] = value;
					results[(accSize - 1) * 3 + 1] = r;
					results[(accSize - 1) * 3 + 2] = theta;

					// shift up until its in right place
					int i = (accSize - 2) * 3;
					while ((i >= 0) && (results[i + 3] > results[i])) {
						for (int j = 0; j < 3; j++) {
							int temp = results[i + j];
							results[i + j] = results[i + 3 + j];
							results[i + 3 + j] = temp;
						}
						i = i - 3;
						if (i < 0)
							break;
					}
				}
			}
		}


		// 绘制像素坐标
		System.out.println("Total " + accSize + " matches:");
		for (int i = accSize - 1; i >= 0; i--) {
			Line line = drawPolarLine(results[i * 3], results[i * 3 + 1],results[i * 3 + 2]);
			lines.add(line);
		}
		return output;
	}

	// 变换极坐标为平面坐标，并绘制
	private Line drawPolarLine(int value, int r, int theta) {
		int x1=100000, y1=0, x2=0, y2=0;
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				int temp = (int) (x * coslut[theta] + y * sinlut[theta]);
				if ((temp - r) == 0) {// 变换坐标并绘制
					if(x > x2) {
						x2 = x;
						y2 = y;
					}
					if(x < x1) {
						x1 = x;
						y1 = y;
					}
				}
			}
		}
		/*System.out.println(" [ x1 = " + x1 + " y1 = " + y1 + " ] ");
		System.out.println(" [ x2 = " + x2 + " y2 = " + y2 + " ] ");
		System.out.println();*/
		return new Line(x1, y1, x2, y2);
	}

	private double[] setupCosLUT() {
		coslut = new double[180];
		for (int theta = 0; theta < 180; theta++) {
			coslut[theta] = Math.cos((theta * Math.PI) / 180.0);
		}
		return coslut;
	}

	private double[] setupSinLUT() {
		sinlut = new double[180];
		for (int theta = 0; theta < 180; theta++) {
			sinlut[theta] = Math.sin((theta * Math.PI) / 180.0);
		}
		return sinlut;
	}
}
