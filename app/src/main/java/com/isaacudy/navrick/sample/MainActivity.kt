package com.isaacudy.navrick.sample

import android.app.AlertDialog
import android.os.Bundle
import android.transition.Fade
import android.transition.Slide
import android.view.Gravity
import android.view.View
import com.isaacudy.navrick.Navrick
import com.isaacudy.navrick.NavrickActivity
import com.isaacudy.navrick.NavrickBuilder
import com.isaacudy.navrick.NavrickFragment
import kotlinx.android.synthetic.main.view_hello_world.*

class MainActivity : NavrickActivity() {

    override fun createNavrick(): Navrick {
        return NavrickBuilder()
                .addBinding(String::class, HelloWorldView::class)
                .setHome("HOME!")
                .build(this, R.id.frameLayout)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}

class HelloWorldView : NavrickFragment<String>() {
    override val layout = R.layout.view_hello_world

    override fun onViewBound(t: String) {
        helloWorldText.text = t

        if(navrick.backstackSize <= 1){
            backButton.visibility = View.GONE
        }

        backButton.setOnClickListener {
            navrick.back {enter, exit ->
                enter.enterTransition = Slide(Gravity.LEFT)
                exit?.exitTransition = Slide(Gravity.RIGHT)
            }
        }

        nextButton.setOnClickListener {
            navrick.forward("FRAGMENT ${navrick.backstackSize}") {enter, exit ->
                enter.enterTransition = Slide(Gravity.RIGHT)
                exit?.exitTransition = Slide(Gravity.LEFT)
            }
        }
    }

    override fun onBackPressed(): Boolean {
        AlertDialog.Builder(activity).apply {
            setTitle("Are you sure?")
            setMessage("Going back will cancel!")
            setPositiveButton("Sure!") {_, _ ->
                navrick.back()
            }
            show()
        }
        return true
    }
}