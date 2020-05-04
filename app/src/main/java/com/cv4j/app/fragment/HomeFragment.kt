package com.cv4j.app.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cv4j.app.R
import com.cv4j.app.app.BaseFragment
import kotlinx.android.synthetic.main.fragment_home.*

/**
 *
 * @FileName:
 *          com.cv4j.app.fragment.HomeFragment
 * @author: Tony Shen
 * @date: 2020-05-04 11:48
 * @version: V1.0 <描述当前版本功能>
 */

class HomeFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val v: View = inflater.inflate(R.layout.fragment_home, container, false)

        markdownView.loadMarkdownFromAsset("README.md")

        return v
    }
}