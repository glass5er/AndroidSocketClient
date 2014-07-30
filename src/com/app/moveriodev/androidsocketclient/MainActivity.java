package com.app.moveriodev.androidsocketclient;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity {
    private final static int DEVICES_DIALOG = 1;
    private final static int ERROR_DIALOG = 2;

    private ClientTask clientTask = new ClientTask(this);

    private ProgressDialog waitDialog;
    private EditText editTextInput;
    private EditText editTextOutput;
    private EditText editTextIP;
    private EditText editTextPort;
    private String errorMessage = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextInput  = (EditText) findViewById(R.id.editText1);
        editTextOutput = (EditText) findViewById(R.id.editText2);
        editTextIP     = (EditText) findViewById(R.id.editTextIP);
        editTextPort   = (EditText) findViewById(R.id.editTextPort);

        Button sendBtn = (Button) findViewById(R.id.sendBtn);
        sendBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = editTextInput.getText().toString();
                Log.d("SOCKET_TEST", "click : " + msg);
                clientTask.doSend(msg);
            }
        });
        Button resetBtn = (Button) findViewById(R.id.resetBtn);
        resetBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                restart();
            }
        });
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onResume() { super.onResume(); }

    @Override
    protected void onDestroy() {
        clientTask.doClose();
        super.onDestroy();
    }

    public void doSetResultText(String text) {
        editTextOutput.setText(text);
    }

    protected void restart() {
        //Intent intent = this.getIntent();
        //this.finish();
        clientTask.doClose();
        String strIP   = editTextIP  .getText().toString();
        String strPort = editTextPort.getText().toString();
        clientTask.init(strIP, strPort);
        clientTask.doConnect();
    }

    @SuppressWarnings("deprecation")
    public void errorDialog(String msg) {
        if (this.isFinishing()) { return; }
        this.errorMessage = msg;
        this.showDialog(ERROR_DIALOG);
    }

    public Dialog createErrorDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Error");
        alertDialogBuilder.setMessage("");
        alertDialogBuilder.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                });
        return alertDialogBuilder.create();
    }

    public void showWaitDialog(String msg) {
        if (waitDialog == null) {
            waitDialog = new ProgressDialog(this);
        }
        waitDialog.setMessage(msg);
        waitDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        waitDialog.show();
    }

    public void hideWaitDialog() {
        waitDialog.dismiss();
    }
}
