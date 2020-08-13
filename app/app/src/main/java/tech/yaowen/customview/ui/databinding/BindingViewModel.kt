package tech.yaowen.customview.ui.databinding

import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.databinding.PropertyChangeRegistry
import androidx.databinding.library.baseAdapters.BR
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class BindingViewModel : ViewModel(), Observable {
    var firstName: MutableLiveData<String> = MutableLiveData<String>()
    private val callbacks: PropertyChangeRegistry = PropertyChangeRegistry()

    var rememberMe = false
        @Bindable get() {
            return field
        }
        set(value) {
            // Avoids infinite loops.
            if (field != value) {
                field = value

                // React to the change.
                // saveData()

                // Notify observers of a new value.
                notifyPropertyChanged(BR.rememberMe)
            }
        }


    init {
        firstName.value = "Albert"
    }




    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
        callbacks.add(callback)
    }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
        callbacks.remove(callback)
    }


    /**
     * Notifies observers that all properties of this instance have changed.
     */
    fun notifyChange() {
        callbacks.notifyCallbacks(this, 0, null)
    }

    /**
     * Notifies observers that a specific property has changed. The getter for the
     * property that changes should be marked with the @Bindable annotation to
     * generate a field in the BR class to be used as the fieldId parameter.
     *
     * @param fieldId The generated BR id for the Bindable field.
     */
    fun notifyPropertyChanged(fieldId: Int) {
        callbacks.notifyCallbacks(this, fieldId, null)
    }
}
