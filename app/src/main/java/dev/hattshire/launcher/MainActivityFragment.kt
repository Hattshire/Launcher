package dev.hattshire.launcher

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.icu.text.DateFormat
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import rm.com.clocks.ClockImageView
import java.text.DateFormat.getDateInstance
import java.text.DateFormat.getTimeInstance
import java.util.*
import kotlin.collections.ArrayList

class MainActivityFragment : Fragment() {
    private lateinit var  listView: RecyclerView
    private lateinit var clockView: TextView
    private lateinit var  dateView: TextView
    private lateinit var analogClockView: ClockImageView
    private var favViews: ArrayList<ImageView> = ArrayList(4)

    class TimeUpdateReceiver(private val activity: MainActivityFragment): BroadcastReceiver() {
        override fun onReceive(c: Context, i: Intent) {
            activity.updateTime()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.apps_page, container, false)

        listView = view.findViewById(R.id.AppList)
        clockView = view.findViewById(R.id.ClockView)
        dateView = view.findViewById(R.id.DateView)
        analogClockView = view.findViewById(R.id.AnalogClock)

        favViews.add(view.findViewById(R.id.appfav1))
        favViews.add(view.findViewById(R.id.appfav2))
        favViews.add(view.findViewById(R.id.appfav3))
        favViews.add(view.findViewById(R.id.appfav4))

        requireActivity().registerReceiver(
            TimeUpdateReceiver(this),
            IntentFilter(Intent.ACTION_TIME_TICK)
        )
        updateTime()

        listView.adapter = AppListAdapter(requireContext())
        listView.layoutManager = LinearLayoutManager(requireContext())

        setFavouriteApps()

        return view
    }

    fun updateTime() {
        val currentTime = Calendar.getInstance().time
        clockView.text = getTimeInstance(java.text.DateFormat.SHORT).format(currentTime)
        dateView .text = getDateInstance().format(currentTime)
        /*clockView.text = SimpleDateFormat("KK:mm aaa").format(currentTime).toString()
        dateView .text = SimpleDateFormat("E dd LLL ").format(currentTime).toString()*/
        analogClockView.animateToTime(
            Calendar.getInstance().get(Calendar.HOUR),
            Calendar.getInstance().get(Calendar.MINUTE))
    }
    private fun setFavouriteApps() {
        val favAppIds: ArrayList<String> = arrayListOf(
            "com.android.dialer",
            "com.whatsapp",
            "org.mozilla.firefox",
            "com.android.camera2")
        for(favId in 0..3) {
            favViews[favId].setImageDrawable(requireActivity().packageManager.getApplicationIcon(favAppIds[favId]))
            favViews[favId].setOnClickListener { launchPackage(favAppIds[favId]) }
        }
    }
    private fun launchPackage(appId: String) {
        val launchIntent: Intent? = requireActivity().packageManager.getLaunchIntentForPackage(appId)
        startActivity(launchIntent)
    }
}