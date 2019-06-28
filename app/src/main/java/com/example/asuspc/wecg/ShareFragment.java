package com.example.asuspc.wecg;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.Objects;

import static android.support.constraint.Constraints.TAG;
import static com.example.asuspc.wecg.MainActivity.FILE_UPLOADED;

public class ShareFragment extends Fragment {
    public ArrayAdapter<String> mUploadAdapter;
    private ListView mUploadListView;
    private StorageReference mStorageRef;
    private FragmentMessage mCallback;

    //fragment's view
    View view;

    //defining firebaseauth object
    private FirebaseAuth firebaseAuth;
    String user_id;

    //defining firestore oject
    private FirebaseFirestore firebaseFirestore;

    private final static String RECORDING_KEY = "records";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_share, container, false);
        return view;
    }

    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        //set title for toolbar
        getActivity().setTitle(R.string.action_share);

        final boolean readPermissionGranted;
        if (isReadStoragePermissionGranted()) {
            readPermissionGranted = true;
        } else {
            readPermissionGranted = false;
        }

        mStorageRef = FirebaseStorage.getInstance().getReference();


        mUploadAdapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_list_item_1);
        mUploadListView = (ListView)view.findViewById(R.id.uploadListView);
        mUploadListView.setSelector(R.drawable.list_bg);
        mUploadListView.setAdapter(mUploadAdapter);
        mUploadListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                showUploadDialog(mUploadAdapter.getItem(i),i);
            }
        });

        //getting list of files
        if(readPermissionGranted == true){
            displayListOfFiles();
        } else {
            boolean secondPermission;
            secondPermission = isReadStoragePermissionGranted();
            if(secondPermission == false){
                Toast.makeText(view.getContext(), "Cannot read storage", Toast.LENGTH_SHORT).show();
            }
        }


        //initializing firebase auth object
        firebaseAuth = FirebaseAuth.getInstance();

        firebaseFirestore = FirebaseFirestore.getInstance();

    }

    public void displayListOfFiles() {
        File dir = new File(Environment.getExternalStorageDirectory().getPath()+File.separator+"Kardium");
        File[] fileList = dir.listFiles();
        if(fileList != null) {
            String[] theNamesOfFiles = new String[fileList.length];
            for (int i = 0; i < theNamesOfFiles.length; i++) {
                theNamesOfFiles[i] = fileList[i].getName();
            }
            mUploadAdapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_list_item_1, theNamesOfFiles);
            mUploadListView.setAdapter(mUploadAdapter);
        }
    }

    private void showUploadDialog(final String item, final int position) {

        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setMessage("Upload this file?")
                .setCancelable(true)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        uploadFile(item, position);
                    }
                }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        //Creating dialog box
        AlertDialog alert = builder.create();
        //Setting the title manually
        alert.setTitle(R.string.app_name);
        alert.show();
    }

    //this method will upload the file
    private void uploadFile(final String item, final int position) {
        final Uri filePath = Uri.fromFile(new File(Environment.getExternalStorageDirectory().getPath()+File.separator+"Kardium"+File.separator+item));
        //if there is a file to upload
        if (filePath != null) {
            //displaying a progress dialog while upload is going on
            final ProgressDialog progressDialog = new ProgressDialog(view.getContext());
            progressDialog.setTitle("Uploading");
            progressDialog.show();

            final StorageReference riversRef = mStorageRef.child(item);
            final UploadTask uploadTask = riversRef.putFile(filePath);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //if the upload is successful
                            //hiding the progress dialog
                            progressDialog.dismiss();
                            addRecording(item);
                            mUploadListView.setSelection(position);
                            //and displaying a success toast
                            Toast.makeText(view.getContext(), "File Uploaded", Toast.LENGTH_LONG).show();
                            Bundle bundle = new Bundle();
                            bundle.putString(FILE_UPLOADED, "new file uploaded");
                            mCallback.sentMessage(bundle);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            //if the upload is not successful
                            //hiding the progress dialog
                            progressDialog.dismiss();

                            //and displaying error message
                            Toast.makeText(view.getContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            //calculating progress percentage
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                            //displaying percentage in progress dialog
                            progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                        }
                    });
        }
    }

    private void addRecording(String nameOfFile) {
        String trimmedName = nameOfFile.substring(0,15);
        user_id = firebaseAuth.getCurrentUser().getUid();
        DocumentReference user = firebaseFirestore.collection("patients").document(user_id);

        user.update(RECORDING_KEY, FieldValue.arrayUnion(trimmedName));
    }

    public  boolean isReadStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (Objects.requireNonNull(getActivity()).checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted");
                return true;
            } else {

                Log.v(TAG,"Permission is revoked");
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 3);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted");
            return true;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentMessage) {
            mCallback = (FragmentMessage) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement FragmentMessage");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }
}
