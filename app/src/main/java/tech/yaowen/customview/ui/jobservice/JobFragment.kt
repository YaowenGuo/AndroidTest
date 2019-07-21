package tech.yaowen.customview.ui.jobservice

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.Context.JOB_SCHEDULER_SERVICE
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import tech.yaowen.customview.service.NotificationJobService
import android.content.ComponentName
import android.database.DatabaseUtils
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import tech.yaowen.customview.R


class JobFragment : Fragment() {

    companion object {
        fun newInstance() = JobFragment()
        private val JOB_ID = 0
    }

    private lateinit var viewModel: JobViewModel
    lateinit var binding: tech.yaowen.customview.databinding.JobFragmentBinding
    private var scheduler: JobScheduler? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.job_fragment, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(JobViewModel::class.java)
        binding.user = User("Yaowen", "Guo")

    }

    data class User(val firstName: String, val lastName: String)

    fun scheduleJob(networkType: Int) {
        val constraintSet = networkType != JobInfo.NETWORK_TYPE_NONE
        // notify the user
        if (constraintSet) {
            Toast.makeText(
                requireActivity(), "Job Scheduled, job will run when " +
                        "the constraints are met.", Toast.LENGTH_SHORT
            ).show()
            //Schedule the job

            scheduler = requireActivity().getSystemService(JOB_SCHEDULER_SERVICE) as JobScheduler
            val serviceName = ComponentName(
                requireContext().packageName,
                NotificationJobService::class.java.name
            )
            val myJobInfo = JobInfo.Builder(JOB_ID, serviceName)
                .setRequiredNetworkType(networkType)
                .build()
            scheduler?.schedule(myJobInfo)
        } else {
            Toast.makeText(
                requireActivity(), "Please set at least one constraint",
                Toast.LENGTH_SHORT
            ).show()
            return
        }




    }


    fun cancelJobs() {
        if (scheduler != null) {
            scheduler?.cancelAll()
            scheduler = null
            Toast.makeText(requireActivity(), "Jobs cancelled", Toast.LENGTH_SHORT).show();
        }
    }

    fun scheduleJob() {
        val networkType = when (binding.networkOptions.checkedRadioButtonId) {
            tech.yaowen.customview.R.id.noNetwork -> JobInfo.NETWORK_TYPE_NONE
            tech.yaowen.customview.R.id.anyNetwork -> JobInfo.NETWORK_TYPE_ANY
            else -> JobInfo.NETWORK_TYPE_UNMETERED
        }

        scheduleJob(networkType)
    }


}
