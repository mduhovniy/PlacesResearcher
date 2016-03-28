package info.duhovniy.maxim.placesresearcher.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by maxduhovniy on 06/03/2016.
 */
public class PowerConnectionReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
/*
        int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isChargingStart = status == BatteryManager.BATTERY_STATUS_CHARGING;
        boolean isChargingFinish = status == BatteryManager.BATTERY_STATUS_FULL;
*/
        if(intent.getAction().equals(Intent.ACTION_POWER_CONNECTED))
            Toast.makeText(context, "Charging started", Toast.LENGTH_SHORT).show();

        if(intent.getAction().equals(Intent.ACTION_POWER_DISCONNECTED))
            Toast.makeText(context, "Charging stopped", Toast.LENGTH_SHORT).show();

    }
}