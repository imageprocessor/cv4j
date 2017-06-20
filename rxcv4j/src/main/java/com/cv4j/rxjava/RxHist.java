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
package com.cv4j.rxjava;

import com.cv4j.core.datamodel.ByteProcessor;
import com.cv4j.core.datamodel.CV4JImage;
import com.cv4j.core.datamodel.ImageProcessor;
import com.cv4j.core.hist.CalcHistogram;
import com.cv4j.core.hist.EqualHist;

import io.reactivex.Flowable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

public class RxHist {

    private CV4JImage image;
    private Flowable flowable;

    private RxHist(CV4JImage image) {

        this.image = image;
        flowable = Flowable.just(image);
    }

    public static RxHist image(CV4JImage image) {

        return new RxHist(image);
    }

    /**
     * 直方图均衡化
     * @return
     */
    public Flowable<CV4JImage> equalize() {

        return flowable.doOnNext(new Consumer<CV4JImage>() {
            @Override
            public void accept(@NonNull CV4JImage cv4JImage) throws Exception {
                ByteProcessor src = (ByteProcessor) cv4JImage.convert2Gray().getProcessor();

                EqualHist equalHist = new EqualHist();
                equalHist.equalize(src);
            }
        });
    }

    /**
     * 计算直方图
     * @param bins
     */
    public Flowable<int[][]> calcRGBHist(final int bins) {

        return flowable.map(new Function<CV4JImage,int[][]>() {
            @Override
            public int[][] apply(@NonNull CV4JImage cv4JImage) throws Exception {

                CalcHistogram calcHistogram = new CalcHistogram();
                ImageProcessor imageProcessor = cv4JImage.getProcessor();
                int[][] hist = new int[imageProcessor.getChannels()][bins];
                calcHistogram.calcRGBHist(imageProcessor,bins,hist,true);
                return hist;
            }
        });
    }
}
