package com.cv4j.core.binary;

import com.cv4j.core.datamodel.ByteProcessor;
import com.cv4j.core.datamodel.Line;

import java.util.List;

public class HoughLinesP {
	/**
	 * 1. 初始化霍夫变换空间
	 * 2. 将图像的2D空间转换到霍夫空间,每个像素坐标都要转换到霍夫极坐标的对应强度值
	 * 3. 找出霍夫极坐标空间的最大强度值
	 * 4. 根据最大强度值归一化,范围为0 ~ 255
	 * 5. 根据输入前accSize值,画出前accSize个信号最强的直线
	 * @return
	 */
	public void process(ByteProcessor binary, List<Line> lines, int numLines, int accThreshold, boolean filterByThreshold) {
		int width = binary.getWidth();
		int height = binary.getHeight();
		int rmax = (int) Math.sqrt(width * width + height * height);
		int[] acc = new int[rmax * 180]; // 0 ~ 180角度范围
		int r;
		byte[] input = binary.getGray();

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {

				if ((input[y * width + x] & 0xff) == 255) {

					for (int theta = 0; theta < 180; theta++) {
						r = (int) (x * Math.cos(((theta) * Math.PI) / 180) + y * Math.sin(((theta) * Math.PI) / 180)); // 计算出极坐标
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
		findMaxima(acc, lines, width, height, numLines);
	}

	private void findMaxima(int[] acc, List<Line> lines, int width, int height, int numLines) {

		// 初始化
		int rmax = (int) Math.sqrt(width * width + height * height);
		int[] results = new int[numLines * 3];
		// 开始寻找前N个最强信号点，记录极坐标坐标位置
		for (int r = 0; r < rmax; r++) {
			for (int theta = 0; theta < 180; theta++) {
				int value = (acc[r * 180 + theta] & 0xff);

				// if its higher than lowest value add it and then sort
				if (value > results[(numLines - 1) * 3]) {

					// add to bottom of array
					results[(numLines - 1) * 3] = value;
					results[(numLines - 1) * 3 + 1] = r;
					results[(numLines - 1) * 3 + 2] = theta;

					// shift up until its in right place
					int i = (numLines - 2) * 3;
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
		//System.out.println("Total " + numLines + " matches:");
		for (int i = numLines - 1; i >= 0; i--) {
			drawPolarLine(results[i * 3], results[i * 3 + 1],results[i * 3 + 2], lines, width, height);
		}
	}

	// 变换极坐标为平面坐标，并绘制
	private void drawPolarLine(int value, int r, int theta, List<Line> lines, int width, int height) {
		int x1 = 10000, y1 = 100000;
		int x2 = 0, y2 = 0;
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				int temp = (int) (x * Math.cos(((theta) * Math.PI) / 180) + y * Math.sin(((theta) * Math.PI) / 180));
				if ((temp - r) == 0) {// 变换坐标并绘制
					if(x1 > x && y1 > y) {
						x1 = x;
						y1 = y;
					}
					if(x2 < x && y2 < y) {
						x2 = x;
						y2 = y;
					}
				}
			}
		}
		Line line = new Line();
		line.x1 = x1;
		line.y1 = y1;
		line.x2 = x2;
		line.y2 = y2;
		lines.add(line);
	}
}
