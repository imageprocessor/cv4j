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

package com.cv4j.rxjava

import com.cv4j.core.binary.Threshold
import com.cv4j.core.datamodel.ByteProcessor
import com.cv4j.core.datamodel.CV4JImage
import io.reactivex.Flowable
import io.reactivex.functions.Function


class RxThreshold private constructor(image: CV4JImage) {

    private val flowable: Flowable<CV4JImage>

    private var type: Int = 0
    private var method: Int = 0
    private var thresh: Int = 0

    init {
        flowable = Flowable.just(image)
    }

    fun type(type: Int): RxThreshold {

        this.type = type
        return this
    }

    fun method(method: Int): RxThreshold {

        this.method = method
        return this
    }

    fun thresh(thresh: Int): RxThreshold {

        this.thresh = thresh
        return this
    }

    fun process(): Flowable<ByteProcessor> {

        return flowable.map(Function<CV4JImage, ByteProcessor> { cv4JImage ->
            val threshold = Threshold()
            threshold.process(cv4JImage.convert2Gray().processor as ByteProcessor, type, method, thresh)
            cv4JImage.processor as ByteProcessor
        })
    }

    companion object {

        fun image(image: CV4JImage): RxThreshold {

            return RxThreshold(image)
        }
    }
}