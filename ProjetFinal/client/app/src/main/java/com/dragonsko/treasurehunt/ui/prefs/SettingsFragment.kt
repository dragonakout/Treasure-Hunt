package com.dragonsko.treasurehunt.ui.prefs

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import com.dragonsko.treasurehunt.Keys
import com.dragonsko.treasurehunt.R
import com.dragonsko.treasurehunt.Utils
import com.dragonsko.treasurehunt.databinding.FragmentSettingsBinding
import com.google.android.material.slider.Slider

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        setupSlider(
            binding.nbDailyQuestSlider,
            binding.nbDailyQuestNumber,
            1f,
            1f,
            5f,
            Utils.MAX_NUMBER_OF_DAILY_QUESTS.toFloat(),
            Keys.NB_DAILY_QUEST,
            {value: Float -> value.toInt().toString()}
        )
        setupDistancePreferences()
        setupSwitch(
            binding.distancePreviewSwitch,
            binding.distancePreviewTextviewDistance,
            binding.distancePreviewTextviewTemps,
            Keys.DISTANCE_MEASUREMENT_PREVIEW
        )

        return binding.root
    }

    private fun setupDistancePreferences() {
        // Setup distance pref switch
        setupSwitch(
            binding.questDistancePrefsSwitch,
            binding.questDistanceTextviewDistance,
            binding.questDistanceTextviewTemps,
            Keys.DISTANCE_MEASUREMENT,
            fun() {
                if(binding.questDistancePrefsSwitch.isChecked) {
                    binding.questDistancePrefsDistanceSlider.visibility = View.VISIBLE
                    binding.questDistanceSliderValueDistance.visibility = View.VISIBLE

                    val layoutParams = binding.questDistanceBg.layoutParams
                    //binding.questDistanceBg.layoutParams = ViewGroup.LayoutParams(layoutParams.width, floor(layoutParams.height * 0.8).toInt())
                    binding.questDistancePrefsTimeSlider.visibility = View.GONE
                    binding.questDistanceCourseMarcheSwitch.visibility = View.GONE
                    binding.questDistanceTempsTextviewCourse.visibility = View.GONE
                    binding.questDistanceTempsTextviewMarche.visibility = View.GONE
                    binding.questDistanceSliderValueTime.visibility = View.GONE
                } else {
                    binding.questDistancePrefsTimeSlider.visibility = View.VISIBLE
                    binding.questDistanceSliderValueTime.visibility = View.VISIBLE

                    binding.questDistanceCourseMarcheSwitch.visibility = View.VISIBLE
                    binding.questDistanceTempsTextviewCourse.visibility = View.VISIBLE
                    binding.questDistanceTempsTextviewMarche.visibility = View.VISIBLE
                    val layoutParams = binding.questDistanceBg.layoutParams
                    //binding.questDistanceBg.layoutParams = ViewGroup.LayoutParams(layoutParams.width, floor(layoutParams.height * 1.25).toInt())
                    binding.questDistancePrefsDistanceSlider.visibility = View.GONE
                    binding.questDistanceSliderValueDistance.visibility = View.GONE
                }})

        // setup transportation mean switch
        setupSwitch(
            binding.questDistanceCourseMarcheSwitch,
            binding.questDistanceTempsTextviewCourse,
            binding.questDistanceTempsTextviewMarche,
            Keys.TRANSPORTATION_MEAN,
            fun() {}
        )


        // Setup sliders
        setupSlider(
            binding.questDistancePrefsDistanceSlider,
            binding.questDistanceSliderValueDistance,
            0.1f,
            0.1f,
            10f,
            Utils.AVERAGE_DISTANCE_M.toFloat() * 2 / 1000,
            Keys.DISTANCE_VALUE_DISTANCE,
            {value: Float -> value.toString().substring(0, 3) + " km"}
        )

        setupSlider(
            binding.questDistancePrefsTimeSlider,
            binding.questDistanceSliderValueTime,
            5f,
            5f,
            120f,
            Utils.AVERAGE_DISTANCE_M.toFloat() * 2 / 4000 * 60,   // meters to minutes
            Keys.DISTANCE_VALUE_TIME,
            {value: Float -> value.toInt().toString() + " min"}
        )
    }

    private fun setupSlider(slider: Slider, sliderTitle: TextView, stepSize: Float, valueFrom: Float, valueTo: Float, defaultValue: Float, key: String, format: (value: Float) -> String) {
        slider.stepSize = stepSize
        slider.valueFrom = valueFrom
        slider.valueTo = valueTo
        val nbDailyQuests = Utils.readIntFromSharedPrefs(key, activity).toFloat()
        slider.value = if (nbDailyQuests != 0f) nbDailyQuests else defaultValue
        sliderTitle.text = format(slider.value)
        slider.addOnChangeListener { s, value, fromUser ->
            sliderTitle.text = format(value)
            Utils.writeToSharedPrefs(key, value.toInt(), activity)
        }
    }

    private fun setupSwitch(switch: SwitchCompat, textView1: TextView, textView2: TextView, sharedPrefsKey: String, onChangedCallback : () -> Unit = fun() {}) {
        val prefMeasurement = Utils.readIntFromSharedPrefs(sharedPrefsKey, activity) != 0
        switch.isChecked = prefMeasurement
        val isDarkMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
        switch.setOnCheckedChangeListener { buttonView, isChecked ->
            val activated = if(isChecked) 1 else 0
            changeSwitchColors(textView1, textView2, isChecked, isDarkMode)
            Utils.writeToSharedPrefs(sharedPrefsKey, activated, activity)
            onChangedCallback()

        }
        changeSwitchColors(textView1, textView2, prefMeasurement, isDarkMode)
        onChangedCallback()
    }



    private fun changeSwitchColors(view1: TextView, view2: TextView, isActivated: Boolean, isDarkMode: Boolean) {
        if(isActivated) {
            if(isDarkMode) {
                view1.setTextColor(resources.getColor(R.color.main_beige, null))
                view2.setTextColor(resources.getColor(R.color.grey, null))
            } else {
                view1.setTextColor(resources.getColor(R.color.ocean_blue, null))
                view2.setTextColor(resources.getColor(R.color.grey, null))
            }
        } else {
            if(isDarkMode) {
                view2.setTextColor(resources.getColor(R.color.main_beige, null))
                view1.setTextColor(resources.getColor(R.color.grey,null))
            } else {
                view2.setTextColor(resources.getColor(R.color.ocean_blue, null))
                view1.setTextColor(resources.getColor(R.color.grey,null))
            }
        }
    }
}