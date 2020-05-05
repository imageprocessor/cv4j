package com.cv4j.app.activity

import android.os.Bundle
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBar
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.cv4j.app.R
import com.cv4j.app.app.BaseActivity
import com.cv4j.app.fragment.HomeFragment
import com.cv4j.app.menu.MenuManager
import com.cv4j.app.utils.DoubleClickExitUtils
import com.google.android.material.navigation.NavigationView
import com.safframework.tony.common.utils.Preconditions
import kotlinx.android.synthetic.main.activity_main.*

/**
 *
 * @FileName:
 *          com.cv4j.app.activity.MainActivity
 * @author: Tony Shen
 * @date: 2020-05-05 21:23
 * @version: V1.0 <描述当前版本功能>
 */

class MainActivity : BaseActivity() {

    private var menuManager: MenuManager? = null
    private var mContent: Fragment? = null
    private var doubleClickExitHelper: DoubleClickExitUtils? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViews()
        initData()
    }

    private fun initViews() {
        initToolbar()
        navigation_view.setNavigationItemSelectedListener(object : NavigationView.OnNavigationItemSelectedListener {
            override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
                if (Preconditions.isNotBlank(menuItem.title)) {
                    toolbar.title = menuItem.title
                }
                showMenu(menuItem)
                menuItem.isChecked = true
                if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
                    drawer_layout.closeDrawer(GravityCompat.START)
                }
                return true
            }
        })
    }

    private fun initData() {
        doubleClickExitHelper = DoubleClickExitUtils(this)
        if (mContent == null) {
            menuManager = MenuManager.getInstance(getSupportFragmentManager())
            mContent = HomeFragment()
        }
        getSupportFragmentManager().beginTransaction().add(R.id.content_frame, mContent!!, MenuManager.MenuType.HOME.getTitle()).commit()
    }

    private fun initToolbar() {
        setSupportActionBar(toolbar)
        val actionBar: ActionBar? = supportActionBar
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp)
            actionBar.setDisplayHomeAsUpEnabled(true)
        }
        toolbar.setNavigationOnClickListener(View.OnClickListener { drawer_layout.openDrawer(GravityCompat.START) })
    }

    private fun showMenu(menuItem: MenuItem) {
        when (menuItem.itemId) {
            R.id.drawer_cv4j -> menuManager!!.show(MenuManager.MenuType.HOME)
            R.id.drawer_io -> menuManager!!.show(MenuManager.MenuType.IO)
            R.id.drawer_filters -> menuManager!!.show(MenuManager.MenuType.FILTERS)
            R.id.drawer_spatial_conv -> menuManager!!.show(MenuManager.MenuType.SPTIAL_CONV)
            R.id.drawer_binary -> menuManager!!.show(MenuManager.MenuType.BINARY)
            R.id.drawer_hist -> menuManager!!.show(MenuManager.MenuType.HIST)
            R.id.drawer_template_match -> menuManager!!.show(MenuManager.MenuType.TEMPLATE_MATCH)
            R.id.drawer_pixel_operator -> menuManager!!.show(MenuManager.MenuType.PIXEL_OPERATOR)
            else -> {
            }
        }
    }

    //重写物理按键的返回逻辑(实现返回键跳转到上一页)
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        //用户触摸返回键
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            doubleClickExitHelper!!.onKeyDown(keyCode, event)
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
}