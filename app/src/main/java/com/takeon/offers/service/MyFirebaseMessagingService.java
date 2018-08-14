package com.takeon.offers.service;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.takeon.offers.ui.MainActivity;
import com.takeon.offers.ui.MyApplication;
import java.util.Map;
import org.json.JSONObject;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

  private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();

  private NotificationUtils notificationUtils;

  @Override
  public void onMessageReceived(RemoteMessage remoteMessage) {
    Log.e(TAG, "From: " + remoteMessage.getFrom());

    if (remoteMessage.getNotification() != null) {
      Log.e(TAG, "Notification Body: " + remoteMessage.getNotification().getBody());
      handleNotification(remoteMessage.getNotification().getBody());
    }

    // Check if message contains a data payload.
    if (remoteMessage.getData().size() > 0) {
      System.out.println("Data Payload: " + remoteMessage.getData().toString());
      try {
        handleDataMessage(remoteMessage.getData());
      } catch (Exception e) {
        System.out.println("Exception: " + e.getMessage());
      }
    } else {
      System.out.println("Data Payload: " + remoteMessage.getData().toString());
    }
  }

  @Override
  public void onNewToken(String token) {
    System.out.println("Refreshed token: " + token);

    // If you want to send messages to this application instance or
    // manage this apps subscriptions on the server side, send the
    // Instance ID token to your app server.
    MyApplication.instance.preferenceUtility.setToken(token);
  }

  // Check if message contains a notification payload.

  private void handleNotification(String message) {
    // app is in foreground, broadcast the push message
    Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
    pushNotification.putExtra("message", message);
    LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

    Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
    resultIntent.putExtra("message", message);

    showNotificationMessage(getApplicationContext(), message, resultIntent);

    // play notification sound
    NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
    notificationUtils.playNotificationSound();
  }

  private void handleDataMessage(Map obj) {

    try {
      String imgUrl;
      System.out.println(new JSONObject(obj));
      JSONObject r = new JSONObject(obj);
      String message = r.getString("title");
      //String body = r.getString("body");
      imgUrl = r.getString("banner");

      //if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
      // app is in foreground, broadcast the push message
      Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
      pushNotification.putExtra("message", message);
      LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

      Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
      resultIntent.putExtra("message", message);

      // check for image attachment
      if (TextUtils.isEmpty(imgUrl)) {
        showNotificationMessage(getApplicationContext(), message, resultIntent);
      } else {
        // image is present, show notification with image
        System.out.println("ImageWithNotification-1");
        showNotificationMessageWithBigImage(getApplicationContext(), message, resultIntent,
            imgUrl);
      }

      // play notification sound
      NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
      notificationUtils.playNotificationSound();
      //} else {
      //  // app is in background, show the notification in notification tray
      //  Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
      //  resultIntent.putExtra("message", message);
      //
      //  // check for image attachment
      //  if (TextUtils.isEmpty(imgUrl)) {
      //    showNotificationMessage(getApplicationContext(), message, resultIntent);
      //  } else {
      //    // image is present, show notification with image
      //    showNotificationMessageWithBigImage(getApplicationContext(), message, resultIntent,
      //        imgUrl);
      //  }
      //}

    } catch (Exception e) {
      Log.e(TAG, "Exception: " + e.getMessage());
    }
  }

  /**
   * Showing notification with text only
   */
  private void showNotificationMessage(Context context, String message, Intent intent) {
    notificationUtils = new NotificationUtils(context);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    notificationUtils.showNotificationMessage(message, intent);
  }

  /**
   * Showing notification with text and image
   */
  private void showNotificationMessageWithBigImage(Context context, String message, Intent intent,
      String imageUrl) {
    notificationUtils = new NotificationUtils(context);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    notificationUtils.showNotificationMessage(message, intent, imageUrl);
  }
}