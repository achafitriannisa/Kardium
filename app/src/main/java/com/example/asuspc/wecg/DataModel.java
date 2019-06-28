package com.example.asuspc.wecg;

public class DataModel {

    private String fileName;
    private String lastModified;

    public DataModel(){

    }

    public DataModel(String fileName, String lastModified){
        this.fileName=fileName;
        this.lastModified=lastModified;
    }

    public String getFileName() {
        return fileName;
    }

    public String getLastModified() {
        return lastModified;
    }

}