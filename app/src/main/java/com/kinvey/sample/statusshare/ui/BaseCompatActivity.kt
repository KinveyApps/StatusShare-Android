package com.kinvey.sample.statusshare.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.kinvey.sample.statusshare.R

abstract class BaseCompatActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutId)
    }

    open val layoutId = 0

    open val contentId = R.id.fragmentBox

    fun replaceFragment(frag: Fragment, addToBackStack: Boolean) {
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(contentId, frag)
        if (addToBackStack) {
            ft.addToBackStack(frag.toString())
        }
        ft.commit()
    }
}