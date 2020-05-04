package com.mys3soft.mys3chat;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;

public class MyOnSuccessListener implements OnSuccessListener {
    String value;
    Context contexto;
    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setContexto(Context contexto) {
        this.contexto = contexto;
    }

    public Context getContexto() {
        return contexto;
    }

    @Override
    public void onSuccess(Object o) {
        Intent i = new Intent(getContexto(), VisorImagenes.class);
        i.putExtra("key", getValue());
        getContexto().startActivity(i);
    }
}
