/*
 * Copyright (c) 2017-present, CV4J Contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cv4j.core.datamodel;

public interface ImageProcessor {

	/** Returns the width of this image in pixels. */
	int getWidth();

	/** Returns the height of this image in pixels. */
	int getHeight();

	/** Returns the channels of this image. */
	int getChannels();

	void getPixel(int row, int col, byte[] rgb);

	/** get all pixels */
	int[] getPixels();

	ImageData getImage();

	/** Returns float array with one channel of image by index*/
	float[] toFloat(int index);

	/** Returns int array with one channel of image by index*/
	int[] toInt(int index);

	/** Returns byte array with one channel of image by index*/
	byte[] toByte(int index);

}
