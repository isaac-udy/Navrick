package com.isaacudy.navrick

import android.app.Fragment
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import java.util.*

private const val NAVRICK_ACTIVITY_ID = "com.isaacudy.navrick.NAVRICK_ACTIVITY_ID"

abstract class NavrickActivity : AppCompatActivity() {

    private lateinit var _id: String

    lateinit var navrick: Navrick
        private set

    override fun onCreate(savedInstanceState: Bundle?) {createNavrick()
        navrick = createNavrick()

        val id = savedInstanceState?.getString(NAVRICK_ACTIVITY_ID)
        if(id != null){
            _id = id
            NavrickStorage.storage[id]?.let{
                navrick.backstack = it
            }
        }
        else {
            _id = UUID.randomUUID().toString()
        }

        super.onCreate(savedInstanceState)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        navrick.initialise()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putString(NAVRICK_ACTIVITY_ID, _id)
        NavrickStorage.storage.put(_id, navrick.backstack)
    }

    override fun onBackPressed() {
        val currentFragment = supportFragmentManager.findFragmentById(navrick.layout)
        val delegated = (currentFragment as? NavrickFragment<*>)?.onBackPressed() ?: false
        if(delegated) return

        if (!navrick.back()) {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if(isFinishing){
            NavrickStorage.storage.remove(_id)
        }
    }

    abstract fun createNavrick(): Navrick
}

internal object NavrickStorage {
    val storage: HashMap<String, ArrayList<Any>> = HashMap()
}