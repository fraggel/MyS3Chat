package com.mys3soft.mys3chat;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.Vibrator;
import android.provider.ContactsContract;
import android.service.notification.StatusBarNotification;
import android.support.v7.app.NotificationCompat;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.mys3soft.mys3chat.Models.StaticInfo;
import com.mys3soft.mys3chat.Models.User;
import com.mys3soft.mys3chat.Services.DataContext;
import com.mys3soft.mys3chat.Services.LocalUserService;
import com.mys3soft.mys3chat.Services.Tools;

import java.util.Map;

public class AppService extends Service {
    public AppService() {
    }
    Firebase refUser;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Firebase.setAndroidContext(getApplicationContext());

        DataContext db = new DataContext(this, null, null, 1);

        // check if user exists in local db
       User user = LocalUserService.getLocalUserFromPreferences(getApplicationContext());
        if (user.Email == null) {
            // send to activitylogin
//            Intent intent = new Intent(this, ActivityLogin.class);
//            startActivityForResult(intent, 100);
//
        } else {
            startService(new Intent(this, AppService.class));
            if (refUser == null) {
                refUser = new Firebase(StaticInfo.UsersURL + "/" + user.Email);
            }

        }
        final Firebase reference = new Firebase(StaticInfo.NotificationEndPoint + "/" + user.Email);
        reference.addChildEventListener(
                new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        if (LocalUserService.getLocalUserFromPreferences(getApplicationContext()).Email != null) {
                            Map map = dataSnapshot.getValue(Map.class);
                            String mess = map.get("Message").toString();
                            String senderEmail = map.get("SenderEmail").toString();
                            String senderFullName = Tools.toProperName(map.get("FirstName").toString()) + " " + Tools.toProperName(
                                    map.get("LastName").toString());
                            int notificationType = 1; // Message
                            notificationType = map.get("NotificationType") == null ? 1 : Integer.parseInt(map.get("NotificationType").toString());
                            // check if user is on chat activity with senderEmail
                            if (!StaticInfo.UserCurrentChatFriendEmail.equals(senderEmail)) {
                                notifyUser(senderEmail, senderFullName, mess, notificationType);
                                // remove notification
                                reference.child(dataSnapshot.getKey()).removeValue();
                            } else {
                                reference.child(dataSnapshot.getKey()).removeValue();
                            }
                        }
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                }
        );
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // check if user is login
        if (LocalUserService.getLocalUserFromPreferences(getApplicationContext()).Email != null) {
            sendBroadcast(new Intent("com.mys3soft.mys3chat.restartservice"));
        }


    }

    private void notifyUser(String friendEmail, String senderFullName, String mess, int notificationType) {
        NotificationCompat.Builder not = new NotificationCompat.Builder(getApplicationContext());
        not.setAutoCancel(true);
        not.setSmallIcon(R.mipmap.ic_launcher_round);
        not.setTicker("New Message");
        not.setWhen(System.currentTimeMillis());
        not.setContentText(mess);
        int uniqueID = Tools.createUniqueIdPerUser(friendEmail);

        Intent i;
        // 1) Message 3) Contact Request Accepted
        if (notificationType == 1 || notificationType == 3) {
            i = new Intent(getApplicationContext(), ActivityChat.class);
            DataContext db = new DataContext(getApplicationContext(), null, null, 1);
            User frnd = db.getFriendByEmailFromLocalDB(friendEmail);
            if (frnd.FirstName != null) {
                not.setContentTitle(frnd.FirstName + " " + frnd.LastName);
                i.putExtra("FriendFullName", frnd.FirstName + " " + frnd.LastName);
            } else {
                not.setContentTitle(senderFullName);
                i.putExtra("FriendFullName", senderFullName);
            }
        }
        // Contact Request
        else if (notificationType == 2) {
            i = new Intent(getApplicationContext(), ActivityNotifications.class);
            not.setContentTitle(senderFullName);
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), uniqueID, i, PendingIntent.FLAG_UPDATE_CURRENT);
            not.setContentIntent(pendingIntent);

        } else {
            i = null;
        }
        not.setContentTitle("New Update");
        not.setContentText("New Update");
        not.setContentInfo("New Update");
        not.setContentIntent(PendingIntent.getActivity(getApplicationContext(), uniqueID, new Intent() , PendingIntent.FLAG_CANCEL_CURRENT));
        i.putExtra("FriendEmail", friendEmail);
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        not.setDefaults(Notification.DEFAULT_ALL);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        StatusBarNotification[] notifications = new StatusBarNotification[0];
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            notifications = mNotificationManager.getActiveNotifications();
            if(notifications.length>0) {
                for (StatusBarNotification notification : notifications) {
                    if (notification.getId() != uniqueID) {
                        nm.notify(uniqueID, not.build());
                    }
                }
            }else{
                nm.notify(uniqueID, not.build());
            }
        }
    }
}
