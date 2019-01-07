package com.example.egehaneralp.dovizalsat;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    TextView tlText,dolarText,euroText;
    EditText editText;
    RadioGroup bgrup;
    RadioButton dolarrb,eurorb,RB;
    Button satinAlButton,bozdurButton;

    String edittextS;
    int girilentutar;

    int control;

    static float bakiyeTL,bakiyeDOLAR,bakiyeEURO;

    SharedPreferences sharedPre;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPre=getPreferences(MODE_PRIVATE);
        editor=sharedPre.edit();
        bakiyeTL=sharedPre.getFloat("a",1000);
        bakiyeDOLAR=sharedPre.getFloat("b",50);
        bakiyeEURO=sharedPre.getFloat("c",60);


        tlText=findViewById(R.id.tlText);
        dolarText=findViewById(R.id.dolarText);
        euroText=findViewById(R.id.euroText);

        tlText.setText(" "+bakiyeTL+" TL");
        dolarText.setText(" "+bakiyeDOLAR+ " $");
        euroText.setText(" "+bakiyeEURO+ " €");

        editText=findViewById(R.id.editText);

        bgrup=findViewById(R.id.bgrup);

        dolarrb=findViewById(R.id.dolarrb);
        eurorb=findViewById(R.id.eurorb);

        satinAlButton=findViewById(R.id.satinAlButton);
        bozdurButton=findViewById(R.id.bozdurButton);




        satinAlButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                control=0;
                new ArkaPlan().execute("https://www.doviz.gen.tr/doviz_json.asp?version=1.0.4");
            }
        });
        bozdurButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                control=1;
                new ArkaPlan().execute("https://www.doviz.gen.tr/doviz_json.asp?version=1.0.4");

            }
        });

    }

    class ArkaPlan extends AsyncTask<String,Void,String>{
        @Override
        protected String doInBackground(String... params) {

            HttpURLConnection connection;
            BufferedReader buf;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream is = connection.getInputStream();
                buf = new BufferedReader(new InputStreamReader(is));

                String satir, dosya ="";

                while ((satir = buf.readLine()) != null) {
                    //Log.d("satir", satir);
                    dosya += satir;  // WHİLE BİTTİGİNDE SUNUCUDAKİ TÜM SATİRLARI ELDE ETMİŞ OLUCAĞIM

                }
                return dosya;

            } catch (Exception e) {
                e.printStackTrace();

            }

            return "sorun";


        }

        @Override
        protected void onPostExecute(String s) {

            int selectedRB= bgrup.getCheckedRadioButtonId(); //********************
            RB =findViewById(selectedRB);                   //SEÇİLMİŞ RB Yİ BELİRLEDİK

            edittextS=editText.getText().toString();         //EditText teki sayı ---editInt---
            girilentutar=Integer.parseInt(edittextS);

            try{
                JSONObject json =new JSONObject(s);

                if(control==0){ //SATIN AL BUTONUNA BASILDIYSA

                    if(RB==dolarrb){

                        double kur=json.getDouble("dolar2");
                        double a=kur*girilentutar;
                        bakiyeTL-=a;
                        tlText.setText(" "+ bakiyeTL +" TL");
                        bakiyeDOLAR +=girilentutar;
                        dolarText.setText(" "+ bakiyeDOLAR + " $");

                        editor.putFloat("a",bakiyeTL);
                        editor.putFloat("b",bakiyeDOLAR);
                        editor.commit();

                    }
                    if(RB==eurorb){

                        double kur=json.getDouble("euro2");
                        double a =kur*girilentutar;
                        bakiyeTL -=a;
                        tlText.setText(" "+bakiyeTL+" TL");
                        bakiyeEURO+=girilentutar;
                        euroText.setText(" "+bakiyeEURO + " €");

                        editor.putFloat("a",bakiyeTL);
                        editor.putFloat("c",bakiyeEURO);
                        editor.commit();

                    }

                }
                if(control==1){ //BOZDUR BUTONUNA TIKLANDIYSA
                    if(RB==dolarrb){

                        double kur=json.getDouble("dolar");
                        double a=kur*girilentutar;
                        bakiyeTL+=a;
                        tlText.setText(" "+bakiyeTL+" TL");
                        bakiyeDOLAR-=girilentutar;
                        dolarText.setText(" "+bakiyeDOLAR+" $");

                        editor.putFloat("a",bakiyeTL);
                        editor.putFloat("b",bakiyeDOLAR);
                        editor.commit();

                    }
                    if(RB==eurorb){

                        double kur=json.getDouble("euro");
                        double a=kur*girilentutar;
                        bakiyeTL+=a;
                        tlText.setText(" "+bakiyeTL+" TL");
                        bakiyeEURO-=girilentutar;
                        euroText.setText(" "+bakiyeEURO+" €");

                        editor.putFloat("a",bakiyeTL);
                        editor.putFloat("c",bakiyeEURO);
                        editor.commit();
                    }
                }


                editor.putFloat("a",bakiyeTL);
                editor.putFloat("b",bakiyeDOLAR);
                editor.putFloat("c",bakiyeEURO);
                editor.commit();

            }
            catch(Exception e){
                e.printStackTrace();
            }

        }
    }
}
