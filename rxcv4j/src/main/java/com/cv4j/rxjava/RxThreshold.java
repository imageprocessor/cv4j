/*
 * Copyright (c) 2017 - present, CV4J Contributors.
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

import com.cv4j.core.binary.Threshold;
import com.cv4j.core.datamodel.ByteProcessor;
import com.cv4j.core.datamodel.CV4JImage;

import io.reactivex.Flowable;
import io.reactivex.functions.Function;

public class RxThreshold {

    private Flowable flowable;

    private int type;
    private int method;
    private int thresh;

    private RxThreshold(CV4JImage image) {
        flowable = Flowable.just(image);
    }

    public static RxThreshold image(CV4JImage image) {

        return new RxThreshold(image);
    }

    public RxThreshold type(int type) {

        this.type = type;
        return this;
    }

    public RxThreshold method(int method) {

        this.method = method;
        return this;
    }

    public RxThreshold thresh(int thresh) {

        this.thresh = thresh;
        return this;
    }

    public Flowable<ByteProcessor> process(){

        return flowable.map(new Function<CV4JImage,ByteProcessor>() {
            @Override
            public ByteProcessor apply(CV4JImage cv4JImage) throws Exception {
                Threshold threshold = new Threshold();
                threshold.process((ByteProcessor)(cv4JImage.convert2Gray().getProcessor()),type,method,thresh);
                return (ByteProcessor)cv4JImage.getProcessor();
            }
        });
    }
}
