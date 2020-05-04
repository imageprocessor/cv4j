package com.cv4j.app.fragment

import android.content.res.Resources
import android.graphics.*
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cv4j.app.R
import com.cv4j.app.app.BaseFragment
import com.cv4j.core.datamodel.CV4JImage
import com.cv4j.core.datamodel.Point
import com.cv4j.core.tpl.TemplateMatch
import com.cv4j.image.util.Tools
import kotlinx.android.synthetic.main.fragment_template_match.*

/**
 *
 * @FileName:
 *          com.cv4j.app.fragment.TemplateMatchFragment
 * @author: Tony Shen
 * @date: 2020-05-04 13:12
 * @version: V1.0 <描述当前版本功能>
 */

class TemplateMatchFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val v = inflater.inflate(R.layout.fragment_template_match, container, false)

        initData()
        return v
    }

    private fun initData() {
        val res: Resources = getResources()
        val bitmap1 = BitmapFactory.decodeResource(res, R.drawable.test_tpl_target)
        target_image.setImageBitmap(bitmap1)
        val bitmap2 = BitmapFactory.decodeResource(res, R.drawable.tpl)
        template_image.setImageBitmap(bitmap2)
        val targetCV4J = CV4JImage(bitmap1)
        val targetImageProcessor = targetCV4J.convert2Gray().processor
        val templateCV4J = CV4JImage(bitmap2)
        val templateProcessor = templateCV4J.convert2Gray().processor
        val match = TemplateMatch()
        val floatProcessor = match.match(targetImageProcessor, templateProcessor, TemplateMatch.TM_CCORR_NORMED)
        val points = Tools.getMinMaxLoc(floatProcessor.gray, floatProcessor.width, floatProcessor.height)
        var resultPoint: Point? = null
        if (points != null) {
            resultPoint = points[0]
            val resultBitmap = bitmap1.copy(Bitmap.Config.ARGB_8888, true)
            val canvas = Canvas(resultBitmap)
            val rect = Rect()
            val sx = resultPoint.x + templateProcessor.width / 2
            val sy = resultPoint.y - templateProcessor.height / 2
            rect[sx, sy, sx + templateProcessor.width] = sy + templateProcessor.height
            val paint = Paint()
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 4.0.toFloat()
            paint.color = Color.RED
            canvas.drawRect(rect, paint)
            result.setImageBitmap(resultBitmap)
        }
    }
}