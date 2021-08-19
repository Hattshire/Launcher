package dev.hattshire.launcher

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView


class AppListAdapter(context: Context): RecyclerView.Adapter<AppListAdapter.ViewHolder>() {

    private val pm: PackageManager = context.packageManager

    class AppInfo(
        val packageName: CharSequence,
        val icon: Drawable,
        val label: CharSequence
        )

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var icon: ImageView = itemView.findViewById(R.id.app_row_icon)
        var text: TextView = itemView.findViewById(R.id.app_row_text)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            val pos = adapterPosition
            val context: Context = view.context
            val appPackage: String = appList[pos].packageName.toString()
            val launchIntent: Intent? = context.packageManager.getLaunchIntentForPackage(appPackage)
            context.startActivity(launchIntent)
            // TODO(Handle invalid intent)
        }
    }

    companion object {
        private var appList: ArrayList<AppInfo> = ArrayList()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view: View = inflater.inflate(R.layout.app_row, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val textView: TextView = holder.text
        val iconView: ImageView = holder.icon
        textView.text = appList[position].label
        iconView.setImageDrawable(appList[position].icon)
    }

    override fun getItemCount(): Int {
        return appList.size
    }

    private fun updateList() {
        val intent = Intent(Intent.ACTION_MAIN)
            intent.addCategory(Intent.CATEGORY_LAUNCHER)

        val apps: List<ResolveInfo> = pm.queryIntentActivities(intent, 0)
                .sortedBy { info -> info.loadLabel(pm).toString().lowercase() }
        //TODO(Threaded update)
        for (app_info: ResolveInfo in apps) {
            val app = AppInfo(
                app_info.activityInfo.packageName,
                app_info.activityInfo.loadIcon(pm),
                app_info.loadLabel(pm)
            )
            appList.add(app)
        }
    }

    init {
        updateList()
    }
}