package tw.edu.ncku.iim.yhjiang.setcardgame

import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.navArgs
import tw.edu.ncku.iim.yhjiang.setcardgame.databinding.FragmentGameBinding
import tw.edu.ncku.iim.yhjiang.setcardgame.databinding.FragmentHistoryBinding

class HistoryFragment : Fragment() {

    private lateinit var binding: FragmentHistoryBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    val args: HistoryFragmentArgs by navArgs<HistoryFragmentArgs>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val setCount = GameFragment.SET
        Log.i("args", "SET: $setCount")
        val setFoundTextView: TextView = view.findViewById(R.id.text_SET)
        setFoundTextView.text = "SET Found: $setCount"

        if (setCount != 0) {
            showSetOfCards(setCount)
        }
    }

    fun showSetOfCards(setCount: Int) {
        val parent : LinearLayout = binding.parentHistoryLayout
        parent.setBackgroundColor(Color.BLACK)

        val selectedCards = SelectedCards.selectedCards
//        Log.i("selectedCards", "Size: ${selectedCards.size}")

        for (i in 1..setCount) {
            val linearLayout = LinearLayout(activity)
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
                val cardView = SetCardView(requireContext())
                cardView.id = View.generateViewId()
                cardView.addSetIds(cardView.id)
                cardView.layoutParams = layoutParams
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