package tw.edu.ncku.iim.yhjiang.setcardgame

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView

class HistoryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        getSupportActionBar()?.hide() // hide title bar

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        val setCount = intent.getIntExtra("SET", 0)
        val setFoundTextView: TextView = findViewById(R.id.text_SET)
        setFoundTextView.text = "SET Found: $setCount"
        val parent : LinearLayout = findViewById(R.id.parentHistoryLayout)
        parent.setBackgroundColor(Color.BLACK)

        val selectedCards = SelectedCards.selectedCards
//        Log.i("selectedCards", "Size: ${selectedCards.size}")

        for (i in 1..setCount) {
            val linearLayout = LinearLayout(this)
            linearLayout.orientation = LinearLayout.HORIZONTAL
            linearLayout.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, // width
                550 // height
            )
//                .apply {
//                weight = 1f
//            }
            linearLayout.gravity = Gravity.CENTER
            linearLayout.setBackgroundColor(Color.BLACK)

            for (j in (i - 1) * 3 until minOf(i * 3, selectedCards.size)) {
                val layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
                )
                layoutParams.weight = 1f
                layoutParams.setMargins(10, 10, 10, 55)

                // random attrs
                val cardView = SetCardView(this)
                cardView.tag = "Card ${selectedCards[j].id}"
                cardView.id = View.generateViewId()
                cardView.addSetIds(cardView.id)
                cardView.layoutParams = layoutParams
                Log.i("selectedCards", "Added card with tag: ${cardView.tag}")
                cardView.color = selectedCards[j].color
                cardView.number = selectedCards[j].number
                cardView.shape = selectedCards[j].shape
                cardView.shading = selectedCards[j].shading

                linearLayout.addView(cardView)
            }
            parent.addView(linearLayout)
        }


    }
}