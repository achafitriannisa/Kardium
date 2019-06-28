package com.example.asuspc.wecg;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileOperations {
    public FileOperations() {

    }


    public Boolean write(String fname, String fcontent){
        try {

            String fpath = Environment.getExternalStorageDirectory().getPath()+File.separator+"Kardium";
            File storageDir = new File(fpath);
            if(!storageDir.exists()){
                storageDir.mkdirs();
            }
            File file = new File(storageDir,fname);

            FileWriter fw = new FileWriter(file, true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(fcontent);
            bw.flush();
            bw.close();

            Log.d("Success","Success");
            return true;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

    }



    public String[] read(String fname){

        BufferedReader br = null;

        try {

            // Get the directory for the user's public pictures directory.
            String fpath = Environment.getExternalStorageDirectory().getPath()+File.separator+"Kardium"+File.separator+fname;

            br = new BufferedReader(new FileReader(fpath));
            List<String> lines = new ArrayList<String>();
            String line = null;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }

            br.close();
            return lines.toArray(new String[0]);

        } catch (IOException e) {
            e.printStackTrace();
            return null;

        }

    }
}