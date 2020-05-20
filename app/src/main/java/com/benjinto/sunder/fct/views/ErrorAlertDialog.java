package com.benjinto.sunder.fct.views;

import android.app.AlertDialog;
import android.content.Context;

public class ErrorAlertDialog {
    private AlertDialog alertDialog;

    public ErrorAlertDialog(Context baseContext) {
        AlertDialog.Builder builder = new AlertDialog.Builder(baseContext);
        builder.setCancelable(false).setTitle("Greška").setMessage("Ups, došlo je do pogreške");
        builder.setPositiveButton("U REDU", (dialogInterface, i) -> alertDialog.dismiss());
        alertDialog = builder.create();
    }

    public void show(){
        alertDialog.show();
    }

}
