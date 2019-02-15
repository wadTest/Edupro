package com.prospec.edupro;

import android.support.v7.app.AppCompatActivity;
import android.support.design.widget.TextInputEditText;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
//    ประกาศตัวแปร
    private Button acceder;
    private TextView registrar;
    private TextInputEditText email;
    private TextInputEditText password;
    private ProgressDialog progreso;
    private RequestQueue requestQueue;
    StringRequest stringRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getevent();
    }

    private void getevent() {
//        Get Event
            email = (TextInputEditText)findViewById(R.id.etusuario);
            password = (TextInputEditText)findViewById(R.id.etpass);
            acceder = (Button)findViewById(R.id.btn_acceder);
            registrar = (TextView)findViewById(R.id.signup);
            requestQueue = Volley.newRequestQueue(this);

//            กดปุ่ม register เพื่อทำการลงทะเบียน
            registrar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent =new Intent(getApplicationContext(),SignupActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                    finish();
                }
            });

//            กรณีที่ลงทะเบียนแล้วกรอก email password จากนั้นกดLogin
            acceder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    iniciar();
                }
            });
        }

    private void iniciar() {

        if (!validar()) return;

        progreso = new ProgressDialog(this);
        progreso.setMessage("รอสักครู่...");
        progreso.show();
        String url = "http://192.168.1.5/movil_database/login_movil.php?";

        stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                UserParcelable userParcelable = new UserParcelable();;
//                lod ดู Register JSON ว่าใช้งานได้มั้ย
                Log.i("Register JSON: ",""+response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.names().get(0).equals("success")){
                        email.setText("");
                        password.setText("");
//                        เรียกข้อมูลJSON มาเช็ค
                        userParcelable.setId(jsonObject.getJSONArray("usuario").getJSONObject(0).getInt("iduser_"));
                        userParcelable.setEmail(jsonObject.getJSONArray("usuario").getJSONObject(0).getString("email"));
                        userParcelable.setNombre(jsonObject.getJSONArray("usuario").getJSONObject(0).getString("nombres"));
                        userParcelable.setImage(jsonObject.getJSONArray("usuario").getJSONObject(0).getString("photo"));

                        Toast.makeText(getApplicationContext(),jsonObject.getString("เข้าสู่ระบบสำเร็จ"),Toast.LENGTH_SHORT).show();
                        progreso.dismiss();

                        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                        intent.putExtra("DATA_USER",userParcelable);
                        startActivity(intent);
                        finish();
                    }else{
                        Toast.makeText(getApplicationContext(),jsonObject.getString("error"),Toast.LENGTH_SHORT).show();
//                        log แสดงในกรณีที่เกิดความผิดพลาด
                        Log.i("Register JSON: ",""+jsonObject.getString("error"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                progreso.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"ไม่สามารถเชื่อมต่อ",Toast.LENGTH_SHORT).show();
                progreso.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {//เพื่อส่งข้อมูลโดย POST
                String sEmail = email.getText().toString();
                String sPassword =  password.getText().toString();

                Map<String,String> parametros = new HashMap<>();
                parametros.put("email",sEmail);
                parametros.put("password",sPassword);
                //พารามิเตอร์เหล่านี้จะถูกส่งไปยังบริการเว็บของบริษัท

                return parametros;
            }
        };

        requestQueue.add(stringRequest);
    }

    private boolean validar() {
        boolean valid = true;

        String sEmail = email.getText().toString();
        String sPassword = password.getText().toString();

        if (sEmail.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(sEmail).matches()) {
            email.setError("ป้อนที่อยู่อีเมลที่ถูกต้อง");
            valid = false;
        } else {
            email.setError(null);
        }

        if (sPassword.isEmpty() || password.length() < 4 || password.length() > 8) {
            password.setError("ระหว่าง 4 ถึง 8 ตัวอักษรและตัวเลข");
            valid = false;
        } else {
            password.setError(null);
        }

        return valid;
    }

}