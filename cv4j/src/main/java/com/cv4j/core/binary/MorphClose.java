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
import com.cv4j.core.datamodel.Size;

public class MorphClose {

    /***
     * erode operator after success doing dilate operator
     * can fill litter hole.
     *
     * @param binary
     * @param structureElement
     */
    public void process(ByteProcessor binary, Size structureElement) {
        Erode erode = new Erode();
        Dilate dilate = new Dilate();
        dilate.process(binary, structureElement);
        erode.process(binary, structureElement);
    }
}
