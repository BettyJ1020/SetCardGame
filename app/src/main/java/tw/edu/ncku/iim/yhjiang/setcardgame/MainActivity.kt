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
import android.widget.Toast

data class CardAttributes(
    val color: Int,
    val number: Int,
    val shape: String,
    val shading: String
)

class MainActivity : AppCompatActivity(), SetCardView.SetCardClickListener {
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
//        var cardIds = ArrayList<Int>() // to store each card's Id

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
                cardView.setCardClickListener(this)
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

    override fun onSetCardClick(clickedCardIds: List<Int>) {
        // Handle the selected card IDs in the MainActivity
        handleSelectedIds(clickedCardIds)
    }

    private fun handleSelectedIds(selectedIds: List<Int>) {
        // Access the selectedIds list and perform any desired operations
        if (selectedIds.size == 3) {
            if (makingSet()) { // if forms a set
                Log.d("MainActivity", "form a set")
                // show animation of card disappear
                // empty selectedIds
                // delete the cards
                // adding 3 cards
            } else {
                Toast.makeText(this, "Not a valid set", Toast.LENGTH_SHORT).show()
                //  in case if no set

            }


        }


    }

    private fun makingSet(): Boolean {
        val selectedViews = selectedIds.map { findViewById<SetCardView>(it) }
        Log.d("MainActivity", "Selected card IDs: $selectedIds")

        return checkAttributes(selectedViews, SetCardView::color) &&
                checkAttributes(selectedViews, SetCardView::shape) &&
                checkAttributes(selectedViews, SetCardView::shading) &&
                checkAttributes(selectedViews, SetCardView::number)
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