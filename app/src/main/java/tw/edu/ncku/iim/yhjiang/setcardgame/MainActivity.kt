package tw.edu.ncku.iim.yhjiang.setcardgame

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import android.view.Gravity
import android.view.ScrollCaptureSession
import android.view.View
import android.widget.*
import androidx.navigation.NavController
import androidx.navigation.Navigation


class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (!isTablet(this)) { // on phone
            navController = Navigation.findNavController(this, R.id.navHostFragment)
        }
    }
    fun isTablet(context: Context): Boolean {
        return ((context.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE)
    }

    override fun onBackPressed() {
        if (!isTablet(this)) {
            navController.navigateUp()
        } else {
            super.onBackPressed()
        }
    }





}