package com.app.moveriodev.androidsocketclient;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.UUID;

/**
 * Created by doba on 2014/07/16.
 */
public class ClientTask {
    private static final String TAG = "SocketTask";

    private static final UUID APP_UUID = UUID.fromString("e509e07c-1b92-4b45-92bd-e1deb8731442");

    private MainActivity activity;
    private BufferedReader bufIn;
    private PrintWriter bufOut;
    private Socket mSocket = null;
    private String mAddress = "";
    private String mStrPort = "";

    // Constructor
    public ClientTask(MainActivity activity) {
        this.activity = activity;
    }

    public void init(String ip, String port) {
        mAddress = ip;
        mStrPort = port;
    }

    public void doConnect()        { new ConnectTask().execute(); }
    public void doClose()          { new CloseTask()  .execute(); }
    public void doSend(String msg) { new SendTask()   .execute(msg); }

    // Asynchronous tasks
    private class ConnectTask extends AsyncTask<Void, Void, Object> {
        @Override
        protected void onPreExecute() {
            activity.showWaitDialog("Connect Socket Device.");
        }

        @Override
        protected Object doInBackground(Void... params) {
            try {
                Log.d(TAG, "address = " + mAddress + ", port = " + mStrPort);
                mSocket = new Socket(mAddress, Integer.parseInt(mStrPort));
                // set socket IO
                bufIn  = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
                bufOut = new PrintWriter(mSocket.getOutputStream(), true);
            } catch (Throwable t) {
                doClose();
                t.printStackTrace();
                return t;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object result) {
            if (result instanceof Throwable) {
                Log.e(TAG, result.toString(), (Throwable) result);
                activity.errorDialog(result.toString());
            } else {
                activity.hideWaitDialog();
            }
        }
    }

    private class CloseTask extends AsyncTask<Void, Void, Object> {
        @Override
        protected Object doInBackground(Void... params) {
            try {
                try { bufOut .close(); } catch (Throwable t) {/* ignore */}
                try { bufIn  .close(); } catch (Throwable t) {/* ignore */}
                try { mSocket.close(); } catch (Throwable t) {/* ignore */}
                // socket = null;
            } catch (Throwable t) {
                return t;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object result) {
            if (result instanceof Throwable) {
                Log.e(TAG, result.toString(), (Throwable) result);
                activity.errorDialog(result.toString());
            }
        }
    }

    private class SendTask extends AsyncTask<String, Void, Object> {
        @Override
        protected Object doInBackground(String... params) {
            try {
                // @DEBUG
                Log.d(TAG, new String(params[0].getBytes()));
                // send
                bufOut.println(params[0]);
                // receive
                String ret = bufIn.readLine();

                return ret;
            } catch (Throwable t) {
                doClose();
                return t;
            }
        }

        @Override
        protected void onPostExecute(Object result) {
            if (result instanceof Exception) {
                Log.e(TAG, result.toString(), (Throwable) result);
                activity.errorDialog(result.toString());
            } else {
                activity.doSetResultText(result.toString());
            }
        }
    }
}
