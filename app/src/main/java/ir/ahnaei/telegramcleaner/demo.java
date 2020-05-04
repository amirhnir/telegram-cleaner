package ir.ahnaei.telegramcleaner;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;

import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

public class demo extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);


        if (ActivityCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) != PermissionChecker.PERMISSION_GRANTED) {
            finish();
            return;
        }


        new AlertDialog.Builder(this).setTitle(R.string.app_name)
                .setIcon(R.mipmap.ic_launcher)
                .setMessage("برای استفاده از برنامه باید ابتدا آن را خریداری کنید . اما قبل از آن میتوانید یکبار عملگرد برنامه را تست بفرمایید !")
                .setPositiveButton("خرید برنامه", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(demo.this, logo.class);
                        intent.putExtra("buy_or_no", true);
                        startActivity(intent);
                        finish();
                    }
                }).setNeutralButton("خروج", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        }).setCancelable(false)
                .setNegativeButton("تست برنامه", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        File root = android.os.Environment.getExternalStorageDirectory();
                        File dir = new File(root.getAbsolutePath() + "/teln");

                        if (!dir.exists()) {
                            dir.mkdirs();
                            startActivity(new Intent(demo.this, MainActivity.class));
                        } else {
                            Toast.makeText(demo.this, "قبلا یکبار برنامه را تست کرده اید !", Toast.LENGTH_SHORT).show();
                        }
                        finish();
                    }
                }).show();

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
