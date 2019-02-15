package com.prospec.edupro;

import android.support.v7.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.TextInputEditText;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.prospec.edupro.Utils.UserParcelable;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {
//    ประกาศตัวแปร
    private TextView loginLink;
    private ImageView imagePhoto;
    private TextInputEditText password;
    private TextInputEditText nombre;
    private TextInputEditText email;
    private Button registrar;
    private int request_code = 1;
    private Bitmap bitmap;
    private ProgressDialog progreso;
    RequestQueue requestQueue; //อนุญาตการเชื่อมต่อโดยตรงจากบริการบนเว็บ
    StringRequest stringRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        getevent();
    }

    private void getevent() {
        imagePhoto = (ImageView) findViewById(R.id.usuario_imagen_registro);
        loginLink = (TextView)findViewById(R.id.link_login);
        email = (TextInputEditText)findViewById(R.id.correo_registro);
        password = (TextInputEditText)findViewById(R.id.password_registro);
        nombre = (TextInputEditText)findViewById(R.id.nombre_registro);
        registrar = (Button)findViewById(R.id.btn_registro_usuario);

        requestQueue = Volley.newRequestQueue(this);

        registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Registrar();
            }
        });

        imagePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = null;
                //การตรวจสอบเวอร์ชันของแพลตฟอร์ม
                if(Build.VERSION.SDK_INT < 19){
                    //Android 4.3 และรุ่นก่อนหน้า
                    i = new Intent();
                    i.setAction(Intent.ACTION_GET_CONTENT);
                }else {
                    //Android 4.4 และสูงกว่า
                    i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    i.addCategory(Intent.CATEGORY_OPENABLE);
                }
                i.setType("image/*");
                startActivityForResult(i, request_code);
            }
        });

//        มีบัฐชีแล้ว
        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                finish();
            }
        });
    }

    private void Registrar() {

        if (!validar()) return;

        progreso = new ProgressDialog(this);
        progreso.setMessage("รอสักครู่...");
        progreso.show();
        String url = "http://192.168.1.5/movil_database/register_movil.php?";

        stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                UserParcelable userParcelable = new UserParcelable();;
                Log.i("Register JSON: ",""+response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.names().get(0).equals("success")){
                        email.setText("");
                        nombre.setText("");
                        password.setText("");
                        userParcelable.setId(jsonObject.getJSONArray("usuario").getJSONObject(0).getInt("iduser_"));
                        userParcelable.setEmail(jsonObject.getJSONArray("usuario").getJSONObject(0).getString("email"));
                        userParcelable.setNombre(jsonObject.getJSONArray("usuario").getJSONObject(0).getString("nombres"));
                        userParcelable.setImage(jsonObject.getJSONArray("usuario").getJSONObject(0).getString("photo"));

                        Toast.makeText(getApplicationContext(),jsonObject.getString("success"),Toast.LENGTH_SHORT).show();
                        progreso.dismiss();

                        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                        intent.putExtra("DATA_USER",userParcelable);
                        startActivity(intent);
                        finish();
                    }else{
                        Toast.makeText(getApplicationContext(),jsonObject.getString("error"),Toast.LENGTH_SHORT).show();
                        Log.i("RESPUESTA JSON: ",""+jsonObject.getString("error"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                progreso.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"No se ha podido conectar",Toast.LENGTH_SHORT).show();
                Log.i("ERROR: ",""+error.toString());
                progreso.dismiss();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {//para enviar los datos mediante POST
                String sEmail = email.getText().toString();
                String sPassword =  password.getText().toString();
                String sNombre = nombre.getText().toString();
                String  sImagePhoto = convertirImgString(bitmap);

                Map<String,String> parametros = new HashMap<>();
                parametros.put("email",sEmail);
                parametros.put("password",sPassword);
                parametros.put("photo",sImagePhoto);
                parametros.put("nombres",sNombre);
                //พารามิเตอร์เหล่านี้จะถูกส่งไปยังบริการเว็บของบริษัท

                return parametros;
            }
        };

        requestQueue.add(stringRequest);
    }

    private String convertirImgString(Bitmap bitmap) {

        String imagenString;
        ByteArrayOutputStream array=new ByteArrayOutputStream();
        if(bitmap!=null){
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,array);
            byte[] imagenByte=array.toByteArray();
            imagenString= Base64.encodeToString(imagenByte,Base64.DEFAULT);
        }else{
            imagenString = "no image"; //สายนี้จะถูกส่งในกรณีที่ไม่มีภาพ
        }

        return imagenString;
    }

    private boolean validar() {
        boolean valid = true;

        String sNombre = nombre.getText().toString();
        String sPassword = password.getText().toString();
        String sEmail = email.getText().toString();

        if (sNombre.isEmpty() || sNombre.length() < 3) {
            nombre.setError("ป้อนอย่างน้อย 3 ตัวอักษร");
            valid = false;
        } else {
            nombre.setError(null);
        }

        if (sEmail.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(sEmail).matches()) {
            email.setError("ที่อยู่อีเมลไม่ถูกต้อง");
            valid = false;
        } else {
            email.setError(null);
        }

        if (sPassword.isEmpty() || password.length() < 4 || password.length() > 8) {
            password.setError("ป้อนตัวอักษรและตัวเลขระหว่าง 4 ถึง 8 ตัว");
            valid = false;
        } else {
            password.setError(null);
        }

        return valid;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode == RESULT_OK && requestCode == request_code){
            imagePhoto.setImageURI(data.getData());

            try{
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),data.getData());
                imagePhoto.setImageBitmap(bitmap);
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
