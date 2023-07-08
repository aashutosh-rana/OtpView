package com.mobile.customotp

import android.content.Context
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.EditText
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat

/**
 * @author Aashutosh kumar <aashutosh.kr.2308@gmail.com>
 * Gyankaar Technologies Pvt Ltd
 * $Date 2023/07/08
 **/

class CustomOtpEditText : ConstraintLayout {

    private var customBackground: Int = R.drawable.bg_rect
    private var otpLength: Int = 6
    private var boxPadding: Float = 10F
    private var boxMargin: Float = 10F
    private val otpEditTexts = mutableListOf<EditText>()
    private var size = 0

    private val validationColor: Int by lazy {
        ContextCompat.getColor(context, R.color.green_600)
    }

    constructor(context: Context) : super(context, null) {
        init(context, null, R.style.AppTheme)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs, R.style.AppTheme)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context, attrs, defStyleAttr)
    }

    private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.CustomOtpEditText)
            customBackground = typedArray.getInt(
                R.styleable.CustomOtpEditText_customBackground,
                R.drawable.bg_rect
            )
            otpLength = typedArray.getInt(R.styleable.CustomOtpEditText_otpLength, 6)
            boxPadding = typedArray.getDimension(R.styleable.CustomOtpEditText_boxPadding, 10F)
            boxMargin = typedArray.getDimension(R.styleable.CustomOtpEditText_boxMargin, 10F)
            setupOTPInputViews()
            typedArray.recycle()
        }

    }

    private fun setupOTPInputViews() {
        val constraintSet = ConstraintSet()
        constraintSet.clone(this)
        for (i in 0 until otpLength) {
            val editText = EditText(context)
            editText.id = View.generateViewId()
            editText.inputType = InputType.TYPE_CLASS_NUMBER
            editText.gravity = Gravity.CENTER
            editText.tag = i
            editText.layoutParams = LayoutParams(MATCH_PARENT, WRAP_CONTENT)
            editText.setPadding(
                boxPadding.toInt(),
                boxPadding.toInt(),
                boxPadding.toInt(),
                boxPadding.toInt()
            )
            if (i != otpLength - 1) {
                val params = editText.layoutParams as LayoutParams
                params.marginEnd = boxMargin.toInt()
                editText.layoutParams = params
            }
            editText.maxEms = 1
            editText.filters = arrayOf(InputFilter.LengthFilter(1))
            editText.setBackgroundResource(customBackground)
            editText.setOnFocusChangeListener { v, hasFocus ->
                if (hasFocus)
                    size = v.tag as Int
            }
            editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s?.length == 1) {
                        try {
                            if (otpEditTexts[size + 1].text.isBlank())
                                focusNextEditText()
                        } catch (e: IndexOutOfBoundsException) {
                            focusNextEditText()
                        }
                    }
                    if (s?.length == 0) {
                        if (size < otpLength - 1) {
                            if (otpEditTexts[size + 1].text.isBlank() && size > 0) {
                                val editText1 = otpEditTexts[size - 1]
                                editText1.requestFocus()
                            }
                        } else {
                            val editText1 = otpEditTexts[size - 1]
                            editText1.requestFocus()
                        }
                    }
                }

                override fun afterTextChanged(s: Editable?) {}
            })

            otpEditTexts.add(editText)
            addView(editText)

            constraintSet.connect(
                editText.id,
                ConstraintSet.START,
                ConstraintSet.PARENT_ID,
                ConstraintSet.START
            )
            constraintSet.connect(
                editText.id,
                ConstraintSet.END,
                ConstraintSet.PARENT_ID,
                ConstraintSet.END
            )
            constraintSet.constrainWidth(editText.id, MATCH_PARENT)
            constraintSet.constrainHeight(editText.id, ConstraintSet.WRAP_CONTENT)
            if (i == 0) {
                constraintSet.connect(
                    editText.id,
                    ConstraintSet.TOP,
                    ConstraintSet.PARENT_ID,
                    ConstraintSet.TOP
                )
            } else {
                constraintSet.connect(
                    editText.id,
                    ConstraintSet.TOP,
                    getChildAt(i - 1).id,
                    ConstraintSet.BOTTOM
                )
            }
        }
        constraintSet.connect(
            getChildAt(0).id,
            ConstraintSet.TOP,
            ConstraintSet.PARENT_ID,
            ConstraintSet.TOP
        )

        constraintSet.createVerticalChain(
            ConstraintSet.PARENT_ID,
            ConstraintSet.TOP,
            ConstraintSet.PARENT_ID,
            ConstraintSet.BOTTOM,
            IntArray(otpLength),
            FloatArray(otpLength),
            ConstraintSet.CHAIN_PACKED
        )

        constraintSet.applyTo(this)
    }

    private fun focusNextEditText() {
        for (i in otpEditTexts.indices) {
            val editText = otpEditTexts[i]
            if (editText.text.isBlank()) {
                editText.requestFocus()
//                size = i
                return
            }
        }
    }

    fun getFocusedEditText(): EditText? {
        val focusedView = findViewWithTag<View>(1)
        return focusedView as? EditText
    }


    fun getOTP(): String {
        val otpBuilder = StringBuilder()
        for (editText in otpEditTexts) {
            otpBuilder.append(editText.text)
        }
        return otpBuilder.toString()
    }
}