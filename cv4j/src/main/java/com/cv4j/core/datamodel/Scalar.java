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

public class Scalar {
	public int red;
	public int green;
	public int blue;
	public int alpha;
	public Scalar(int red, int green, int blue) {
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.alpha = 255;
	}
	
	public static Scalar argb(int alpha, int red, int green, int blue){
		return new Scalar(red, green, blue);
	}
	
	public static Scalar rgb(int red, int green, int blue){
		return new Scalar(red, green, blue);
	}
	
	public Scalar() {
		red = 0;
		green = 0;
		blue = 0;
		alpha = 255;
	}
	
}
