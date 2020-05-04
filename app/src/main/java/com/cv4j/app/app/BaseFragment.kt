package com.cv4j.app.app

import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.os.Build
import androidx.fragment.app.Fragment

/**
 *
 * @FileName:
 *          com.cv4j.app.app.BaseFragment
 * @author: Tony Shen
 * @date: 2020-05-04 10:51
 * @version: V1.0 <描述当前版本功能>
 */
open class BaseFragment : Fragment() {

    /**
     * Fragment 所在的 FragmentActivity
     */
    lateinit var mContext: Activity

    /**
     * Deprecated on API 23
     * @param activity
     */
    override fun onAttach(activity: Activity) {
        super.onAttach(activity!!)
        if (Build.VERSION.SDK_INT < 23) {
            mContext = activity
        }
    }

    @TargetApi(23)
    override fun onAttach(context: Context) {
        super.onAttach(context!!)
        if (context is Activity) {
            mContext = context
        }
    }
}