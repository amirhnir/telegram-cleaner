package ir.ahnaei.telegramcleaner;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.CountDownTimer;

import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (ActivityCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) != PermissionChecker.PERMISSION_GRANTED) {
            finish();
            return;
        }

        update_size();

        TextView photo = (TextView) findViewById(R.id.photo);
        TextView video = (TextView) findViewById(R.id.video);
        TextView music = (TextView) findViewById(R.id.music);
        TextView more = (TextView) findViewById(R.id.more);

        Animation bb = AnimationUtils.loadAnimation(this, R.anim.bb);
        photo.startAnimation(bb);
        video.startAnimation(bb);
        music.startAnimation(bb);
        more.startAnimation(bb);

        ((RelativeLayout) findViewById(R.id.relativeLayout)).setAnimation(AnimationUtils.loadAnimation(this, R.anim.cc));

        final View start = (View) findViewById(R.id.startbtn);
        final View circleImageView = (View) findViewById(R.id.imageviewCircle);

        circleImageView.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.aa));
        start.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.aa));

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                File root = android.os.Environment.getExternalStorageDirectory();
                File dir = new File(root.getAbsolutePath() + "/teln");
                dir.mkdirs();

                new start_cleaner().execute();
            }
        });

    }

    public void update_size() {
        TextView photo = (TextView) findViewById(R.id.photo);
        TextView video = (TextView) findViewById(R.id.video);
        TextView music = (TextView) findViewById(R.id.music);
        TextView more = (TextView) findViewById(R.id.more);
        TextView total = (TextView) findViewById(R.id.total);


        final File root = android.os.Environment.getExternalStorageDirectory();
        long d = dirSize(new File(root.getAbsolutePath() + "/Telegram"));
        if (d == 0) {

            new CountDownTimer(1500, 1500) {
                @Override
                public void onTick(long l) {

                }

                @Override
                public void onFinish() {

                    View circleImageView = (View) findViewById(R.id.imageviewCircle);
                    View start = (View) findViewById(R.id.startbtn);
                    View done = (View) findViewById(R.id.done);

                    circleImageView.animate().setDuration(800).translationY(circleImageView.getTranslationY() - 500);
                    start.animate().setDuration(800).translationY(start.getTranslationY() - 500);
                    done.animate().setDuration(800).translationY(done.getTranslationY() - 500);

                    start.setEnabled(false);

                }
            }.start();
        }

        total.setText(String.format("%.2f", (float) dirSize(new File(root.getAbsolutePath() + "/Telegram")) / 1000000) + "\n" + "مگابایت");
        photo.setText(String.format("%.2f", (float) dirSize(new File(root.getAbsolutePath() + "/Telegram/Telegram Images")) / 1000000) + "\n" + "مگابایت");
        music.setText(String.format("%.2f", (float) dirSize(new File(root.getAbsolutePath() + "/Telegram/Telegram Audio")) / 1000000) + "\n" + "مگابایت");
        more.setText(String.format("%.2f", (float) dirSize(new File(root.getAbsolutePath() + "/Telegram/Telegram Documents")) / 1000000) + "\n" + "مگابایت");
        video.setText(String.format("%.2f", (float) dirSize(new File(root.getAbsolutePath() + "/Telegram/Telegram Video")) / 1000000) + "\n" + "مگابایت");

    }

    public boolean deleteDirectory(File path) {
        if (path.exists()) {
            File[] files = path.listFiles();
            if (files == null) {
                return true;
            }
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    deleteDirectory(files[i]);
                } else {
                    files[i].delete();
                }
            }
        }
        return (path.delete());
    }


    private class start_cleaner extends AsyncTask<String, String, Boolean> {

        public ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(MainActivity.this);
            pd.setIcon(R.mipmap.ic_launcher);
            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pd.setCancelable(false);
            pd.setMessage("در حال پاکسازی اطلاعات اضافه ...");
            pd.requestWindowFeature(Window.FEATURE_NO_TITLE);
            pd.show();
            ((TextView) pd.findViewById(android.R.id.message)).setGravity(Gravity.RIGHT);


        }

        @Override
        protected Boolean doInBackground(String... args) {

            File root = android.os.Environment.getDataDirectory();
            deleteDirectory(new File(root.getAbsolutePath() + "/Telegram/Telegram Audio"));
            deleteDirectory(new File(root.getAbsolutePath() + "/Telegram/Telegram Images"));
            deleteDirectory(new File(root.getAbsolutePath() + "/Telegram/Telegram Video"));
            deleteDirectory(new File(root.getAbsolutePath() + "/Telegram/Telegram Documents"));

            File root2 = android.os.Environment.getExternalStorageDirectory();
            deleteDirectory(new File(root2.getAbsolutePath() + "/Telegram/Telegram Audio"));
            deleteDirectory(new File(root2.getAbsolutePath() + "/Telegram/Telegram Images"));
            deleteDirectory(new File(root2.getAbsolutePath() + "/Telegram/Telegram Video"));
            deleteDirectory(new File(root2.getAbsolutePath() + "/Telegram/Telegram Documents"));

            return false;
        }

        @Override
        protected void onPostExecute(Boolean th) {

            new CountDownTimer(1500, 1500) {
                @Override
                public void onTick(long millisUntilFinished) {
                }

                @Override
                public void onFinish() {
                    pd.dismiss();


                }
            }.start();

            update_size();

        }
    }

    private long dirSize(File dir) {

        if (dir.exists()) {
            long result = 0;
            File[] fileList = dir.listFiles();
            for (int i = 0; i < fileList.length; i++) {
                if (fileList[i].isDirectory()) {
                    result += dirSize(fileList[i]);
                } else {
                    result += fileList[i].length();
                }
            }
            return result;
        } else
            return 0;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
