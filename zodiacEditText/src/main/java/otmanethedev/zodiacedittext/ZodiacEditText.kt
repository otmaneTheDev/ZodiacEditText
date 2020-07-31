package otmanethedev.zodiacedittext

import android.content.Context
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import kotlinx.android.synthetic.main.zodiac_date.view.*
import java.util.*


class ZodiacEditText(context: Context, attributes: AttributeSet? = null, defStyleAttr: Int = 0) :
    LinearLayout(context, attributes, defStyleAttr) {
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context) : this(context, null)

    private var errorColorState = ContextCompat.getColorStateList(context, R.color.defaultColorRed)
    private val shakeAnim: Animation = AnimationUtils.loadAnimation(context, R.anim.shake)

    private var defaultColorState = ContextCompat.getColorStateList(context, R.color.defaultColorBlue)
    private val zoomInAnim: Animation = AnimationUtils.loadAnimation(context, R.anim.zoom_in)

    private val cakeDrawable = ContextCompat.getDrawable(context, R.drawable.icon_cake)

    private val currentDate = Calendar.getInstance()

    private var selectedDay: Int = -1
    private var selectedMonth: Int = -1
    private var selectedYear: Int = -1

    private var dateChangedListener: DateChangedListener? = null

    var hasError: Boolean = false

    init {
        init()
        setUpListeners()
        setUpAttributes(attributes)
    }

    fun setDateChangedListener(dateChangedListener: DateChangedListener) {
        this.dateChangedListener = dateChangedListener
    }

    private fun init() {
        View.inflate(context, R.layout.zodiac_date, this)
    }

    private fun setUpAttributes(attributes: AttributeSet?) {
        val typedArray = context.theme.obtainStyledAttributes(attributes, R.styleable.ZodiacEditText, 0, 0)

        val defaultColor = typedArray.getResourceId(R.styleable.ZodiacEditText_defaultColor, R.color.defaultColorBlue)
        val errorColor = typedArray.getResourceId(R.styleable.ZodiacEditText_errorColor, R.color.defaultColorRed)
        val textColor = typedArray.getResourceId(R.styleable.ZodiacEditText_textColor, R.color.defaultColorBlack)
        val hintColor = typedArray.getResourceId(R.styleable.ZodiacEditText_hintColor, R.color.defaultColorGrey)

        defaultColorState = ContextCompat.getColorStateList(context, defaultColor)
        errorColorState = ContextCompat.getColorStateList(context, errorColor)

        editTextDay.setHintTextColor(ContextCompat.getColor(context, hintColor))
        editTextDay.setTextColor(ContextCompat.getColor(context, textColor))

        editTextMonth.setHintTextColor(ContextCompat.getColor(context, hintColor))
        editTextMonth.setTextColor(ContextCompat.getColor(context, textColor))

        editTextYear.setHintTextColor(ContextCompat.getColor(context, hintColor))
        editTextYear.setTextColor(ContextCompat.getColor(context, textColor))

        birthdayContainer.backgroundTintList = defaultColorState
        imageView.imageTintList = defaultColorState

        editTextMonth.isEnabled = false
        editTextYear.isEnabled = false
    }

    private fun setUpListeners() {
        editTextDay.doOnTextChanged { text, start, before, count ->
            selectedDay = if (text?.isBlank() != true) text.toString().toInt() else -1
            if (selectedDay > 31) {
                editTextMonth.isEnabled = false
                editTextYear.isEnabled = false
                showError("Selected day not valid")
            } else {
                if (selectedDay > getMaximumOfMonth(selectedMonth, selectedYear)) {
                    editTextYear.isEnabled = false
                    editTextMonth.isEnabled = false
                    showError("Selected day not in selected month.")
                } else {
                    hideError()
                    showZodiacIcon()
                    checkIfDateCompleted()
                    editTextYear.isEnabled = true
                    editTextMonth.isEnabled = true
                    if (text?.length == 2) editTextMonth.requestFocus()
                }
            }
        }

        editTextMonth.doOnTextChanged { text, start, before, count ->
            selectedMonth = if (text?.isBlank() != true) text.toString().toInt() - 1 else -1
            if (selectedMonth in 0..11) {
                if (selectedDay > getMaximumOfMonth(selectedMonth, selectedYear)) {
                    editTextDay.isEnabled = false
                    editTextYear.isEnabled = false
                    showError("Selected day not in selected month.")
                } else {
                    hideError()
                    showZodiacIcon()
                    checkIfDateCompleted()
                    editTextDay.isEnabled = true
                    editTextYear.isEnabled = true
                    if (text?.length == 2) editTextYear.requestFocus()
                }
            } else {
                editTextDay.isEnabled = false
                editTextYear.isEnabled = false
                showError("Selected month not valid")
            }
        }

        editTextYear.doOnTextChanged { text, start, before, count ->
            if (text?.length == 4) {
                selectedYear = if (!text.isBlank()) text.toString().toInt() else -1
                if (selectedDay > getMaximumOfMonth(selectedMonth, selectedYear)) {
                    showError("Selected day not in selected month.")
                } else {
                    hideError()
                    showZodiacIcon()
                    checkIfDateCompleted()
                }
            }
        }

        editTextMonth.setOnKeyListener { v, keyCode, event ->
            if (event.keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN && editTextMonth.text.isEmpty()) {
                editTextDay.requestFocus()
            }
            false
        }

        editTextYear.setOnKeyListener { v, keyCode, event ->
            if (event.keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN && editTextYear.text.isEmpty()) {
                editTextMonth.requestFocus()
            }
            false
        }
    }

    private fun showZodiacIcon() {
        val iconId = validateZodiacSign(selectedDay, selectedMonth)
        if (iconId != R.drawable.icon_cake) {
            val drawable = context.resources.getDrawable(iconId)
            imageView.setImageDrawable(drawable)
            imageView.startAnimation(zoomInAnim)
            imageView.imageTintList = null
        }
    }

    private fun showError(msg: String) {
        birthdayContainer.backgroundTintList = errorColorState

        imageView.setImageDrawable(cakeDrawable)
        imageView.imageTintList = errorColorState
        imageView.startAnimation(shakeAnim)

        errorMessage.visibility = View.VISIBLE
        errorMessage.text = msg

        hasError = true
    }

    private fun hideError() {
        birthdayContainer.backgroundTintList = defaultColorState

        imageView.imageTintList = defaultColorState
        imageView.setImageDrawable(cakeDrawable)

        errorMessage.visibility = View.GONE
        errorMessage.text = ""

        hasError = false
    }

    private fun checkIfDateCompleted() {
        if (selectedDay != -1 && selectedMonth != -1 && selectedYear != -1) {
            val selectedDate = Calendar.getInstance().apply {
                set(selectedYear, selectedMonth, selectedDay)
            }
            dateChangedListener?.onDateChanged(selectedDate.time)
        }
    }

    private fun getMaximumOfMonth(month: Int, year: Int): Int {
        var calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.YEAR, if (year > 0) year else currentDate.get(Calendar.YEAR))
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    }

    private fun validateZodiacSign(day: Int, month: Int): Int {
        return when (month) {
            11 -> if (day < 22) R.drawable.icon_sagittarius else R.drawable.icon_capricorn
            0 -> if (day < 20) R.drawable.icon_capricorn else R.drawable.icon_aquarius
            1 -> if (day < 19) R.drawable.icon_aquarius else R.drawable.icon_pisces
            2 -> if (day < 21) R.drawable.icon_pisces else R.drawable.icon_aries
            3 -> if (day < 20) R.drawable.icon_aries else R.drawable.icon_taurus
            4 -> if (day < 21) R.drawable.icon_taurus else R.drawable.icon_gemini
            5 -> if (day < 21) R.drawable.icon_gemini else R.drawable.icon_cancer
            6 -> if (day < 23) R.drawable.icon_cancer else R.drawable.icon_leo
            7 -> if (day < 23) R.drawable.icon_leo else R.drawable.icon_virgo
            8 -> if (day < 23) R.drawable.icon_virgo else R.drawable.icon_libra
            9 -> if (day < 23) R.drawable.icon_libra else R.drawable.icon_scorpio
            10 -> if (day < 22) R.drawable.icon_scorpio else R.drawable.icon_sagittarius
            else -> R.drawable.icon_cake
        }
    }

    interface DateChangedListener {
        fun onDateChanged(date: Date)
    }
}

