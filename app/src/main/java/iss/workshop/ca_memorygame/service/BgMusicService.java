package iss.workshop.ca_memorygame.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

import iss.workshop.ca_memorygame.R;

public class BgMusicService extends Service {

    MediaPlayer mMediaPlayer;
    public static String onBGMusic = "off";
    private Thread bgMusicThread;
    private static String mMusicMode = "";

    public BgMusicService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            mMusicMode = intent.getAction();
        }
        if (mMusicMode != null && !bgMusicThread.interrupted()) {
            bgMusicThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    if (mMediaPlayer == null) {
                        mMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.bg_pixabay);
                        mMediaPlayer.setLooping(true);
                    }
                    if (onBGMusic.equals("on")) {
                        switch (mMusicMode) {
                            case "play":
                                mMediaPlayer.setVolume(1f, 1f);
                                mMediaPlayer.start();
                                break;
                            case "gaming":
                                mMediaPlayer.setVolume(0.5f, 0.5f);
                                mMediaPlayer.start();
                                break;
                            case "pause":
                                mMediaPlayer.pause();
                                break;
                        }
                    } else if (mMusicMode != null && onBGMusic.equals("off")) {
                        if (mMediaPlayer != null) {
                            mMediaPlayer.stop();
                            mMediaPlayer.release();
                            mMediaPlayer = null;
                        }
                    }
                }
            });
            bgMusicThread.start();
        }
        return START_STICKY;
    }

    public static void startBgMusicService(Context context, String mode) {
        Intent bgMusicIntent = new Intent(context, BgMusicService.class);
        bgMusicIntent.setAction(mode);
        context.startService(bgMusicIntent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }
}