package io.particle.cloudsdk.example_app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import io.particle.android.sdk.cloud.ParticleCloud;
import io.particle.android.sdk.cloud.ParticleEvent;
import io.particle.android.sdk.cloud.ParticleEventHandler;
import io.particle.android.sdk.cloud.exceptions.ParticleCloudException;
import io.particle.android.sdk.cloud.ParticleCloudSDK;
import io.particle.android.sdk.cloud.ParticleDevice;
import io.particle.android.sdk.utils.Async;
import io.particle.android.sdk.utils.Async.ApiWork;
import io.particle.android.sdk.utils.Toaster;

enum doorStatus{
    DOOR_NOT_MOVING,
    DOOR_OPENING,
    DOOR_CLOSING
};

public class ValueActivity extends AppCompatActivity {

    private static final String ARG_VALUE = "ARG_VALUE";
    private static final String ARG_DEVICEID = "ARG_DEVICEID";

    private TextView openPercentText;
    private TextView statusText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_value);
        openPercentText = findViewById(R.id.percentText);
        openPercentText.setText(String.valueOf(getIntent().getIntExtra(ARG_VALUE, 0)));

        statusText = findViewById((R.id.doorStatus));

        Async.executeAsync(ParticleCloudSDK.getCloud(), new ApiWork<ParticleCloud, Object>()
        {
            @Override
            public Object callApi(@NonNull ParticleCloud ParticleCloud) throws ParticleCloudException, IOException {
                ParticleDevice device = ParticleCloud.getDevice(getIntent().getStringExtra(ARG_DEVICEID));
                Object variable;
                try {
                    variable = device.getVariable("openDistance");
                } catch (ParticleDevice.VariableDoesNotExistException e) {
                    Toaster.l(ValueActivity.this, e.getMessage());
                    variable = -1;
                }
                return variable;
            }

            @Override
            public void onSuccess(@NonNull Object i) { // this goes on the main thread
                openPercentText.setText(i.toString());
            }

            @Override
            public void onFailure(@NonNull ParticleCloudException e) {
                e.printStackTrace();
            }
        });

        // start Async task to subscribe for an event
        Async.executeAsync(ParticleCloudSDK.getCloud(), new ApiWork<ParticleCloud, Object>()
        {
            @Override
            public Object callApi(@NonNull ParticleCloud ParticleCloud) throws ParticleCloudException, IOException {
                ParticleDevice device = ParticleCloud.getDevice(getIntent().getStringExtra(ARG_DEVICEID));
                try {
                    device.subscribeToEvents(null, new ParticleEventHandler() {
                        public void onEvent(String eventName, ParticleEvent particleEvent){
                            //start async task to update the status
                            Async.executeAsync(ParticleCloudSDK.getCloud(), new ApiWork<ParticleCloud, Object>()
                            {
                                public Object callApi(@NonNull ParticleCloud particleCloud) throws ParticleCloudException, IOException {
                                    return particleEvent.getDataPayload();
                                }

                                @Override
                                public void onSuccess(@NonNull Object i) { // this goes on the main thread
                                    statusText.setText(i.toString());
                                }

                                @Override
                                public void onFailure(@NonNull ParticleCloudException e) {
                                    e.printStackTrace();
                                }
                            });
                        }

                        public void onEventError(@NonNull Exception e) {
                            e.printStackTrace();
                        }
                    });
                } catch (@NonNull Exception e){
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            public void onSuccess(@NonNull Object i) { // this goes on the main thread
//                openPercentText.setText(i.toString());
            }

            @Override
            public void onFailure(@NonNull ParticleCloudException e) {
                e.printStackTrace();
            }
        });

        Async.executeAsync(ParticleCloudSDK.getCloud(), new ApiWork<ParticleCloud, Object>()
        {
            @Override
            public Object callApi(@NonNull ParticleCloud ParticleCloud) throws ParticleCloudException, IOException {
                ParticleDevice device = ParticleCloud.getDevice(getIntent().getStringExtra(ARG_DEVICEID));
                Object variable;
                try {
                    variable = device.getIntVariable("doorStatus");
                } catch (ParticleDevice.VariableDoesNotExistException e) {
                    Toaster.l(ValueActivity.this, e.getMessage());
                    variable = -1;
                }
                return variable;
            }

            @Override
            public void onSuccess(@NonNull Object i) { // this goes on the main thread
                String message;
                if (i.equals(0))
                    message = "Status Unknown";
                else if (i.equals(2))
                    message = "Opening...";
                else if (i.equals(3))
                    message = "Open";
                else if (i.equals(4))
                    message = "Closing...";
                else if (i.equals(5))
                    message = "Closed";
                else
                    message = "Err - Unknown: " + i.toString();

                statusText.setText(message);
            }

            @Override
            public void onFailure(@NonNull ParticleCloudException e) {
                e.printStackTrace();
            }
        });

        findViewById(R.id.refresh_button).setOnClickListener(v -> {
            //...
            // Do network work on background thread
            Async.executeAsync(ParticleCloudSDK.getCloud(), new ApiWork<ParticleCloud, Object>() {
                @Override
                public Object callApi(@NonNull ParticleCloud ParticleCloud) throws ParticleCloudException, IOException
                {
                    ParticleDevice device = ParticleCloud.getDevice(getIntent().getStringExtra(ARG_DEVICEID));
                    Object variable;
                    try {
                        variable = device.getVariable("openDistance");
                    } catch (ParticleDevice.VariableDoesNotExistException e) {
                        Toaster.l(ValueActivity.this, e.getMessage());
                        variable = -1;
                    }
                    return variable;
                }

                @Override
                public void onSuccess(@NonNull Object i) { // this goes on the main thread
                    openPercentText.setText(i.toString());
                }

                @Override
                public void onFailure(@NonNull ParticleCloudException e) {
                    e.printStackTrace();
                }
            });
        });

        findViewById(R.id.open).setOnClickListener(v -> {
            //...
            // Do network work on background thread
            Async.executeAsync(ParticleCloudSDK.getCloud(), new ApiWork<ParticleCloud, Object>() {
                @Override
                public Object callApi(@NonNull ParticleCloud ParticleCloud) throws ParticleCloudException, IOException {
                    ParticleDevice device = ParticleCloud.getDevice(getIntent().getStringExtra(ARG_DEVICEID));
                    Object Status;
                    try {
                        Status = device.callFunction("open");
                    } catch (ParticleDevice.FunctionDoesNotExistException e) {
                        Toaster.l(ValueActivity.this, e.getMessage());
                        Status = -1;
                    }
                    return Status;
                }

                @Override
                public void onSuccess(@NonNull Object i) {
                    String message;
                    if (i.equals(1))
                        message = "Door Not Moving";
                    else if (i.equals(2))
                        message = "Garage Door Opening";
                    else if (i.equals(4))
                        message = "Garage Door Closing";
                    else
                        message = "Garage Status Unknown: " + i.toString();
                    Toaster.l(ValueActivity.this, message);
                }

                @Override
                public void onFailure(@NonNull ParticleCloudException e) {
                    e.printStackTrace();
                }
            });
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(this).inflate(R.menu.menu_value, menu);
        return (super.onCreateOptionsMenu(menu));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_info) {
            Intent intent = DeviceInfoActivity.buildIntent(ValueActivity.this, getIntent().getStringExtra(ARG_DEVICEID));
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }


    public static Intent buildIntent(Context ctx, Integer value, String deviceId) {
        Intent intent = new Intent(ctx, ValueActivity.class);
        intent.putExtra(ARG_VALUE, value);
        intent.putExtra(ARG_DEVICEID, deviceId);

        return intent;
    }


}
