package tech.yaowen.customview.ui.transition

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.transition.Scene
import android.transition.TransitionManager
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import tech.yaowen.customview.R
import tech.yaowen.customview.databinding.ASceneBinding
import tech.yaowen.customview.databinding.AnotherSceneBinding
import tech.yaowen.customview.databinding.TransitionFragmentBinding

class TransitionFragment : Fragment() {


    private lateinit var viewModel: TransitionViewModel
    private lateinit var binding: TransitionFragmentBinding
    private lateinit var aScene:Scene
    private lateinit var anotherScene:Scene
    private var isSceneOne = true


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.transition_fragment, container, false)
        binding.sceneRoot.setOnClickListener {
//            Toast.makeText(requireActivity(), "ssdsd", Toast.LENGTH_LONG).show()
            doScene()
        }
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val inflater = LayoutInflater.from(requireContext())
        viewModel = ViewModelProviders.of(requireActivity()).get(TransitionViewModel::class.java)
        val sceneRoot: ViewGroup = binding.sceneRoot
        val aSceneBinding = DataBindingUtil.inflate<ASceneBinding>(inflater, R.layout.a_scene, sceneRoot, false)

        val anotherSceneBinding =
            DataBindingUtil.inflate<AnotherSceneBinding>(inflater, R.layout.another_scene, sceneRoot, false)

//        val aScene: Scene = Scene.getSceneForLayout(sceneRoot, R.layout.a_scene, requireContext())
//        val anotherScene: Scene = Scene.getSceneForLayout(sceneRoot, R.layout.another_scene, requireContext())

        aScene = Scene(sceneRoot, aSceneBinding.root)
        anotherScene = Scene(sceneRoot, anotherSceneBinding.root)

        aSceneBinding.listener = this
        anotherSceneBinding.listener = this
//        binding.listener = this
//        binding.sceneRoot.setOnClickListener {
//            if (isSceneOne) {
//                toAnotherScene()
//            } else {
//                toAScene()
//            }
//        }

    }

    fun doScene() {
        if (isSceneOne) {
            toAnotherScene()
        } else {
            toAScene()
        }
    }

    fun toAnotherScene() {
        isSceneOne = false
        TransitionManager.go(anotherScene)
    }

    fun toAScene() {
        isSceneOne = true
        TransitionManager.go(aScene)
    }

}
