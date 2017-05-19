package com.cv4j.rxjava

import com.cv4j.core.datamodel.CV4JImage
import com.cv4j.core.filters.CommonFilter

/**
 * Created by Tony Shen on 2017/5/19.
 */
data class WrappedCV4JImage(val image: CV4JImage, val filters: List<CommonFilter>) {
}