package ir.ahnaei.telegramcleaner;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.CountDownTimer;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;

import android.os.Bundle;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import ir.ahnaei.telegramcleaner.payment.IabHelper;
import ir.ahnaei.telegramcleaner.payment.IabResult;
import ir.ahnaei.telegramcleaner.payment.Inventory;
import ir.ahnaei.telegramcleaner.payment.Purchase;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static ir.ahnaei.telegramcleaner.config.BAZAAR_TOKEN;
import static ir.ahnaei.telegramcleaner.config.SKU;

public class logo extends Activity {

    boolean mIsPremium = true;
    ProgressDialog pd;

    IabHelper mHelper;
    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener;
    IabHelper.QueryInventoryFinishedListener mGotInventoryListener;

    Boolean p_b = false;
    Boolean buy_or_no = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo);


        ((ImageView) findViewById(R.id.icon)).startAnimation(AnimationUtils.loadAnimation(this, R.anim.aa));


        if (ActivityCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) == PermissionChecker.PERMISSION_GRANTED) {

            mHelper = new IabHelper(logo.this, BAZAAR_TOKEN);
            mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
                public void onIabSetupFinished(IabResult result) {

                    try {
                        mHelper.queryInventoryAsync(mGotInventoryListener);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
                public void onQueryInventoryFinished(IabResult result, Inventory inventory) {

                    if (result.isFailure()) {
                        return;
                    } else {
                        mIsPremium = inventory.hasPurchase(SKU);
                    }
                }
            };

            mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
                public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
                    if (result.isFailure()) {
                        Toast.makeText(logo.this, "خرید نا موفق بود.", Toast.LENGTH_SHORT).show();
                    } else if (purchase.getSku().equals(SKU)) {
                        Toast.makeText(logo.this, "خرید موفق آمیز بود.", Toast.LENGTH_SHORT).show();
                        p_b = true;
                        SharedPreferences shared = getSharedPreferences("s", MODE_PRIVATE);
                        SharedPreferences.Editor editor = shared.edit();
                        editor.putBoolean("b", true);
                        editor.apply();
                    }
                    finish();
                }
            };


            SharedPreferences shared = getSharedPreferences("s", MODE_PRIVATE);
            SharedPreferences.Editor editor = shared.edit();
            Boolean b = shared.getBoolean("b", false);
            if (b) {
                new CountDownTimer(1500, 1500) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                    }

                    @Override
                    public void onFinish() {
                        startActivity(new Intent(logo.this, MainActivity.class));
                        finish();
                    }
                }.start();
            } else {
                Bundle extras = getIntent().getExtras();
                if (extras != null) {
                    buy_or_no = extras.getBoolean("buy_or_no");
                }
                if (buy_or_no) {
                    pd = new ProgressDialog(logo.this);
                    pd.setIcon(R.mipmap.ic_launcher);
                    pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    pd.setCancelable(false);
                    pd.setMessage("درحال برقراری ارتباط ...");
                    pd.setTitle("لطفا کمی صبر کنید...");
                    pd.show();

                    startBuy();
                } else {
                    new CountDownTimer(1000, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                        }

                        @Override
                        public void onFinish() {
                            startActivity(new Intent(logo.this, demo.class));
                            finish();
                        }
                    }.start();
                }

            }

        } else
            ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, 5);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 5) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                startActivity(new Intent(logo.this, logo.class));
                finish();

            } else {
                Toast.makeText(logo.this, "دسترسی های مورد نیاز برنامه باید تایید شوند", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHelper = null;
        if (p_b) {
            startActivity(new Intent(logo.this, logo.class));
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    public void startBuy() {
        try {
            mHelper.launchPurchaseFlow(this, SKU, 100, mPurchaseFinishedListener, System.currentTimeMillis() + "tc");
            pd.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
            new CountDownTimer(1000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                }

                @Override
                public void onFinish() {
                    startBuy();
                }
            }.start();
        }
    }
}
