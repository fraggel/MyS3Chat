package com.mys3soft.mys3chat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.mys3soft.mys3chat.Models.Message;
import com.mys3soft.mys3chat.Models.StaticInfo;
import com.mys3soft.mys3chat.Models.User;
import com.mys3soft.mys3chat.Services.DataContext;
import com.mys3soft.mys3chat.Services.LocalUserService;

import java.util.List;

public class Main2Activity extends AppCompatActivity {
    Button button0, button1, button2, button3, button4, button5, button6,
            button7, button8, button9, buttonAdd, buttonSub, buttonDivision,
            buttonMul, button10, buttonC, buttonEqual;
    TextView crunchifyEditText;

    float mValueOne, mValueTwo;
    private ActivityMain.SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    private User user;
    Firebase refUser;
    private DataContext db;
    private ProgressDialog pd;
    private List<Message> userLastChatList;
    private List<User> userFriednList;

    boolean crunchifyAddition, mSubtract, crunchifyMultiplication, crunchifyDivision;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        button0 = (Button) findViewById(R.id.button0);
        button1 = (Button) findViewById(R.id.button1);
        button2 = (Button) findViewById(R.id.button2);
        button3 = (Button) findViewById(R.id.button3);
        button4 = (Button) findViewById(R.id.button4);
        button5 = (Button) findViewById(R.id.button5);
        button6 = (Button) findViewById(R.id.button6);
        button7 = (Button) findViewById(R.id.button7);
        button8 = (Button) findViewById(R.id.button8);
        button9 = (Button) findViewById(R.id.button9);
        button10 = (Button) findViewById(R.id.buttonDot);
        buttonAdd = (Button) findViewById(R.id.buttonadd);
        buttonSub = (Button) findViewById(R.id.buttonsub);
        buttonMul = (Button) findViewById(R.id.buttonmul);
        buttonDivision = (Button) findViewById(R.id.buttondiv);
        buttonC = (Button) findViewById(R.id.buttonDel);
        buttonEqual = (Button) findViewById(R.id.buttoneql);
        crunchifyEditText = (TextView) findViewById(R.id.edit_text);

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        666);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
        }
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        667);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
        }
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        667);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
        }

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECEIVE_BOOT_COMPLETED)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.RECEIVE_BOOT_COMPLETED)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECEIVE_BOOT_COMPLETED},
                        667);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
        }
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
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                crunchifyEditText.setText(crunchifyEditText.getText() + "1");
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                crunchifyEditText.setText(crunchifyEditText.getText() + "2");
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                crunchifyEditText.setText(crunchifyEditText.getText() + "3");
            }
        });

        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                crunchifyEditText.setText(crunchifyEditText.getText() + "4");
            }
        });

        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                crunchifyEditText.setText(crunchifyEditText.getText() + "5");
            }
        });

        button6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                crunchifyEditText.setText(crunchifyEditText.getText() + "6");
            }
        });

        button7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                crunchifyEditText.setText(crunchifyEditText.getText() + "7");
            }
        });

        button8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                crunchifyEditText.setText(crunchifyEditText.getText() + "8");
            }
        });

        button9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                crunchifyEditText.setText(crunchifyEditText.getText() + "9");
            }
        });

        button0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                crunchifyEditText.setText(crunchifyEditText.getText() + "0");
            }
        });

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (crunchifyEditText == null) {
                    crunchifyEditText.setText("");
                } else {
                    mValueOne = Float.parseFloat(crunchifyEditText.getText() + "");
                    crunchifyAddition = true;
                    crunchifyEditText.setText(null);
                }
            }
        });

        buttonSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mValueOne = Float.parseFloat(crunchifyEditText.getText() + "");
                mSubtract = true;
                crunchifyEditText.setText(null);
            }
        });

        buttonMul.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mValueOne = Float.parseFloat(crunchifyEditText.getText() + "");
                crunchifyMultiplication = true;
                crunchifyEditText.setText(null);
            }
        });

        buttonDivision.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mValueOne = Float.parseFloat(crunchifyEditText.getText() + "");
                crunchifyDivision = true;
                crunchifyEditText.setText(null);
            }
        });

        buttonEqual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mValueTwo = Float.parseFloat(crunchifyEditText.getText() + "");
                if(mValueTwo==1404){
                    crunchifyEditText.setText("");
                    Intent i = new Intent (v.getContext(), ActivityMain.class);

                    startActivityForResult(i, 0);
                }
                if (crunchifyAddition == true) {
                    crunchifyEditText.setText(mValueOne + mValueTwo + "");
                    crunchifyAddition = false;
                }

                if (mSubtract == true) {
                    crunchifyEditText.setText(mValueOne - mValueTwo + "");
                    mSubtract = false;
                }

                if (crunchifyMultiplication == true) {
                    crunchifyEditText.setText(mValueOne * mValueTwo + "");
                    crunchifyMultiplication = false;
                }

                if (crunchifyDivision == true) {
                    crunchifyEditText.setText(mValueOne / mValueTwo + "");
                    crunchifyDivision = false;
                }
            }
        });

        buttonC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                crunchifyEditText.setText("");
            }
        });

        button10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                crunchifyEditText.setText(crunchifyEditText.getText() + ".");
            }
        });
    }
    protected void onStart() {
        super.onStart();

        // check if user exists in local db
        user = LocalUserService.getLocalUserFromPreferences(this);
        if (user.Email == null) {
            // send to activitylogin
            Intent intent = new Intent(this, ActivityLogin.class);
            startActivityForResult(intent, 100);
            return;
        }

        // refresh last chat
        userLastChatList = db.getUserLastChatList(user.Email);
        ListAdapter lastChatAdp = new AdapterLastChat(this, userLastChatList);
        final ListView lv_LastChatList = (ListView) findViewById(R.id.lv_LastChatList);
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

        // refresh contacts
        userFriednList = db.getUserFriendList();
        ListAdapter adp = new FriendListAdapter(this, userFriednList);
        final ListView lv_FriendList = (ListView) findViewById(R.id.lv_FriendList);
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
                                Intent i = new Intent(Main2Activity.this, ActivityFriendProfile.class);
                                i.putExtra("Email", selectedUser.Email);
                                startActivityForResult(i, StaticInfo.ChatAciviityRequestCode);
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

    }
}
