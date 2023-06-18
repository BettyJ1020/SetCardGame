package tw.edu.ncku.iim.yhjiang.setcardgame

import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.ViewGroup
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
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
import androidx.navigation.fragment.findNavController
import tw.edu.ncku.iim.yhjiang.setcardgame.databinding.FragmentGameBinding


class GameFragment : Fragment(), SetCardView.SetCardClickListener {
    data class CardAttributes(
        val color: Int,
        val number: Int,
        val shape: String,
        val shading: String
    )

    data class SelectedSet(
//        val id: Int,
        val color: Int,
        val number: Int,
        val shape: SetCardView.Shape,
        val shading: SetCardView.Shading
    ): Parcelable {
        constructor(parcel: Parcel) : this(
//            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt(),
            SetCardView.Shape.valueOf(parcel.readString()!!),
            SetCardView.Shading.valueOf(parcel.readString()!!)
        )

        override fun writeToParcel(parcel: Parcel, flags: Int) {
//            parcel.writeInt(id)
            parcel.writeInt(color)
            parcel.writeInt(number)
            parcel.writeString(shape.name)
            parcel.writeString(shading.name)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<SelectedSet> {
            override fun createFromParcel(parcel: Parcel): SelectedSet {
                return SelectedSet(parcel)
            }

            override fun newArray(size: Int): Array<SelectedSet?> {
                return arrayOfNulls(size)
            }
        }
    }


    var attrIndex = 80 // 81 cards
    val PURPLE = Color.rgb(128, 0, 128)
    val RED = Color.RED
    val GREEN = Color.GREEN
    private lateinit var attrSets: List<CardAttributes>
    val colors = listOf(PURPLE, RED, GREEN)
    val numbers = listOf(1, 2, 3)
    val shapes = listOf("WORM", "DIAMOND", "OVAL")
    val shadings = listOf("SOLID", "STRIP", "EMPTY")
    var setNotFound: Boolean = true

    private lateinit var binding: FragmentGameBinding
    private lateinit var parentLayout: LinearLayout
    private lateinit var buttonHistory: Button
    private lateinit var buttonRestart: Button


    // control SET value
    companion object {
        var SET: Int = 0
    }
    private fun incrementSET() {
        SET++
    }
    private  fun resetSET() {
        SET = 0
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGameBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val linearLayout = LinearLayout(requireContext())
        val cardView = SetCardView(requireContext())

        parentLayout = binding.parentLayout
        buttonHistory = binding.buttonHistory
        buttonRestart = binding.buttonRestart

        var numOfCard = 3
        attrSets = randomAttrs(colors, numbers, shapes, shadings)

        for (i in 1..4) { // initial 12 cards
            val linearLayout = LinearLayout(requireContext())
            linearLayout.orientation = LinearLayout.HORIZONTAL
            linearLayout.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, // width
                550 // height
            )
//                .apply {
//                weight = 1f
//            }
            linearLayout.gravity = Gravity.CENTER

            for (j in 1..numOfCard) {
                val layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
                )
                layoutParams.weight = 1f
                layoutParams.setMargins(10, 10, 10, 10)

                // random attrs
                val cardView = SetCardView(requireContext())
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


        if (parentFragment != null) { // on phone
            buttonHistory.setOnClickListener {
                val action = GameFragmentDirections.actionGameFragmentToHistoryFragment(binding.set)
                findNavController().navigate(action)
            }
        }
        else { // on tablet
            buttonHistory.setOnClickListener {
                val historyFragment = requireActivity().supportFragmentManager.findFragmentById(R.id.historyFragment) as? HistoryFragment
                historyFragment?.showSetOfCards(SET)
            }
        }

        buttonRestart.setOnClickListener {
            restartGame()
        }
    }

//    override fun onAttach(context: Context) {
//        super.onAttach(context)
//    }

    private fun restartGame() {
        attrIndex = 80
        resetSET()
        SelectedCards.selectedCards.clear()

        // Remove all cards from the parent layout
        val parentLayout = binding.parentLayout
        parentLayout.removeAllViews()

        // Restart the activity
        val intent = Intent(requireActivity(), MainActivity::class.java)
        requireActivity().finish()
        startActivity(intent)
    }

    override fun onSetCardClick(clickedCardIds: List<Int>) {
        // Handle the selected card IDs in the MainActivity
        handleSelectedIds(clickedCardIds)
    }

    private fun handleSelectedIds(selectedIds: List<Int>) {
        // Access the selectedIds list and perform any desired operations
        if (selectedIds.size == 3) {
            Log.d("MainActivity", "Selected card IDs: ${tw.edu.ncku.iim.yhjiang.setcardgame.selectedIds}")
            val selectedViews = tw.edu.ncku.iim.yhjiang.setcardgame.selectedIds.map { binding.root.findViewById<SetCardView>(it) }
            for (cardView in selectedViews) {
                cardView.cardSelected = false
                cardView.refreshSelected()
            }
            if (makingSet(selectedViews)) { // if forms a set
                incrementSET()
                val selectedCards = selectedViews.map { cardView ->
                    SelectedSet(
                        color = cardView.color,
                        number = cardView.number,
                        shape = cardView.shape,
                        shading = cardView.shading
                    )
                }
                selectedCards.forEach { selectedCard ->
                    val selectedSet = GameFragment.SelectedSet(
                        selectedCard.color,
                        selectedCard.number,
                        selectedCard.shape,
                        selectedCard.shading
                    )
                    SelectedCards.selectedCards.add(selectedSet)
                }

                // animations of set forming
                val animatorSet = AnimatorSet() // Create an AnimatorSet to combine multiple
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
                        replaceCards(selectedViews) // replace 3 cards
                    }
                    override fun onAnimationCancel(animation: Animator) {}
                    override fun onAnimationRepeat(animation: Animator) {}
                })
            } else { // set not found
                setNotFound = true // write a function to check

                if (setNotFound && attrIndex >= 2) {
                    Toast.makeText(requireContext(), "Not a valid set", Toast.LENGTH_SHORT).show()
                    dealCards()
                } else if (setNotFound && attrIndex < 2) {
                    val linearLayout = LinearLayout(requireContext())
                    linearLayout.orientation = LinearLayout.VERTICAL
                    linearLayout.layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, // width
                        LinearLayout.LayoutParams.MATCH_PARENT // height
                    )
                    linearLayout.gravity = Gravity.CENTER
                    linearLayout.setBackgroundColor(Color.BLACK)
                    val textGameEnd = TextView(requireContext())
                    textGameEnd.text = "Game Over"
                    textGameEnd.setTextColor(Color.WHITE)
                    textGameEnd.gravity = Gravity.CENTER
                    textGameEnd.textSize = 24f
                    linearLayout.addView(textGameEnd)
                    val rootLayout = binding.rootLayout
                    val scrollView = binding.scrollView
                    scrollView.visibility = View.INVISIBLE
                    rootLayout.addView(linearLayout)
                    restartGame()
                } else if (!setNotFound) {
                    Toast.makeText(requireContext(), "Not a valid set, Still have sets", Toast.LENGTH_SHORT).show()
                }

            }
        }
    }
    private fun makingSet(selectedViews: List<SetCardView>): Boolean {

        return checkAttributes(selectedViews, SetCardView::color) &&
                checkAttributes(selectedViews, SetCardView::shape) &&
                checkAttributes(selectedViews, SetCardView::shading) &&
                checkAttributes(selectedViews, SetCardView::number)
//        return false
    }
    private fun replaceCards(selectedViews: List<SetCardView>) {
        if (attrIndex >= 2) { // remaining at least 3 cards
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
            // show the new deal cards
            val fadeInAnimators = mutableListOf<ObjectAnimator>()
            for (cardView in selectedViews) {
                // Create fade-in animator for each card
                val fadeInAnimator = ObjectAnimator.ofFloat(cardView, View.ALPHA, 0f, 1f) // visible
                fadeInAnimator.duration = 1600 // Adjust the duration as needed
                fadeInAnimators.add(fadeInAnimator)
            }

            // Create an AnimatorSet to combine the fade-in animations
            val animatorSet = AnimatorSet()
            animatorSet.playTogether(fadeInAnimators as Collection<Animator>)
            animatorSet.start()
        }
    }
    private fun dealCards() {
        var numOfCard = 3

        // adding cards
        val parentLayout = binding.parentLayout
        val linearLayout = LinearLayout(requireContext())
        linearLayout.orientation = LinearLayout.HORIZONTAL
        linearLayout.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, // width
            550 // height
        )
//            .apply {
//            weight = 1f
//        }
        linearLayout.gravity = Gravity.CENTER

        for (j in 1..numOfCard) {

            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
            layoutParams.weight = 1f
            layoutParams.setMargins(10, 10, 10, 10)

            // random attrs
            val cardView = SetCardView(requireContext())
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