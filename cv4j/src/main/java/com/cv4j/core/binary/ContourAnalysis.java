/**
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
package com.cv4j.core.binary;

import com.cv4j.core.datamodel.ByteProcessor;
import com.cv4j.core.datamodel.MeasureData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContourAnalysis {

    public void process(ByteProcessor binary, int[] labels, List<MeasureData> measureDataList) {
        int width = binary.getWidth();
        int height = binary.getHeight();
        int offset = 0;
        Map<Integer, List<PixelNode>> aggregationMap = new HashMap<Integer, List<PixelNode>>();
        for (int i = 0; i < height; i++)
        {
            offset = i * width;
            List<PixelNode> pixelList = null;
            PixelNode pn = null;
            for (int j = 0; j < width; j++)
            {
                int pixelLabel = labels[offset+j];
                // skip background
                if(pixelLabel < 0) {
                    continue;
                }
                // label each area
                pixelList = aggregationMap.get(pixelLabel);
                if(pixelList == null) {
                    pixelList = new ArrayList<PixelNode>();
                    aggregationMap.put(pixelLabel, pixelList);
                }
                pn = new PixelNode();
                pn.row = i;
                pn.col = j;
                pn.index = offset+j;
                pixelList.add(pn);
            }
        }

        // assign labels
        Integer[] keys = aggregationMap.keySet().toArray(new Integer[0]);
        List<PixelNode> pixelList = null;
        GeoMoments moments = new GeoMoments();
        for(Integer key : keys) {
            pixelList = aggregationMap.get(key);
            measureDataList.add(moments.calculate(pixelList));
        }
    }
}
