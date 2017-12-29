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
package com.cv4j.core.pixels;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.cv4j.core.datamodel.ColorProcessor;
import com.cv4j.core.datamodel.Scalar;

public class PrincipalColorExtractor {

	private List<ClusterCenter> clusterCenterList;
	private List<ClusterPoint> pointList;
	
	private int numOfCluster;
	
	public PrincipalColorExtractor(int clusters)
	{
		this.numOfCluster = clusters;
		pointList = new ArrayList<ClusterPoint>();
		clusterCenterList = new ArrayList<ClusterCenter>();
	}
	
	public PrincipalColorExtractor()
	{
		this(5);
	}

	public List<Scalar> extract(ColorProcessor processor) {
		// initialization the pixel data
        int width = processor.getWidth();
        int height = processor.getHeight();
        byte[] R = processor.getRed();
        byte[] G = processor.getGreen();
        byte[] B = processor.getBlue();
        
        //Create random points to use a the cluster center
		Random random = new Random();
		int index = 0;
		for (int i = 0; i < numOfCluster; i++)
		{
		    int randomNumber1 = random.nextInt(width);
		    int randomNumber2 = random.nextInt(height);
		    index = randomNumber2 * width + randomNumber1;
		    ClusterCenter cc = new ClusterCenter(randomNumber1, randomNumber2, R[index]&0xff, G[index]&0xff, B[index]&0xff);
		    cc.setcIndex(i);
		    clusterCenterList.add(cc); 
		}
        
        // create all cluster point
        for (int row = 0; row < height; ++row)
        {
            for (int col = 0; col < width; ++col)
            {
            	index = row * width + col;
            	pointList.add(new ClusterPoint(row, col, R[index]&0xff, G[index]&0xff, B[index]&0xff));

            }
        }
        
        // initialize the clusters for each point
        double[] clusterDisValues = new double[clusterCenterList.size()];
        for(int i=0; i<pointList.size(); i++)
        {
        	for(int j=0; j<clusterCenterList.size(); j++)
        	{
        		clusterDisValues[j] = calculateEuclideanDistance(pointList.get(i), clusterCenterList.get(j));
        	}
        	pointList.get(i).setClusterIndex(getCloserCluster(clusterDisValues));
        }
        
        // calculate the old summary
        // assign the points to cluster center
        // calculate the new cluster center
        // computation the delta value
        // stop condition--
        double[][] oldClusterCenterColors = reCalculateClusterCenters();
        int times = 10;
        while(true)
        {
        	stepClusters();
        	double[][] newClusterCenterColors = reCalculateClusterCenters();
        	if(isStop(oldClusterCenterColors, newClusterCenterColors))
        	{        		
        		break;
        	} 
        	else
        	{
        		oldClusterCenterColors = newClusterCenterColors;
        	}
        	if(times > 10) {
        		break;
        	}
        	times++;
        }
        
        //update the result image
        List<Scalar> colors = new ArrayList<Scalar>();
        for(ClusterCenter cc : clusterCenterList) {
        	
        	colors.add(cc.getPixelColor());
        }
        return colors;
	}

	private boolean isStop(double[][] oldClusterCenterColors, double[][] newClusterCenterColors) {
		boolean stop = false;
		for (int i = 0; i < oldClusterCenterColors.length; i++) {
			if (oldClusterCenterColors[i][0] == newClusterCenterColors[i][0] &&
					oldClusterCenterColors[i][1] == newClusterCenterColors[i][1] &&
					oldClusterCenterColors[i][2] == newClusterCenterColors[i][2]) {
				stop = true;
				break;
			}
		}
		return stop;
	}

	/**
	 * update the cluster index by distance value
	 */
	private void stepClusters() 
	{
        // initialize the clusters for each point
        double[] clusterDisValues = new double[clusterCenterList.size()];
        for(int i=0; i<pointList.size(); i++)
        {
        	for(int j=0; j<clusterCenterList.size(); j++)
        	{
        		clusterDisValues[j] = calculateEuclideanDistance(pointList.get(i), clusterCenterList.get(j));
        	}
        	pointList.get(i).setClusterIndex(getCloserCluster(clusterDisValues));
        }
		
	}

	/**
	 * using cluster color of each point to update cluster center color
	 * 
	 * @return
	 */
	private double[][] reCalculateClusterCenters() {
		
		// clear the points now
		for(int i=0; i<clusterCenterList.size(); i++)
		{
			 clusterCenterList.get(i).setNumOfPoints(0);
		}
		
		// recalculate the sum and total of points for each cluster
		double[] redSums = new double[numOfCluster];
		double[] greenSum = new double[numOfCluster];
		double[] blueSum = new double[numOfCluster];
		for(int i=0; i<pointList.size(); i++)
		{
			int cIndex = (int)pointList.get(i).getClusterIndex();
			clusterCenterList.get(cIndex).addPoints();
    		int ta = pointList.get(i).getPixelColor().alpha;
            int tr = pointList.get(i).getPixelColor().red;
            int tg = pointList.get(i).getPixelColor().green;
            int tb = pointList.get(i).getPixelColor().blue;
            ta = 255;
			redSums[cIndex] += tr;
			greenSum[cIndex] += tg;
			blueSum[cIndex] += tb;
		}
		
		double[][] oldClusterCentersColors = new double[clusterCenterList.size()][3];
		for(int i=0; i<clusterCenterList.size(); i++)
		{
			double sum  = clusterCenterList.get(i).getNumOfPoints();
			int cIndex = clusterCenterList.get(i).getcIndex();
			int red = (int)(greenSum[cIndex]/sum);
			int green = (int)(greenSum[cIndex]/sum);
			int blue = (int)(blueSum[cIndex]/sum);
			clusterCenterList.get(i).setPixelColor(new Scalar(red, green, blue));
			oldClusterCentersColors[i][0] = red;
			oldClusterCentersColors[i][0] = green;
			oldClusterCentersColors[i][0] = blue;
		}
		
		return oldClusterCentersColors;
	}
	
	

	/**
	 * 
	 * @param clusterDisValues
	 * @return
	 */
	private double getCloserCluster(double[] clusterDisValues)
	{
		double min = clusterDisValues[0];
		int clusterIndex = 0;
		for(int i=0; i<clusterDisValues.length; i++)
		{
			if(min > clusterDisValues[i])
			{
				min = clusterDisValues[i];
				clusterIndex = i;
			}
		}
		return clusterIndex;
	}

	/**
	 *
	 * @param p
	 * @param c
	 * @return distance value
	 */
	private double calculateEuclideanDistance(ClusterPoint p, ClusterCenter c) 
	{
	    int pr = p.getPixelColor().red;
	    int pg = p.getPixelColor().green;
	    int pb = p.getPixelColor().blue;
	    int cr = c.getPixelColor().red;
	    int cg = c.getPixelColor().green;
	    int cb = c.getPixelColor().blue;
	    return Math.sqrt(Math.pow((pr - cr), 2.0) + Math.pow((pg - cg), 2.0) + Math.pow((pb - cb), 2.0));
	}

}
