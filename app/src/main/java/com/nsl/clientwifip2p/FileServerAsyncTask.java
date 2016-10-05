package com.nsl.clientwifip2p;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pDevice;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;


/**
 * Created by fawadahmad on 9/28/16.
 */

public class FileServerAsyncTask extends Service {

    final String className = "FileServerAsyncTask";
    public static WifiP2pDevice deviceToConnectTo;

    public static InetAddress groupOwnerAddress;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d (className, "onStartCommand");

        new Thread (new ClientThread()).start();
        return super.onStartCommand(intent, flags, startId);




    }






    /**
     * Start activity that can handle the JPEG image
     */

   /*
    @Override
    protected void onPostExecute(String result) {
        if (result != null) {
            statusText.setText("File copied - " + result);
            Intent intent = new Intent();
            intent.setAction(android.content.Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.parse("file://" + result), "image/*");
            context.startActivity(intent);
        }
    }
    */








    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    class ClientThread implements Runnable
    {
        @Override
        public void run () {

            /**
             Create a client socket and look for the server
             */

            Log.d(className, "Creating Client Socket " + groupOwnerAddress);

            int portToConnectTo = 12125;




            /*

            InetAddress serverAddr = groupOwnerAddress;
            try {
                serverAddr = InetAddress.getByName(deviceToConnectTo.deviceName);
            }
            catch (IOException e)
            {
                Log.d (className, e.getMessage());
            }
            */


            Log.d (className, "here");


            Socket clientSocket = new Socket();



            try {
                clientSocket.connect(new InetSocketAddress( groupOwnerAddress, portToConnectTo ),1000);
                Log.d(className, "Connected");
            } catch (IOException e) {
                Log.d (className, e.getMessage());
            }
            //InetSocketAddress serverAddress = serverAddr;



            /**   try {
             Socket clientSocket = new Socket(serverAddr, portToConnectTo);
             } catch (IOException e) {
             Log.d(className, e.getMessage());
             }
             */

            //try {
            // clientSocket.bind(null);
            // }


            // try {
            // clientSocket.connect();
            //  }
            //  catch (IOException e)
            // {
            //  Log.d (className, e.getMessage());
            // }
            //  Log.d(className, "Connected");

            /**

             final ServerSocket serverSocket = new ServerSocket();
             serverSocket.setReuseAddress(true);
             serverSocket.bind(new InetSocketAddress(0));
             Log.d(className, "Advertising Port Address : " + serverSocket.getLocalPort());

             Log.d (className, "Waiting for connections");

             new Thread(new Runnable() {
            @Override
            public void run() {
            try {
            Socket client = serverSocket.accept();
            Log.d(className, "Accepted :D :D :D :D ");
            }
            catch (IOException e)
            {
            Log.d (className, e.getMessage());
            }
            }
            });


             */

            /**
             * If this code is reached, a client has connected and transferred data
             * Save the input stream from the client as a JPEG file
             */

            InputStream stream = new ByteArrayInputStream("Project Completed\n".getBytes(StandardCharsets.UTF_8));

            byte [] byteArray = {1,2,3,4,5,6,7,8,9,10};
            try {
                OutputStreamWriter outputStream = new OutputStreamWriter(clientSocket.getOutputStream(), "UTF-8");
                outputStream.write ("Project Complete", 0, "Project Complete".length());
                Log.d (className, "Message sent");
            }
            catch (IOException e)
            {
                Log.d (className, e.getMessage());
            }









        }

    }


}



