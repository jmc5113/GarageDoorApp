package io.particle.cloudsdk.example_app;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import io.particle.android.sdk.cloud.ParticleCloud;
import io.particle.android.sdk.cloud.ParticleCloudSDK;
import io.particle.android.sdk.cloud.ParticleDevice;
import io.particle.android.sdk.cloud.exceptions.ParticleCloudException;
import io.particle.android.sdk.utils.Async;
import io.particle.android.sdk.utils.Toaster;



public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ParticleCloudSDK.init(this);
        setContentView(R.layout.activity_login);

        findViewById(R.id.login_button).setOnClickListener(
                v -> {
//                    @JK Add credentials here! Uncomment example lines.
//                    final String email = "email@live.com";
//                    final String password = "yourpassword";
                    final String email = ((EditText) findViewById(R.id.email)).getText().toString();
                    final String password = ((EditText) findViewById(R.id.password)).getText().toString();

                    // Don't (This is safe, but more work!)
//                    @SuppressLint("StaticFieldLeak")
//                    AsyncTask task = new AsyncTask() {
//                        @Override
//                        protected Object doInBackground(Object[] params) {
//                            try {
//                                ParticleCloudSDK.getCloud().logIn(email, password);
//
//                            } catch (final ParticleCloudException e) {
//                                Runnable mainThread = () -> {
//                                    Toaster.l(LoginActivity.this, e.getBestMessage());
//                                    e.printStackTrace();
//                                    Log.d("info", e.getBestMessage());
////                                            Log.d("info", e.getCause().toString());
//                                };
//                                runOnUiThread(mainThread);
//
//                            }
//
//                            return null;
//                        }
//
//                    };
//                        task.execute();

                    //-------

                    // DO!:
                    Async.executeAsync(ParticleCloudSDK.getCloud(), new Async.ApiWork<ParticleCloud, Object>() {

                        private ParticleDevice mDevice;

                        @Override
                        public Object callApi(@NonNull ParticleCloud sparkCloud) throws ParticleCloudException, IOException {
                            sparkCloud.logIn(email, password);
                            sparkCloud.getDevices();
                            try {
                                mDevice = sparkCloud.getDevices().get(0);
                            } catch (IndexOutOfBoundsException iobEx) {
                                throw new RuntimeException("Your account must have at least one device for this example app to work");
                            }
//
//                            Object obj;
//
//                            try {
//                                obj = mDevice.getVariable("openDistance");
//                                Log.d("BANANA", "openDistance: " + obj);
//                            } catch (ParticleDevice.VariableDoesNotExistException e) {
//                                Toaster.s(LoginActivity.this, "Error reading variable");
//                            }

//                            try {
//                                String strVariable = mDevice.getStringVariable("stringvalue");
//                                Log.d("BANANA", "stringvalue: " + strVariable);
//                            } catch (ParticleDevice.VariableDoesNotExistException e) {
//                                Toaster.s(LoginActivity.this, "Error reading variable");
//                            }
//
//                            try {
//                                double dVariable = mDevice.getDoubleVariable("doublevalue");
//                                Log.d("BANANA", "doublevalue: " + dVariable);
//                            } catch (ParticleDevice.VariableDoesNotExistException e) {
//                                Toaster.s(LoginActivity.this, "Error reading variable");
//                            }
//
//                            try {
//                                int intVariable = mDevice.getIntVariable("analogvalue");
//                                Log.d("BANANA", "int analogvalue: " + intVariable);
//                            } catch (ParticleDevice.VariableDoesNotExistException e) {
//                                Toaster.s(LoginActivity.this, "Error reading variable");
//                            }

                            return -1;

                        }

                        @Override
                        public void onSuccess(@NonNull Object value) {
                            Toaster.l(LoginActivity.this, "Logged in");
                            Intent intent = ValueActivity.buildIntent(LoginActivity.this, 123, mDevice.getID());
                            startActivity(intent);
                        }

                        @Override
                        public void onFailure(@NonNull ParticleCloudException e) {
                            Toaster.l(LoginActivity.this, e.getBestMessage());
                            e.printStackTrace();
                            Log.d("info", e.getBestMessage());
                        }
                    });
                }
        );
    }

}
