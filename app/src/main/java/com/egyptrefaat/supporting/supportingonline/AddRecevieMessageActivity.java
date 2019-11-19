package com.egyptrefaat.supporting.supportingonline;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.egyptrefaat.supporting.supportingonline.Calls.ErrorCall;
import com.egyptrefaat.supporting.supportingonline.Custom.MyRequest;
import com.egyptrefaat.supporting.supportingonline.Custom.MySharedPref;
import com.egyptrefaat.supporting.supportingonline.Custom.Myvollysinglton;
import com.egyptrefaat.supporting.supportingonline.Custom.OnErrorRequest;
import com.egyptrefaat.supporting.supportingonline.Value.Image;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class AddRecevieMessageActivity extends AppCompatActivity {

    private ImageView imageView;
    private Button btn_upload,btn_send;
    private boolean isUploaded;
    private Bitmap bitmap;
    private String id,type,date,groupIdUpdate;
    private int position,countUser;
    private ProgressDialog progressDialog;

    private Socket mSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recevie_message);

        //id
        id=getIntent().getStringExtra("id");
        groupIdUpdate=getIntent().getStringExtra("group_update");
        countUser=getIntent().getIntExtra("count_user",0);
        position=getIntent().getIntExtra("position",0);
        type=getIntent().getStringExtra("type");
        date=getIntent().getStringExtra("date");

        // progress dialog
        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(R.string.loading));

        // image
        imageView=(ImageView)findViewById(R.id.add_receive_image);

        // btn upload
        btn_upload=(Button)findViewById(R.id.add_receive_upload);
        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(AddRecevieMessageActivity.this);

            }
        });




        // send
        btn_send=(Button)findViewById(R.id.add_receive_send);
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    sendPay(isUploaded);

            }
        });
    }

    private void socketReady() {
        try {
            mSocket = IO.socket(getResources().getString(R.string.socket));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        mSocket.connect();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri path = result.getUri();


                try {
                     bitmap = MediaStore.Images.Media.getBitmap(AddRecevieMessageActivity.this.getContentResolver(), path);

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);


                    imageView.setBackgroundResource(0);
                    imageView.setImageBitmap(bitmap);
                    isUploaded=true;


                } catch (IOException e) {

                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();

            }
        }
    }

    private void sendPay(boolean withPhoto){
        progressDialog.show();

       /* ((GroupContentActivity)).restart();
        finish();*/

        String url=getResources().getString(R.string.domain)+"api/save_receive";

        StringRequest request=new MyRequest(MySharedPref.getdata(AddRecevieMessageActivity.this, "token"), 1, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
              //  Log.i("receive",response);
                progressDialog.dismiss();

                try {
                    JSONObject object=new JSONObject(response);
                    if (object.has("success")){
                        sendsocketEmmit();
                        startActivity(new Intent(AddRecevieMessageActivity.this,GroupContentActivity.class)
                        .putExtra("id",id).putExtra("date",date)
                                .putExtra("type",type)
                                .putExtra("position",position).putExtra("count_user",countUser));
                        overridePendingTransition(R.anim.fadin, R.anim.slide_down);
                        finish();

                    }else if (object.has("error")){
                        Toast.makeText(AddRecevieMessageActivity.this, object.getString("error"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        },new OnErrorRequest(AddRecevieMessageActivity.this, new ErrorCall() {
            @Override
            public void OnBack() {
                progressDialog.dismiss();

            }
        })){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map=new HashMap<>();
                if (withPhoto){
                    map.put("image", Image.imagetobinary(bitmap));
                }
               map.put("group_id",id);
                return map;
            }
        };
        Myvollysinglton.getInstance(this).addtorequst(request);

    }

    private void sendsocketEmmit() {

        JSONObject object=new JSONObject();
        try {
            object.put("type","group");
            object.put("group_id",groupIdUpdate);
            object.put("device","nagy");
            object.put("user_id",id);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i("send",object.toString());

        mSocket.emit("supportingonline",object);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(AddRecevieMessageActivity.this,GroupContentActivity.class)
                .putExtra("id",id).putExtra("date",date)
                .putExtra("type",type)
                .putExtra("position",position).putExtra("count_user",countUser));
        overridePendingTransition(R.anim.fadin, R.anim.slide_down);
        finish();

    }
}
