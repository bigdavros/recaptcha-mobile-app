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
import java.io.BufferedReader;

import org.json.JSONObject;

/*import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;*/

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.recaptchaandroiddemo.databinding.FragmentFirstBinding;

class Reply{
    private String data;
    private String result;

    public void setData(String data) {
        this.data = data;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getData() {
        return data;
    }

    public String getResult() {
        return result;
    }
}

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;
    private TextView tx1;
    private String ipAddr = "192.168.240.59";
    private String port = "8080";
    private String endpoint = "api";
    private String proto = "http"; // or https

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentFirstBinding.inflate(inflater, container, false);
        tx1 = binding.textviewFirst;
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
        super.onViewCreated(view, savedInstanceState);
        binding.buttonFirst.setOnClickListener(new View.OnClickListener() {

            private Reply sendRequest() {
                Reply reply = new Reply();
                OutputStream out = null;
                try {
                    URL url = new URL(proto+"://"+ipAddr+":"+port+"/"+endpoint);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(10000);
                    conn.setConnectTimeout(15000);
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.setRequestProperty("X-API-Key","TEST");
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
                            reply.setData(jsonObject.getString("data"));
                            reply.setResult(jsonObject.getString("result"));
                        }
                        catch(Exception e){
                            reply.setData("error");
                            reply.setResult("App Error: Couldn't create JSON object");
                        }
                    }
                    catch(Exception e){
                        reply.setData("error");
                        reply.setResult("App Error: Couldn't read buffer");
                    }
                }
                catch(Exception e){
                    reply.setData("error");
                    reply.setResult("App Error: Couldn't connect to URL");
                }
                return reply;
            }
            @Override
            public void onClick(View view) {
                Reply reply;
                try{
                    reply = (Reply) sendRequest();
                }
                catch(Exception e){
                    reply = new Reply();
                    reply.setData("error");
                    reply.setResult("App Error: Can't send request to server");
                }

                tx1.setText(reply.getData()+"\n"+reply.getResult());
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}