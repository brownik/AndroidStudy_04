package com.example.androidstudy_04

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isNotEmpty
import androidx.core.view.isVisible
import com.example.androidstudy_04.databinding.ActivityMainBinding
import java.util.LinkedList
import java.util.Queue

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var queue: Queue<TestModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        queue = LinkedList()
        addOnClickListener()

    }

    private fun addOnClickListener() = with(binding) {
        btnAdd.setOnClickListener {
            val num = etNum.text.toString().toInt()
            queue.offer(TestModel(num, createComboBoxView()))
            if (!firstComboLayer.isNotEmpty()) {
                makeAnim(firstComboLayer)
            } else if (!secondComboLayer.isNotEmpty()) {
                makeAnim(secondComboLayer)
            } else if (!thirdComboLayer.isNotEmpty()) {
                makeAnim(thirdComboLayer)
            }
        }
    }

    private fun makeAnim(const: ConstraintLayout) = with(binding){
        val nextQueue = queue.poll()
        val animSet = AnimatorSet()
        const.addView(nextQueue.view)
        animSet.playTogether(appearAnim(const),
            sizeTransAnim(nextQueue),
            disappearAnim(nextQueue, const))
        animSet.start()
    }

    private fun createComboBoxView(): ImageView {
        var tvComboBox = ImageView(this).apply {
            setImageResource(R.drawable.glow_circle_fx_01)
        }
        var layoutParams = ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT)
        tvComboBox.layoutParams = layoutParams
        return tvComboBox
    }

    private fun appearAnim(const: ConstraintLayout): ObjectAnimator {
        return ObjectAnimator.ofFloat(const, View.TRANSLATION_X, const.width.toFloat()).apply {
            duration = 330L
        }
    }

    private fun disappearAnim(model: TestModel, const: ConstraintLayout): ObjectAnimator {
        return ObjectAnimator.ofFloat(const, View.TRANSLATION_X, -const.width.toFloat()).apply {
            startDelay = 330L + (400L * model.num) + 500L
            duration = 330L
            addListener(object : Animator.AnimatorListener{
                override fun onAnimationStart(p0: Animator?) {}
                override fun onAnimationCancel(p0: Animator?) {}
                override fun onAnimationRepeat(p0: Animator?) {}
                override fun onAnimationEnd(p0: Animator?) {
                    const.removeView(model.view)
                    if(queue.size != 0) {
                        val nextQueue = queue.poll()
                        val animSet = AnimatorSet()
                        const.addView(nextQueue.view)
                        animSet.playTogether(appearAnim(const),
                            sizeTransAnim(nextQueue),
                            disappearAnim(nextQueue, const))
                        animSet.start()
                    }
                }
            })
        }
    }

    private fun sizeTransAnim(model: TestModel): ValueAnimator {
        return ValueAnimator.ofFloat(0.2f, 1.2f).apply {
            startDelay = 330L
            duration = 400L
            repeatCount = model.num - 1
            addUpdateListener { animation ->
                model.view.scaleX = animation.animatedValue as Float
                model.view.scaleY = animation.animatedValue as Float
            }
        }
    }
}