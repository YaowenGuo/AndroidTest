package tech.yaowen.lifecycle.ui.main

import android.content.Context
import android.content.ContextWrapper
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import tech.yaowen.lifecycle.databinding.MainFragmentBinding


class MainFragment : Fragment() {

    lateinit var binding: MainFragmentBinding


    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MainFragmentBinding.inflate(inflater)

        return binding.main
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
    }

    override fun onResume() {
        super.onResume()
        val view = binding.message
        val context = view.context

        val lifeOwner = requireContext().lifecycleOwner()

        Log.e("Lifecycle", lifeOwner?.javaClass?.name ?: "")
    }


    fun Context.fragmentActivity(): FragmentActivity? {
        var curContext = this
        var maxDepth = 20
        while (--maxDepth > 0 && curContext !is FragmentActivity) {
            curContext = (curContext as ContextWrapper).baseContext
        }
        return if(curContext is FragmentActivity)
            curContext
        else
            null
    }

    fun Context.lifecycleOwner(): LifecycleOwner? {
        var curContext = this
        var maxDepth = 20
        while (maxDepth --> 0 && curContext !is LifecycleOwner) {
            curContext = (curContext as ContextWrapper).baseContext
        }
        return if (curContext is LifecycleOwner) {
            curContext
        } else {
            null
        }
    }

}
