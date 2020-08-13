package tech.yaowen.customview.ui.service

import android.app.Notification
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.annotation.RequiresApi
import tech.yaowen.customview.R
import tech.yaowen.customview.service.TestService

class ServiceTestFragment : Fragment() {

    companion object {
        fun newInstance() =
            ServiceTestFragment()
    }

    private val CHANNEL_DEFAULT_IMPORTANCE: String = "Service"
    private lateinit var viewModel: ServiceViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ServiceViewModel::class.java)
        val button = view?.findViewById<Button>(R.id.message)
        button?.setOnClickListener { view ->
            val activity = requireActivity() as ServiceTestActivity
            activity.addNewBook()

        }

    }

    fun startService(view: View) {
        Log.e("TestService", "Start")
        Intent(requireActivity(), TestService::class.java).also { intent ->
            Log.e("TestService", "Start 1")
            requireActivity().startService(intent)
        }
    }

    fun startForegroundService(view: View) {
        Log.e("TestService", "Start")
        Intent(requireActivity(), TestService::class.java).also { intent ->
            Log.e("TestService", "Start 1")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                requireActivity().startForegroundService(intent)
            }
        }
    }

}