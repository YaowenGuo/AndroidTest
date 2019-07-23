package tech.yaowen.customview.ui.databinding

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import tech.yaowen.customview.R
import tech.yaowen.customview.databinding.BindingFragmentBinding




class BindingFragment : Fragment() {

    companion object {
        const val TAG = "BindingFragment"
        fun newInstance() = BindingFragment()
    }

    private lateinit var binding: BindingFragmentBinding

    private lateinit var viewModel: BindingViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.binding_fragment, container, false)
        binding.lifecycleOwner = this
        binding.handler = this
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(requireActivity()).get(BindingViewModel::class.java)

        // Assign the component to a property in the binding class.
        binding.viewModel = viewModel

    }

    public fun layoutChanged() {
        Log.e(TAG, "Layout changed")
    }


    public fun changeBounnd() {
        val param = binding.testChange.layoutParams
        param.height = 300
        binding.testChange.layoutParams = param
    }

}
