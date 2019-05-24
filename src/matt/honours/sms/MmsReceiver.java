package matt.honours.sms;

import matt.honours.sms.service.SmsReceiverService;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

public class MmsReceiver extends WakefulBroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		intent.setClass(context, SmsReceiverService.class);
		intent.putExtra("result", getResultCode());
		startWakefulService(context,intent);
	}

}