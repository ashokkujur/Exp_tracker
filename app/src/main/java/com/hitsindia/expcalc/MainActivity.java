package com.hitsindia.expcalc;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.hitsindia.expcalc.adapters.SpinnerAdapter;
import com.hitsindia.expcalc.helper.SqliteHelper;
import com.hitsindia.expcalc.listner.OnNewRecordEntryLisnter;
import com.hitsindia.expcalc.listner.OnSelectDataFromSpinner;
import com.hitsindia.expcalc.model.ExpModel;
import com.hitsindia.expcalc.model.SuggestModel;
import com.hitsindia.expcalc.ui.home.FileUtil;
import com.idescout.sql.SqlScoutServer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.PortUnreachableException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import info.androidhive.fontawesome.FontDrawable;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private SqliteHelper sqliteHelper;
    public  OnNewRecordEntryLisnter newRecordEntryLisnter;
    private static  int PICKFILE_REQUEST_CODE = 100;
    private static  int PICKFILE_RESULT_CODE = 1002;
    private static  int PICKCAMERA_REQUEST_CODE = 1001;
    private static  int PICKCAMERA_RESULT_CODE = 1003;
    private String fileName = null;

    private SqlScoutServer sqlScoutServer;

    private Bitmap photo = null;
    private File destination = new File("/sdcard/Expense Tracker/");
    private File source,destFile;
    private boolean isSelectFromFile = true;
    private TextView tvSelectFile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sqlScoutServer = SqlScoutServer.create(this, getPackageName());

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setLogo(R.drawable.wallet_24);
        sqliteHelper = new SqliteHelper(this,null,null,0);

        toolbar.setNavigationIcon(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(false);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View view) {
               final Dialog dialog = new Dialog(MainActivity.this);
               dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
               dialog.setContentView(R.layout.add_new_expense_activity);

               dialog.show();
               final TextView tvAdd  = dialog.findViewById(R.id.tvAdd);
               final TextView tvNext = dialog.findViewById(R.id.tvNext);
               final TextView tvCancel = dialog.findViewById(R.id.tvCancel);
               final ImageView ivAttachment = dialog.findViewById(R.id.ivAttachment);
               final LinearLayout llSelectFile = dialog.findViewById(R.id.llSelectFile);
               final TextView tvSelectFile = dialog.findViewById(R.id.tvSelectFile);
               final ImageView ivClip = dialog.findViewById(R.id.ivClip);
               ivClip.setVisibility(View.VISIBLE);
               llSelectFile.setOnClickListener(v ->uploadFile(MainActivity.this,tvSelectFile,ivAttachment) );
               final TextView tvSpinnerSelected = dialog.findViewById(R.id.tvSpinnerSelected);
               final com.hitsindia.expcalc.helper.Spinner spCategoryList = dialog.findViewById(R.id.spCategoryList);

                ArrayList<String> list= SqliteHelper.getInstanciate(MainActivity.this).getCategory();
                SpinnerAdapter spinnerAdapter = new SpinnerAdapter(list,MainActivity.this );
                spinnerAdapter.setOnDataListner(new OnSelectDataFromSpinner() {
                    @Override
                    public void onSelected(String strCategory) {
                        spCategoryList.dismiss();
                        tvSpinnerSelected.setText(strCategory);
                    }
                });
                spCategoryList .setAdapter(spinnerAdapter);

                tvCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
               final EditText etAmount = dialog.findViewById(R.id.etAmount);

               AutoCompleteTextView etDesc = (AutoCompleteTextView) dialog.findViewById(R.id.etDesc);
               ArrayList<SuggestModel> models = SqliteHelper.getInstanciate(MainActivity.this).getSugest();
               String strSuggest[] = new String[models.size()];
               for(int i = 0;i<models.size();i++){
                   strSuggest[i] = models.get(i).getStrSuggest();
               }
               ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(MainActivity.this,R.layout.support_simple_spinner_dropdown_item,strSuggest);
               etDesc.setAdapter(arrayAdapter);
               etDesc.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        etDesc.showDropDown();
                    }
                });
                etAmount.requestFocus();
                tvAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(isSelectFromFile){
                            try {
                                copyFile(source, destFile);
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                        else{
                            persistImage(photo,"test",destination);
                        }
                          if( SqliteHelper.getInstanciate(MainActivity.this).addExp(new ExpModel(etDesc.getText().toString(),Long.parseLong(etAmount.getText().toString()),new SimpleDateFormat("dd/MM/yyyy").format(new Date()),tvSpinnerSelected.getText().toString(),fileName))) {
                              SqliteHelper.getInstanciate(MainActivity.this).getnewRecordEntryLisnter().onSaved(0);
                              if(!etDesc.getText().toString().isEmpty())
                                SqliteHelper.getInstanciate(MainActivity.this).insertSuggest(etDesc.getText().toString());

                              Toast.makeText(MainActivity.this,"New record saved successfully!", Toast.LENGTH_SHORT).show();
                              dialog.dismiss();
                          }
                    }
                });

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == PICKFILE_RESULT_CODE) {
                if (data != null) {
                    try {
                        Uri uriSelectedFile = data.getData();
                        source = FileUtil.from(this, uriSelectedFile);
                        if (!destination.exists()) {
                            destination.mkdirs();
                        }
                        destFile = new File(destination, FileUtil.getFileName(MainActivity.this, uriSelectedFile));
                        this.fileName = destination+"/" + FileUtil.getFileName(MainActivity.this, uriSelectedFile);
                        isSelectFromFile = true;
                        tvSelectFile.setText(fileName);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else if (requestCode == PICKCAMERA_RESULT_CODE) {
                photo = (Bitmap) data.getExtras().get("data");
                isSelectFromFile = false;
            }
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode ==  PICKFILE_REQUEST_CODE) {
            if (grantResults[0] == 0 && grantResults[1] == 0) {
                pickFile();
            }
        } else if(requestCode == PICKCAMERA_REQUEST_CODE){
            if (grantResults[0] == 0) {
                cameraPick();
            }
        }
    }

    public boolean checkStoragePermission(){
        if(getApplicationContext().checkCallingOrSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && getApplicationContext().checkCallingOrSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            return true;
        }
        return false;
    }
    public boolean checkCameraPermission(){
        if( getApplicationContext().checkCallingOrSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
            return true;
        }
        return false;
    }
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
    public void setIvIcon(TextView v,int intId,int intColor) {
        FontDrawable drawable = new FontDrawable(this.getApplicationContext(),intId, true, false);
        drawable.setTextColor(intColor);
        drawable.setTextSize(16);
        v.setBackground(drawable);
    }
    private void showpopupwindows(final Activity context,RelativeLayout viewGroup) {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.category_list_view,
                (ViewGroup) viewGroup);

        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }
    public void uploadFile(AppCompatActivity context,TextView textView,ImageView ivAttachment){
        Dialog dialog = new Dialog(context);
        Window window = dialog.getWindow();
        dialog.setContentView(R.layout.dialog_file_selection);
        final TextView tvCancel = dialog.findViewById(R.id.tvCancel);
        tvCancel.setOnClickListener(v -> dialog.dismiss());
        final TextView tvOk = dialog.findViewById(R.id.tvOk);
        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                MimeTypeMap myMime ;
                String mimeType = "None";

                if(fileName != null){
                    myMime = MimeTypeMap.getSingleton();
                    mimeType = myMime.getMimeTypeFromExtension(fileExt(fileName));
                }
                if(mimeType.equals("application/pdf")){
                    ivAttachment.setBackgroundResource(R.drawable.pdf);
                }
                else if(mimeType.equals("application/xls") || mimeType.equals("application/xlsx")){
                    ivAttachment.setBackgroundResource(R.drawable.xls);
                }
                else if(mimeType.equals("application/doc")){
                    ivAttachment.setBackgroundResource(R.drawable.doc);
                }
                else if(mimeType.equals("image/png") || mimeType.equals("image/jpeg")){
                    try {
                        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                        StrictMode.setVmPolicy(builder.build());

                        ivAttachment.setBackground(new BitmapDrawable(context.getResources(), MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.fromFile(new java.io.File(fileName)))));
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
                textView.setText(fileName);
            }
        });

        final LinearLayout llCamera = dialog.findViewById(R.id.llCamera);
        llCamera.setOnClickListener(v -> cameraPick());
        final LinearLayout llFolder = dialog.findViewById(R.id.llFolder);
        llFolder.setOnClickListener(v -> pickFile());
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        dialog.show();
    }
    private void copyFile(File source, File destination) throws FileNotFoundException, IOException {

        FileChannel in = new FileInputStream(source).getChannel();
        FileChannel out = new FileOutputStream(destination).getChannel();
        try {
            in.transferTo(0, in.size(), out);
        } catch(Exception e){
            e.printStackTrace();
        } finally {
            if (in != null)
                in.close();
            if (out != null)
                out.close();
        }
    }
    public void pickFile(){
        if(checkStoragePermission()) {
            Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
            chooseFile.setType("*/*");
            String[] extraMimeTypes = {"application/pdf","application/xls", "application/.xlsx", "application/doc","image/png", "image/jpeg",};
            chooseFile.putExtra(Intent.EXTRA_MIME_TYPES, extraMimeTypes);
            chooseFile.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            chooseFile = Intent.createChooser(chooseFile, "Choose a file");
            startActivityForResult(chooseFile, PICKFILE_RESULT_CODE);
        }
        else{
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA},
                    PICKFILE_REQUEST_CODE );
        }
    }
    private void cameraPick(){
        if(checkCameraPermission()) {
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, PICKCAMERA_RESULT_CODE);
        }
        else
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE}, PICKCAMERA_REQUEST_CODE );
    }
    private  void persistImage(Bitmap bitmap, String name,File fileName) {
        this.fileName = "/"+ name + ".jpg";
        File imageFile = new File(fileName, this.fileName);
        tvSelectFile.setText(this.fileName);
        OutputStream os;
        try {
            os = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.flush();
            os.close();
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Error writing bitmap", e);
        }
    }
    public  void desrHint(View v,Context context){
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View customView = layoutInflater.inflate(R.layout.category_list_view,null);
        PopupWindow popupWindow = new PopupWindow(context);
        popupWindow.setContentView(customView);
        popupWindow.showAtLocation(v, Gravity.CENTER,0,0);

    }
    private String fileExt(String url) {
        if (url.indexOf("?") > -1) {
            url = url.substring(0, url.indexOf("?"));
        }
        if (url.lastIndexOf(".") == -1) {
            return null;
        } else {
            String ext = url.substring(url.lastIndexOf(".") + 1);
            if (ext.indexOf("%") > -1) {
                ext = ext.substring(0, ext.indexOf("%"));
            }
            if (ext.indexOf("/") > -1) {
                ext = ext.substring(0, ext.indexOf("/"));
            }
            return ext.toLowerCase();

        }
    }
}
