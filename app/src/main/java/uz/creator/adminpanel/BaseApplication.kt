package uz.creator.adminpanel

import android.app.Application
import android.content.Context
import uz.creator.adminpanel.utils.Permanent

class BaseApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        if (getSharedPreferences(
                Permanent.PREF_LAN_NAME,
                Context.MODE_PRIVATE
            ).getBoolean(Permanent.LAN_KEY, false)
        ) {
            Permanent.isKiril = true
        }
    }

}