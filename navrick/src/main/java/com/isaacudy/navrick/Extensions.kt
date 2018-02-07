package com.isaacudy.navrick

import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity


internal fun AppCompatActivity.fragmentTransaction(block: FragmentTransaction.()->Unit){
    supportFragmentManager.beginTransaction()
            .apply(block)
            .disallowAddToBackStack()
            .commitNowAllowingStateLoss()
}