/**
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.reactnativecommunity.netinfo;
import android.net.wifi.WifiManager;
import android.net.DhcpInfo;
import android.content.Context;
import android.os.Build;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.module.annotations.ReactModule;

/** Module that monitors and provides information about the connectivity state of the device. */
@ReactModule(name = NetInfoModule.NAME)
public class NetInfoModule extends ReactContextBaseJavaModule implements AmazonFireDeviceConnectivityPoller.ConnectivityChangedCallback {
    WifiManager wifi;
    public static final String NAME = "RNCNetInfo";

    private final ConnectivityReceiver mConnectivityReceiver;
    private final AmazonFireDeviceConnectivityPoller mAmazonConnectivityChecker;

    private int numberOfListeners = 0;

    public NetInfoModule(ReactApplicationContext reactContext) {
        super(reactContext);
        // Create the connectivity receiver based on the API level we are running on
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mConnectivityReceiver = new NetworkCallbackConnectivityReceiver(reactContext);
        } else {
            mConnectivityReceiver = new BroadcastReceiverConnectivityReceiver(reactContext);
        }
        wifi = (WifiManager) reactContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        mAmazonConnectivityChecker = new AmazonFireDeviceConnectivityPoller(reactContext, this);
    }

    @Override
    public void initialize() {
        mConnectivityReceiver.register();
        mAmazonConnectivityChecker.register();
    }

    @Override
    public void onCatalystInstanceDestroy() {
        mAmazonConnectivityChecker.unregister();
        mConnectivityReceiver.unregister();
        mConnectivityReceiver.hasListener = false;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @ReactMethod
    public void getCurrentState(final String requestedInterface, final Promise promise) {
        mConnectivityReceiver.getCurrentState(requestedInterface, promise);
    }

    @ReactMethod
    public void getGatewayIPAddress(final Promise promise) throws Exception {
        new Thread(new Runnable() {
            public void run() {
                try {
                    DhcpInfo dhcpInfo = wifi.getDhcpInfo();
                    int gatewayIPInt = dhcpInfo.gateway;
                    String gatewayIP = String.format(
                      "%d.%d.%d.%d",
                      ((gatewayIPInt) & 0xFF),
                      ((gatewayIPInt >> 8 ) & 0xFF),
                      ((gatewayIPInt >> 16) & 0xFF),
                      ((gatewayIPInt >> 24) & 0xFF)
                    );
                    promise.resolve(gatewayIP);
                } catch (Exception e) {
                    promise.resolve(null);
                }
            }
        }).start();
    }

    @Override
    public void onAmazonFireDeviceConnectivityChanged(boolean isConnected) {
        mConnectivityReceiver.setIsInternetReachableOverride(isConnected);
    }

    @ReactMethod
    public void addListener(String eventName) {
        numberOfListeners++;
        mConnectivityReceiver.hasListener = true;
    }

    @ReactMethod
    public void removeListeners(Integer count) {
        numberOfListeners -= count;
        if (numberOfListeners == 0) {
            mConnectivityReceiver.hasListener = false;
        }
    }
}
