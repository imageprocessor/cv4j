package com.cv4j.core.binary;

import com.cv4j.core.datamodel.ByteProcessor;
import com.cv4j.core.datamodel.Rect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/***
 * it is very easy way to filter some minimum noise block by number of pixel;
 * default settings
 * - numOfPixels 100
 * - filterNoise false
 */
public class ConnectedAreaLabel {

	private int numOfPixels;
	private boolean filterNoise;

	public ConnectedAreaLabel() {
		numOfPixels = 100;
		filterNoise = false;
	}

	public void setNoiseArea(int numOfPixels) {
		this.numOfPixels = numOfPixels;
	}

	public void setFilterNoise(boolean filterNoise) {
		this.filterNoise = filterNoise;
	}

	public int process(ByteProcessor binary, byte[] labelMask, List<Rect> rectangles, boolean drawBounding) {
		int width = binary.getWidth();
		int height = binary.getHeight();
		byte[] data = binary.getGray();
		int p1, p2, p3;
		int ymin = 1;
		int xmin = 1;
		int offset = 0;
		int[] labels = new int[(width * height)/2];
		Arrays.fill(labels, -1);
		int[] pixels = new int[width * height];
		Arrays.fill(pixels, -1);
		int ul=-1, ll = -1;
		int currlabel = 0;
		int[] twoLabels = new int[2];
		for (int row = ymin; row < height; row++) {
			offset = row * width + xmin;
			for (int col = xmin; col < width; col++) {
				p1 = data[offset]&0xff;
				p2 = data[offset-1]&0xff; // left
				p3 = data[offset-width]&0xff; // upper
				Arrays.fill(twoLabels, -1);
				ll = -1; ul=-1;
				if(p1 == 255) {
					if(p1 == p2) {
						ll = pixels[offset-1] < 0 ? -1 : labels[pixels[offset-1]];
						twoLabels[0] = ll;
					}
					if(p1 == p3) {
						ul = pixels[offset-width] < 0 ? -1 : labels[pixels[offset-width]];
						twoLabels[1] = ul;
					}

					if(ll < 0 && ul < 0) {
						pixels[offset] = currlabel;
						labels[currlabel] = currlabel;
						currlabel++;
					}
					else {
						Arrays.sort(twoLabels);
						int smallestLabel = twoLabels[0];
						if(twoLabels[0] < 0) {
							smallestLabel = twoLabels[1];
						}
						pixels[offset] = smallestLabel;

						for(int k=0; k<twoLabels.length; k++) {
							if(twoLabels[k] < 0) {
								continue;
							}
							int tempLabel = twoLabels[k];
							int oldSmallestLabe = labels[tempLabel];
							if (oldSmallestLabe > smallestLabel)
							{
								labels[oldSmallestLabe] = smallestLabel ;
								labels[tempLabel] = smallestLabel;
							}
							else if (oldSmallestLabe < smallestLabel)
							{
								labels[smallestLabel] = oldSmallestLabe ;
							}
						}
					}
				}
				offset++;
			}
		}

		int[] labelSet = new int[currlabel];
		System.arraycopy(labels, 0, labelSet, 0, currlabel);
		labels = null;
		for (int i = 2; i < labelSet.length; i++)
		{
			int curLabel = labelSet[i];
			int preLabel = labelSet[curLabel];
			while (preLabel != curLabel)
			{
				curLabel = preLabel;
				preLabel = labelSet[preLabel];
			}
			labelSet[i] = curLabel;
		}

		// 2. second pass
		// aggregation the pixels with same label index
		Map<Integer, List<PixelNode>> aggregationMap = new HashMap<Integer, List<PixelNode>>();
		for (int i = 0; i < height; i++)
		{
			offset = i * width;
			for (int j = 0; j < width; j++)
			{
				int pixelLabel = pixels[offset+j];
				// skip background
				if(pixelLabel < 0) {
					continue;
				}
				// label each area
				pixels[offset+j] = labelSet[pixelLabel];
				List<PixelNode> pixelList = aggregationMap.get(labelSet[pixelLabel]);
				if(pixelList == null) {
					pixelList = new ArrayList<PixelNode>();
					aggregationMap.put(labelSet[pixelLabel], pixelList);
				}
				PixelNode pn = new PixelNode();
				pn.row = i;
				pn.col = j;
				pn.index = offset+j;
				pixelList.add(pn);
			}
		}

		// assign labels
		Integer[] keys = aggregationMap.keySet().toArray(new Integer[0]);
		Arrays.fill(labelMask, (byte)0);
		for(Integer key : keys) {
			List<PixelNode> pixelList = aggregationMap.get(key);
			if(filterNoise && pixelList.size() < numOfPixels) {
				continue;
			}
			// tag each pixel
			for(PixelNode pnode : pixelList) {
				labelMask[pnode.index] = (byte)key.intValue();
			}

			// return each label rectangle
			if(drawBounding && rectangles != null) {
				rectangles.add(boundingRect(pixelList));
			}
		}

		return keys.length;
	}

	/**
	 *
	 * @param binary - binary image data
	 * @param labelMask - label for each pixel point
     * @return int - total labels of image
     */
	public int process(ByteProcessor binary, byte[] labelMask) {
		return process(binary, labelMask, null, false);
	}

	private Rect boundingRect(List<PixelNode> pixelList) {
		int minx = 10000, maxx = 0;
		int miny = 10000, maxy = 0;
		for(PixelNode pn : pixelList) {
			minx = Math.min(pn.col, minx);
			maxx = Math.max(pn.col, maxx);
			miny = Math.min(pn.row, miny);
			maxy = Math.max(pn.row, maxy);
		}
		int dx = maxx - minx;
		int dy = maxy - miny;
		Rect roi = new Rect();
		roi.x = minx;
		roi.y = miny;
		roi.width = dx;
		roi.height = dy;
		return roi;
	}
}
