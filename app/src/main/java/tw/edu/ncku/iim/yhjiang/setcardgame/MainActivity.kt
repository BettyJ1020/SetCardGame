package tw.edu.ncku.iim.yhjiang.setcardgame

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout

data class CardAttributes(
    val color: Int,
    val number: Int,
    val shape: String,
    val shading: String
)

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        getSupportActionBar()?.hide() // hide title bar

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val PURPLE = Color.rgb(128, 0, 128)
        val RED = Color.RED
        val GREEN = Color.GREEN

        // set of attributes
        val colors = listOf(PURPLE, RED, GREEN)
        val numbers = listOf(1, 2, 3)
        val shapes = listOf("WORM", "DIAMOND", "OVAL")
        val shadings = listOf("SOLID", "STRIP", "EMPTY")
        val attrSets = randomAttrs(colors, numbers, shapes, shadings)
        var attrIndex = 80
//        Log.d("MainActivity", "Size of combinations: ${attrSets.size}") // 81

        var numOfCard = 3
        var cardIds = ArrayList<Int>() // to store each card's Id

        // adding cards
        val parentLayout: LinearLayout = findViewById(R.id.parentLayout)
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
                val randColor = attrSets[attrIndex].color
                val randNum = attrSets[attrIndex].number
                val randShape = attrSets[attrIndex].shape
                val randShading = attrSets[attrIndex].shading
                attrIndex -= 1

                val cardView = SetCardView(this)
                cardView.id = View.generateViewId()
                cardView.addCardIds(cardView.id)
                cardView.layoutParams = layoutParams
                cardView.color = randColor
                cardView.number = randNum
                cardView.shape = SetCardView.Shape.valueOf(randShape)
                cardView.shading = SetCardView.Shading.valueOf(randShading)


                linearLayout.addView(cardView)
            }
            parentLayout.addView(linearLayout)
        }


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