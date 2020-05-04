package com.mys3soft.mys3chat;

import android.content.Context;

public class MyImageView extends android.support.v7.widget.AppCompatImageView {
    String clave="";
    String claveImagen="";
    public MyImageView(Context context) {
        super(context);
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getClaveImagen() {
        return claveImagen;
    }

    public void setClaveImagen(String claveImagen) {
        this.claveImagen = claveImagen;
    }
}
