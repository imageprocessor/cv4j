package com.cv4j.rxjava;

import com.cv4j.core.datamodel.CV4JImage;

/**
 * Created by Tony Shen on 2017/5/2.
 */

public class RxBinary {

    CV4JImage image;

    private RxBinary(CV4JImage image) {

        this.image = image;
    }

    public static RxBinary image(CV4JImage image) {

        return new RxBinary(image);
    }
}
