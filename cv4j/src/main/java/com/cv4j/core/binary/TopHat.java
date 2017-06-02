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

public class TopHat {

    /**
     * top hat is difference between original binary and morphology open on it
     * @param binary - input binary image
     * @param structureElement  - size of morph
     */
    public void process(ByteProcessor binary, Size structureElement) {
        int width = binary.getWidth();
        int height = binary.getHeight();
        byte[] data = new byte[width*height];
        System.arraycopy(binary.getGray(), 0, data, 0, data.length);
        MorphOpen open = new MorphOpen();
        open.process(binary, structureElement);
        byte[] output = binary.getGray();
        int c = 0;
        for(int i=0; i<data.length; i++) {
            c = (data[i]&0xff - output[i]&0xff);
            output[i] = (byte) ((c > 0) ? 255 : 0);
        }
    }
}
