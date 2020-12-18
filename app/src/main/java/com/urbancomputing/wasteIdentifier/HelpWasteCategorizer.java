package com.urbancomputing.wasteIdentifier;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

public class HelpWasteCategorizer extends AppCompatActivity {
    private static final int CAMERA_REQUEST_CODE = 101;
    Button Img_choose, Img_upload;
    ImageView Image_View;
    StorageReference storage_ref;
    String image_url;
    private StorageTask uploadTask;
    private Uri image_uri;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_waste_categorizer);

        storage_ref = FirebaseStorage.getInstance().getReference("Unclassified Waste Images");
        Img_choose = findViewById(R.id.chse);
        Img_upload = findViewById(R.id.upld);
        Image_View = findViewById(R.id.imgv);
        Img_choose.setOnClickListener(new View.OnClickListener() {
                                          @Override
                                          public void onClick(View view) {
                                              HelpWasteCategorizer.this.browse_upload_image();
                                              Img_upload.setVisibility(View.VISIBLE);
                                          }
                                      }
        );

        Img_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (uploadTask != null && uploadTask.isInProgress()) {
                    Toast.makeText(HelpWasteCategorizer.this, "Image upload in process!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(HelpWasteCategorizer.this, "Image is being Uploaded!", Toast.LENGTH_LONG).show();
                    HelpWasteCategorizer.this.upload_image();
                }
            }
        });
    }

    private String getExtension(Uri uri){
        ContentResolver cr = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cr.getType(uri));
    }

//    public void onRadioButtonClicked(View view) {
//        // Is the button now checked?
//        boolean checked = ((RadioButton) view).isChecked();
//
//        // Check which radio button was clicked
//        switch(view.getId()) {
//            case R.id.radioButton1:
//                if (checked)
//                    storage_ref = FirebaseStorage.getInstance().getReference("Organic Waste Images");
//                    break;
//            case R.id.radioButton2:
//                if (checked)
//                    storage_ref = FirebaseStorage.getInstance().getReference("Inorganic Waste Images");
//                    break;
//            case R.id.radioButton3:
//                if (checked)
//                    storage_ref = FirebaseStorage.getInstance().getReference("Unclassified Waste Images");
//                    break;
//        }
//    }

    private void upload_image() {
        StorageReference Ref = storage_ref.child(System.currentTimeMillis() + "." + getExtension(image_uri));
        uploadTask = Ref.putFile(image_uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        // Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        Toast.makeText(HelpWasteCategorizer.this, "Image Uploaded Successfully!", Toast.LENGTH_LONG).show();
                        HelpWasteCategorizer.this.recreate();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // TODO
                        // please add : after specific number of button click restart activity
                        Toast.makeText(HelpWasteCategorizer.this, "Failed to Upload!...Please Try again!" + exception.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void browse_upload_image(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, CAMERA_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            image_uri = data.getData();
            Image_View.setImageURI(image_uri);
        }
    }
}