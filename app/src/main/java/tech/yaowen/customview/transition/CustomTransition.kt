package tech.yaowen.customview.transition

import androidx.transition.Transition
import androidx.transition.TransitionValues

class CustomTransition : Transition() {

    // Define a key for storing a property value in
    // TransitionValues.values with the syntax
    // package_name:transition_class:property_name to avoid collisions
    private val PROPNAME_BACKGROUND = "com.example.android.customtransition:CustomTransition:background"

    override fun captureStartValues(transitionValues: TransitionValues) {
        // Call the convenience method captureValues
        captureValues(transitionValues)
    }

    // For the view in transitionValues.view, get the values you
    // want and put them in transitionValues.values
    private fun captureValues(transitionValues: TransitionValues) {
        // Get a reference to the view
        val view = transitionValues.view
        // Store its background property in the values map
        transitionValues.values[PROPNAME_BACKGROUND] = view.background
    }

    override fun captureEndValues(transitionValues: TransitionValues) {
        captureValues(transitionValues)
    }

}