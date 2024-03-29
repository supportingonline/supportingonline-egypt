package com.egyptrefaat.supporting.supportingonline;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.egyptrefaat.supporting.supportingonline.Adapters.EmojiAdapter;
import com.egyptrefaat.supporting.supportingonline.Calls.ErrorCall;
import com.egyptrefaat.supporting.supportingonline.Calls.OnPress;
import com.egyptrefaat.supporting.supportingonline.Custom.MyRequest;
import com.egyptrefaat.supporting.supportingonline.Custom.MySharedPref;
import com.egyptrefaat.supporting.supportingonline.Custom.MySizes;
import com.egyptrefaat.supporting.supportingonline.Custom.Myvollysinglton;
import com.egyptrefaat.supporting.supportingonline.Custom.OnErrorRequest;
import com.egyptrefaat.supporting.supportingonline.Custom.SpaceRecycler_V;
import com.egyptrefaat.supporting.supportingonline.Custom.URLImageParser;
import com.egyptrefaat.supporting.supportingonline.Fragments.TimeLineFragment;
import com.egyptrefaat.supporting.supportingonline.Models.EmojiModel;
import com.egyptrefaat.supporting.supportingonline.Value.Image;
import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.XMLReader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;
import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;

public class WritePostActivity extends AppCompatActivity {

    private ImageView imagecapture,imageview,cancelimage;
    private String type="text";
    private Bitmap bitmap;
    private RelativeLayout layoutimage;
    private Button button;
    private String domain,token;
    private ProgressDialog prog;

    EmojiconEditText emojiconEditText;
    ImageView emojiButton;
    View rootView;
    EmojIconActions emojIcon;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_post);


        // domain and token
        domain=getResources().getString(R.string.domain);
        token= MySharedPref.getdata(this,"token");

        // progress dialog
        prog=new ProgressDialog(this);
        prog.setTitle(getResources().getString(R.string.loading));

        // btn share
        button=(Button)findViewById(R.id.write_btnshare);



        rootView = findViewById(R.id.root_view);
        emojiButton = (ImageView) findViewById(R.id.emoji_btn);
       // submitButton = (ImageView) findViewById(R.id.submit_btn);
      //  mCheckBox = (CheckBox) findViewById(R.id.use_system_default);
        emojiconEditText = (EmojiconEditText) findViewById(R.id.emojicon_edit_text);
       // emojiconEditText2 = (EmojiconEditText) findViewById(R.id.emojicon_edit_text2);
       // textView = (EmojiconTextView) findViewById(R.id.textView);
        emojIcon = new EmojIconActions(this, rootView, emojiconEditText, emojiButton);
        emojIcon.ShowEmojIcon();
        emojIcon.setKeyboardListener(new EmojIconActions.KeyboardListener() {
            @Override
            public void onKeyboardOpen() {
                Log.e("Keyboard", "open");
            }

            @Override
            public void onKeyboardClose() {
                Log.e("Keyboard", "close");
            }
        });




        emojiconEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String ss= charSequence.toString().trim();

                if (ss.length()>0){
                  button.setAlpha(1f);
                }else {
                    button.setAlpha(0.5f);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        // capture
        imagecapture=(ImageView) findViewById(R.id.write_capture);
        imagecapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(WritePostActivity.this);
            }
        });


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s=emojiconEditText.getText().toString().trim();


                if (type.equals("text")){

                    if (s.length()>0){

                        sharePost(s);
                    }

                }else if (type.equals("image")){
                    sharePost(s);
                }
            }
        });




        // image view
        imageview=(ImageView)findViewById(R.id.write_image);



        // layout image
        layoutimage=(RelativeLayout)findViewById(R.id.write_imagelayout);





        // canecl image
        cancelimage=(ImageView)findViewById(R.id.write_imagecancel);
        cancelimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layoutimage.setVisibility(View.GONE);
                type="text";
            }
        });


    }





    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri path = result.getUri();


                try {
                    bitmap = MediaStore.Images.Media.getBitmap(WritePostActivity.this.getContentResolver(), path);

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);


                    layoutimage.setVisibility(View.VISIBLE);
                    button.setAlpha(1f);
                    imageview.setImageBitmap(bitmap);
                    type = "image";




                } catch (IOException e) {

                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();

            }
        }

    }



    private  void sharePost(String s){
       // Log.i("image",imagetobinary(bitmap));
        prog.show();
        String url=domain+"api/post";
        StringRequest request=new MyRequest(token, 1, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                prog.dismiss();
                Log.i("share",response);
                try {
                    JSONObject object=new JSONObject(response);
                    if (object.has("success")){
                        String succ=object.getString("success");
                        if (succ.equals("done")){

                            TimeLineFragment fragment = (TimeLineFragment) HomeActivity.fragments.get(0);
                            fragment.reload();

                            onBackPressed();
                        }else {

                            Toast.makeText(WritePostActivity.this, "ERRor("+succ+")", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException e) {
                    prog.dismiss();
                    Toast.makeText(WritePostActivity.this, response, Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

            }
        }, new OnErrorRequest(WritePostActivity.this, new ErrorCall() {
            @Override
            public void OnBack() {
                prog.dismiss();

            }
        })){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String,String> map=new HashMap<>();
                map.put("type",type);
                map.put("text",s.replace("\n","<br/>"));
                if (type.equals("image")){
                    map.put("image", Image.imagetobinary(bitmap));
                }
                map.put("color","");
                return map;
            }
        };
        Myvollysinglton.getInstance(this).addtorequst(request);
    }





    public void hidekeyboadr(View v){

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }






}
