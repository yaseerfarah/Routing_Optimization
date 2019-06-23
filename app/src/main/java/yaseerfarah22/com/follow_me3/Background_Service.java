package yaseerfarah22.com.follow_me3;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

public class Background_Service extends Service {
    public Background_Service() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        onTaskRemoved(intent);


        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {

        Intent restart =new Intent(getApplicationContext(),this.getClass());
        restart.setPackage(this.getPackageName());
        startService(restart);

        super.onTaskRemoved(rootIntent);
    }
}
