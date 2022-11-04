package com.recaptchaandroiddemo;

import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.InputStream;
import org.json.JSONObject;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.recaptchaandroiddemo.databinding.FragmentFirstBinding;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;
    private TextView tx1;
    private Button bt1;
    private String ipAddr = "";
    private String port = "";
    private String endpoint = "api";
    private String proto = "http"; // or https
    private String xapikey = "";
    
    private void sendRequest() {
        OutputStream out = null;
        try {
            URL url = new URL(proto+"://"+ipAddr+":"+port+"/"+endpoint);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestProperty("X-API-Key",xapikey);
            out = new BufferedOutputStream(conn.getOutputStream());

            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
            writer.write("{\"type\":\"android\"}");
            writer.flush();
            writer.close();
            out.close();
            conn.connect();
            try {
                InputStream is = conn.getInputStream();
                byte[] b1 = new byte[1024];
                StringBuffer buffer = new StringBuffer();

                while ( is.read(b1) != -1)
                    buffer.append(new String(b1));

                conn.disconnect();
                try {
                    JSONObject jsonObject = new JSONObject(buffer.toString());
                    tx1.setText("Data: " + jsonObject.getString("data")+"\n"+"Result: " + jsonObject.getString("result"));
                    bt1.setClickable(false);
                    bt1.setVisibility(View.INVISIBLE);
                }
                catch (Exception e) {
                    tx1.setText("error\nApp Error: Couldn't create JSON object");
                }
            } catch (Exception e) {
                tx1.setText("error\nApp Error: Couldn't read buffer");
                conn.disconnect();
            }
        } catch (Exception e) {
            tx1.setText("error\nApp Error: Couldn't connect to URL");
        }
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentFirstBinding.inflate(inflater, container, false);
        tx1 = binding.textviewFirst;
        bt1 = binding.buttonFirst;
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
        super.onViewCreated(view, savedInstanceState);
        binding.buttonFirst.setOnClickListener(new View.OnClickListener() {

            

            @Override
            public void onClick(View view) {
                try{
                    sendRequest();
                }
                catch(Exception e){
                    tx1.setText("error\nApp Error: Problem with reCAPTCHA execute()");
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
