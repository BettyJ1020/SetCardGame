package tw.edu.ncku.iim.yhjiang.setcardgame

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast

data class CardAttributes(
    val color: Int,
    val number: Int,
    val shape: String,
    val shading: String
)

var attrIndex = 80
val PURPLE = Color.rgb(128, 0, 128)
val RED = Color.RED
val GREEN = Color.GREEN



class MainActivity : AppCompatActivity(), SetCardView.SetCardClickListener {
    // set of attributes
    val colors = listOf(PURPLE, RED, GREEN)
    val numbers = listOf(1, 2, 3)
    val shapes = listOf("WORM", "DIAMOND", "OVAL")
    val shadings = listOf("SOLID", "STRIP", "EMPTY")
    private lateinit var attrSets: List<CardAttributes>

    override fun onCreate(savedInstanceState: Bundle?) {

        getSupportActionBar()?.hide() // hide title bar

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)





        var numOfCard = 3
//        var cardIds = ArrayList<Int>() // to store each card's Id

        // adding cards
        val parentLayout: LinearLayout = findViewById(R.id.parentLayout)
        attrSets = randomAttrs(colors, numbers, shapes, shadings)
        Log.d("MainActivity", "Size of combinations: ${attrSets.size}") // 81
        for (i in 1..4) { // initial 12 cards
            val linearLayout = LinearLayout(this)
            linearLayout.orientation = LinearLayout.HORIZONTAL
            linearLayout.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, // width
                LinearLayout.LayoutParams.WRAP_CONTENT // height
            ).apply {
                weight = 1f
            }
            linearLayout.gravity = Gravity.CENTER


            for (j in 1..numOfCard) {

                val layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
                )
                layoutParams.weight = 1f
                layoutParams.setMargins(10, 10, 10, 10)

                // random attrs

                val cardView = SetCardView(this)
                cardView.setCardClickListener(this)
                cardView.id = View.generateViewId()
                cardView.addCardIds(cardView.id)
                cardView.layoutParams = layoutParams
                cardView.color = attrSets[attrIndex].color
                cardView.number = attrSets[attrIndex].number
                cardView.shape = SetCardView.Shape.valueOf(attrSets[attrIndex].shape)
                cardView.shading = SetCardView.Shading.valueOf(attrSets[attrIndex].shading)
                attrIndex -= 1
                Log.d("Index", "attrIndex: $attrIndex")

                linearLayout.addView(cardView)
            }
            parentLayout.addView(linearLayout)
        }


    }

    override fun onSetCardClick(clickedCardIds: List<Int>) {
        // Handle the selected card IDs in the MainActivity
        handleSelectedIds(clickedCardIds)
    }

    private fun handleSelectedIds(selectedIds: List<Int>) {
        // Access the selectedIds list and perform any desired operations
        if (selectedIds.size == 3) {
            val selectedViews = tw.edu.ncku.iim.yhjiang.setcardgame.selectedIds.map { findViewById<SetCardView>(it) }
            if (makingSet(selectedViews)) { // if forms a set
                // replace 3 cards
                // Create an AnimatorSet to combine multiple animations
                val animatorSet = AnimatorSet()
                val fadeOutAnimators = mutableListOf<ObjectAnimator>() // fade-out animations for each selected card
                for (cardView in selectedViews) {
                    val fadeOutAnimator = ObjectAnimator.ofFloat(cardView, View.ALPHA, 1f, 0f) // invisible
                    fadeOutAnimator.duration = 500 // Adjust the duration as needed
                    fadeOutAnimators.add(fadeOutAnimator)
                }

                // Set up the AnimatorSet and start the animations
                animatorSet.playTogether(fadeOutAnimators as Collection<Animator>)
                animatorSet.start()

                // Replace the cards after the animation finishes
                animatorSet.addListener(object : Animator.AnimatorListener {
                    override fun onAnimationStart(animation: Animator) {}
                    override fun onAnimationEnd(animation: Animator) {
                        replaceCards(selectedViews)
                    }
                    override fun onAnimationCancel(animation: Animator) {}
                    override fun onAnimationRepeat(animation: Animator) {}
                })

            } else {
                Toast.makeText(this, "Not a valid set", Toast.LENGTH_SHORT).show()
                //  in case if no set

            }


        }


    }

    private fun makingSet(selectedViews: List<SetCardView>): Boolean {
//        val selectedViews = selectedIds.map { findViewById<SetCardView>(it) }
        Log.d("MainActivity", "Selected card IDs: $selectedIds")

//        return checkAttributes(selectedViews, SetCardView::color) &&
//                checkAttributes(selectedViews, SetCardView::shape) &&
//                checkAttributes(selectedViews, SetCardView::shading) &&
//                checkAttributes(selectedViews, SetCardView::number)
        return true
    }

    private fun replaceCards(selectedViews: List<SetCardView>) {
        for (cardView in selectedViews) {
            cardView.color = attrSets[attrIndex].color
            cardView.number = attrSets[attrIndex].number
            cardView.shape = SetCardView.Shape.valueOf(attrSets[attrIndex].shape)
            cardView.shading = SetCardView.Shading.valueOf(attrSets[attrIndex].shading)
            cardView.cardSelected = false
            cardView.refreshSelected() // remove from selectedIds
            attrIndex -= 1
            Log.d("Index", "attrIndex: $attrIndex")
        }
        val fadeInAnimators = mutableListOf<ObjectAnimator>()
        for (cardView in selectedViews) {
            // Create fade-in animator for each card
            val fadeInAnimator = ObjectAnimator.ofFloat(cardView, View.ALPHA, 0f, 1f) // visible
            fadeInAnimator.duration = 1900 // Adjust the duration as needed
            fadeInAnimators.add(fadeInAnimator)
        }

        // Create an AnimatorSet to combine the fade-in animations
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(fadeInAnimators as Collection<Animator>)
        animatorSet.start()
    }
    private fun <T> checkAttributes(selectedViews: List<SetCardView>, attributeSelector: (SetCardView) -> T): Boolean {
        val attributes = selectedViews.map(attributeSelector)
        return compareSame(attributes[0], attributes[1], attributes[2]) ||
                compareDiff(attributes[0], attributes[1], attributes[2])
    }

    fun <T> compareSame(value1: T, value2: T, value3: T): Boolean {
        return value1 == value2 && value2 == value3
    }

    fun <T> compareDiff(value1: T, value2: T, value3: T): Boolean {
        return value1 != value2 && value2 != value3  && value1 != value3
    }


    private fun randomAttrs(
        colors: List<Int>,
        numbers: List<Int>,
        shapes: List<String>,
        shadings: List<String>
    ): List<CardAttributes> {
        val combinations = mutableListOf<CardAttributes>()
        for (color in colors) {
            for (number in numbers) {
                for (shape in shapes) {
                    for (shading in shadings) {
                        val combination = CardAttributes(color, number, shape, shading)
                        combinations.add(combination)
                    }
                }
            }
        }
        combinations.shuffle() // random the order

        return combinations
    }

}