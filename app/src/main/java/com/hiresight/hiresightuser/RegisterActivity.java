package com.hiresight.hiresightuser;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.ByteArrayOutputStream;

public class RegisterActivity extends AppCompatActivity {
    public static final String TAG = "TAG";
    private static int PICK_IMAGE = 1;
    private FirebaseFirestore db;
    private DocumentReference documentReference;
    private FirebaseAuth auth;
    private StorageReference storageReference;
    private StorageReference imgStore;
    private Button registerBtn;
    private ImageView photoBtn;
    private EditText nameText, ageText, contactText, yearsExText, emailText, passText;
    private CheckBox checkMale, checkFemale;
    private String imageUrl;
    private String gender;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        nameText = (EditText) findViewById(R.id.nameText);
        ageText = (EditText) findViewById(R.id.ageText);
        checkMale = (CheckBox) findViewById(R.id.checkMale);
        checkFemale = (CheckBox) findViewById(R.id.checkFemale);
        contactText = (EditText) findViewById(R.id.contactText);
        yearsExText = (EditText) findViewById(R.id.yearsExText);
        emailText = (EditText) findViewById(R.id.emailText);
        passText = (EditText) findViewById(R.id.passText);
        registerBtn = (Button) findViewById(R.id.registerBtn);
        AddData();
    }

    /*public void selectImage() {
        photoBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
                        getIntent.setType("image/*");
                        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        pickIntent.setType("image/*");
                        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
                        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});
                        startActivityForResult(chooserIntent, PICK_IMAGE);

                    }
                }
        );

    }

     */


    public void AddData() {
        registerBtn.setOnClickListener(
                 new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (checkMale.isChecked())
                            gender = "Male";
                        if (checkFemale.isChecked())
                            gender = "Female";
                        auth.createUserWithEmailAndPassword(emailText.getText().toString(), passText.getText().toString())
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            String userID = auth.getCurrentUser().getUid();

                                            Users user = new Users(nameText.getText().toString(), Integer.valueOf(ageText.getText().toString()), gender, contactText.getText().toString(),
                                                    Integer.valueOf(yearsExText.getText().toString()), emailText.getText().toString(), passText.getText().toString(), null);
                                            DocumentReference documentReference = db
                                                    .collection("Users").document(userID);
                                            documentReference.set(user)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                Log.d("Firestore", "User profile stored!");
                                                            } else
                                                                Log.d("Firestore", task.getException().getMessage());
                                                        }
                                                    });
                                            Intent startIntent = new Intent(getApplicationContext(), UploadUserImageActivity.class);
                                            startActivity(startIntent);
                                            //Toast.makeText(getApplicationContext(), "Account created!", Toast.LENGTH_LONG).show();

                                        } else {
                                            Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });

                    }
                }

        );
    }


/*
    @Override
    protected void onActivityResult ( int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();

            try {
                photoBtn.setImageURI(uri);
                android.view.ViewGroup.LayoutParams layoutParams = photoBtn.getLayoutParams();
                layoutParams.width = 200;
                layoutParams.height = 200;
                photoBtn.setLayoutParams(layoutParams);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public String uploadImage (String userID){

        photoBtn.setDrawingCacheEnabled(true);
        photoBtn.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) photoBtn.getDrawable()).getBitmap();
        imgStore = storageReference.child("user_image").child(userID + ".jpg");
        UploadTask uploadTask = imgStore.putBytes(imageViewToByte(bitmap));

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return imgStore.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    imageUrl = downloadUri.toString();
                    String userID = auth.getCurrentUser().getUid();
                    documentReference = db.collection("Users").document(userID);
                    documentReference
                            .update("imageURL", imageUrl)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "DocumentSnapshot successfully updated!");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error updating document", e);
                                }
                            });

                } else {
                    Log.d("image", "upload image error.");
                }
            }
        });
        return imageUrl;
    }




    public byte[] imageViewToByte (Bitmap bitmap){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }
*/


}
