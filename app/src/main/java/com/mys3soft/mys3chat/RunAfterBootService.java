package com.mys3soft.mys3chat;

import android.app.ProgressDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.mys3soft.mys3chat.Models.Message;
import com.mys3soft.mys3chat.Models.StaticInfo;
import com.mys3soft.mys3chat.Models.User;
import com.mys3soft.mys3chat.Services.DataContext;
import com.mys3soft.mys3chat.Services.LocalUserService;

import java.util.List;

public class RunAfterBootService extends Service {

    private static final String TAG_BOOT_EXECUTE_SERVICE = "BOOT_BROADCAST_SERVICE";
    private ActivityMain.SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    private User user;
    Firebase refUser;
    private DataContext db;
    private ProgressDialog pd;
    private List<Message> userLastChatList;
    private List<User> userFriednList;

    public RunAfterBootService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG_BOOT_EXECUTE_SERVICE, "RunAfterBootService onCreate() method.");

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String message = "RunAfterBootService onStartCommand() method.";

        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

        Log.d(TAG_BOOT_EXECUTE_SERVICE, "RunAfterBootService onStartCommand() method.");

        // check if user exists in local db
        user = LocalUserService.getLocalUserFromPreferences(this);
        /*if (user.Email == null) {
            // send to activitylogin
            Intent intent = new Intent(this, ActivityLogin.class);
            startActivityForResult(intent, 100);
            return;
        }*/

        // refresh last chat
        userLastChatList = db.getUserLastChatList(user.Email);
        ListAdapter lastChatAdp = new AdapterLastChat(this, userLastChatList);
       /* final ListView lv_LastChatList = (ListView) findViewById(R.id.lv_LastChatList);
        if (lv_LastChatList != null) {
            lv_LastChatList.setAdapter(lastChatAdp);
            // reset listener

            lv_LastChatList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    if (userLastChatList.size() <= position) return false;
                    final Message selectedMessageItem = userLastChatList.get(position);
                    final CharSequence options[] = new CharSequence[]{"Delete Chat"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(Main2Activity.this);
                    builder.setTitle(selectedMessageItem.FriendFullName);
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int index) {
                            // the user clicked on list[index]
                            if (index == 0) {
                                // Delete Chat
                                new AlertDialog.Builder(Main2Activity.this)
                                        .setTitle(selectedMessageItem.FriendFullName)
                                        .setMessage("Are you sure to delete this chat?")
                                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                db.deleteChat(user.Email, selectedMessageItem.FromMail);
                                                Toast.makeText(getApplicationContext(), "Chat deleted successfully", Toast.LENGTH_SHORT).show();
                                                userLastChatList = db.getUserLastChatList(user.Email);
                                                ListAdapter adp = new AdapterLastChat(getApplicationContext(), userLastChatList);
                                                lv_LastChatList.setAdapter(adp);
                                            }
                                        })
                                        .setNegativeButton(android.R.string.no, null)
                                        .show();
                            }
                        }
                    });

                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();
                    return true;
                }
            });

        }
*/
        // refresh contacts
        userFriednList = db.getUserFriendList();
        ListAdapter adp = new FriendListAdapter(this, userFriednList);
        /*final ListView lv_FriendList = (ListView) findViewById(R.id.lv_FriendList);
        if (lv_FriendList != null) {
            lv_FriendList.setAdapter(adp);
            lv_FriendList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    if (userFriednList.size() <= position) return false;
                    final User selectedUser = userFriednList.get(position);
                    final CharSequence options[] = new CharSequence[]{"Profile", "Delete Contact"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(Main2Activity.this);
                    builder.setTitle(selectedUser.FirstName + " " + selectedUser.LastName);
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int index) {
                            // the user clicked on list[index]
                            if (index == 0) {
                                // Profile
                                Intent intent = new Intent(Main2Activity.this, ActivityFriendProfile.class);
                                intent.putExtra("Email", selectedUser.Email);
                                startActivityForResult(intent, StaticInfo.ChatAciviityRequestCode);
                            } else {
                                // Delete Contact
                                new AlertDialog.Builder(Main2Activity.this)
                                        .setTitle(selectedUser.FirstName + " " + selectedUser.LastName)
                                        .setMessage("Are you sure to delete this contact?")
                                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Firebase ref = new Firebase(StaticInfo.EndPoint + "/friends/" + user.Email + "/" + selectedUser.Email);
                                                ref.removeValue();
                                                // delete from local database
                                                db.deleteFriendByEmailFromLocalDB(selectedUser.Email);
                                                Toast.makeText(Main2Activity.this, "Contact deleted successfully", Toast.LENGTH_SHORT).show();
                                                userFriednList = db.getUserFriendList();
                                                ListAdapter adp = new FriendListAdapter(Main2Activity.this, userFriednList);
                                                lv_FriendList.setAdapter(adp);
                                            }
                                        })
                                        .setNegativeButton(android.R.string.no, null)
                                        .show();
                            }
                        }
                    });

                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    builder.show();


                    return true;
                }
            });
        }*/
        Firebase.setAndroidContext(this);

        db = new DataContext(this, null, null, 1);

        pd = new ProgressDialog(this);
        pd.setMessage("Refreshing...");
        // check if user exists in local db
        user = LocalUserService.getLocalUserFromPreferences(this);
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
        // set online status
        user = LocalUserService.getLocalUserFromPreferences(this);
        if (user.Email != null) {
            if (refUser == null) {
                refUser = new Firebase(StaticInfo.UsersURL + "/" + user.Email);
            }
        }
        if (refUser != null)
            refUser.child("Status").setValue("Online");


        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}