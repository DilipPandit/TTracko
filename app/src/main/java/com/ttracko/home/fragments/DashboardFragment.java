package com.ttracko.home.fragments;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.ttracko.R;
import com.ttracko.home.Utils.SharedPref;
import com.ttracko.home.adapter.FriendListAdapter;
import com.ttracko.home.models.Users;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by root on 27/6/18.
 */

public class DashboardFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {
    ArrayList<String> allContactList;
    ArrayList<Users> usersArrayList;
    int REQUEST_READ_CONTACT = 10003;
    FirebaseFirestore firebaseFirestore;
    FriendListAdapter friendListAdapter;
    @InjectView(R.id.rcFriendsList)
    RecyclerView rcFriendsList;
    RecyclerView.LayoutManager layoutManager;
    SwipeRefreshLayout mSwipeRefreshLayout;
    @InjectView(R.id.btnGroupAdd)
    FloatingActionButton btnGroupAdd;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        ButterKnife.inject(this, view);
        _init(view);
        return view;

    }

    private void _init(View view) {
        firebaseFirestore = FirebaseFirestore.getInstance();
        if (allContactList == null)
            allContactList = new ArrayList<>();
        if (usersArrayList == null)
            usersArrayList = new ArrayList<>();
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);
        getReadWritePermission();
        userEventListener();
        btnGroupAdd.setOnClickListener(this);
    }


    void createGroup(Users group, String groupName) {
        mSwipeRefreshLayout.setRefreshing(true);
        firebaseFirestore.collection("Users").document(groupName)
                .set(group.toMap(), SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getActivity(), "Users created", Toast.LENGTH_SHORT).show();
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }).
                addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Users creation failed", Toast.LENGTH_SHORT).show();
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                });
    }

    private static final String TAG = "KK";

    private void userEventListener() {

    }

    private void setFriendListLayout() {
        mSwipeRefreshLayout.setRefreshing(false);
        if (friendListAdapter == null) {
            friendListAdapter = new FriendListAdapter(getActivity(), usersArrayList);
            layoutManager = new LinearLayoutManager(getActivity());
            rcFriendsList.setLayoutManager(layoutManager);
            rcFriendsList.setAdapter(friendListAdapter);
        } else {
            friendListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    public ArrayList<String> getAllContactList() {
        ArrayList<String> alContacts = new ArrayList<String>();
        ContentResolver cr = getActivity().getContentResolver(); //Activity/Application android.content.Context
        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        if (cursor.moveToFirst()) {

            do {
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));

                if (Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String contactNumber = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        alContacts.add(contactNumber.replace("-", "").replace(" ", ""));
                        break;
                    }
                    pCur.close();
                }

            } while (cursor.moveToNext());
        }
        return alContacts;
    }

    public void getReadWritePermission() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS}, REQUEST_READ_CONTACT);

            return;
        } else {
            if (allContactList.size() > 0)
                allContactList.clear();
            allContactList.addAll(getAllContactList());
            getFriendsList();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            if (allContactList.size() > 0)
                allContactList.clear();
            allContactList.addAll(getAllContactList());
            getFriendsList();
        }
    }

    private void getFriendsList() {
        mSwipeRefreshLayout.setRefreshing(true);
        if (usersArrayList.size() > 0)
            usersArrayList.clear();
        firebaseFirestore.collection("Users").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        try {
                            if (task.isSuccessful()) {
                                String strName = "";
                                for (String number : allContactList) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        if (document.get("Mobile").toString().equalsIgnoreCase(number)) {
                                            Users users = new Users();
                                            users.setName(document.get("Name").toString());
                                            users.setMobile(document.get("Mobile").toString());
                                            usersArrayList.add(users);
                                        }
                                    }
                                }
                                setFriendListLayout();
                                getGroupList();

                                //Toast.makeText(getActivity(), strName, Toast.LENGTH_SHORT).show();
                            } else {
                                mSwipeRefreshLayout.setRefreshing(true);
                            }
                        } catch (Exception e) {
                            Log.d("KK", e.toString());
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }


    @Override
    public void onRefresh() {
        getReadWritePermission();
        mSwipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnGroupAdd:
                addGroup();
                break;
        }
    }

    private void addGroup() {
        Users group = new Users();
        SharedPref.init(getActivity());
        group.setMobile(SharedPref.read(SharedPref.MOBILE, null));
        group.setName(SharedPref.read(SharedPref.NAME, null));
        group.setGroupName(SharedPref.read(SharedPref.MOBILE, null));
        createGroup(group, SharedPref.read(SharedPref.MOBILE, null));
    }

    public void getGroupList() {
        mSwipeRefreshLayout.setRefreshing(true);
        firebaseFirestore.collection("Group").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        try {
                            if (task.isSuccessful()) {
                                String strName = "";
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Users users = new Users();
                                        users.setName(document.get("Name").toString());
                                        users.setMobile(document.get("Mobile").toString());
                                        users.setGroupName(document.get("GroupName").toString());
                                        usersArrayList.add(users);
                                    }
                                setFriendListLayout();
                                //Toast.makeText(getActivity(), strName, Toast.LENGTH_SHORT).show();
                            } else {
                                mSwipeRefreshLayout.setRefreshing(true);
                            }
                        } catch (Exception e) {
                            Log.d("KK", e.toString());
                            mSwipeRefreshLayout.setRefreshing(true);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }
}
