package com.rx.text;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

public class Proc
{
    public String[] out = new String[0];
    public int status = 0;

    public Proc(String cmd, File dir)
    {
        Log.d(TAG, "Exec: '" + cmd + "' inside " + dir);
        status = -1;
        try
        {
            Process p = Runtime.getRuntime().exec(cmd, null, dir);
            waitFor(p);
        }
        catch(IOException e)
        {
        }
    }

    public Proc(String[] cmd, File dir)
    {
        Log.d(TAG, "Exec: '" + Arrays.toString(cmd) + "' inside " + dir);
        status = -1;
        try
        {
            Process p = Runtime.getRuntime().exec(cmd, null, dir);
            waitFor(p);
        }
        catch(IOException e)
        {
        }
    }

    void waitFor(final Process p)
    {
        final ArrayList<String> outArr = new ArrayList<String>();

        Thread tout = new Thread(new Runnable()
        {
            public void run()
            {
                BufferedReader bout = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String line;
                try
                {
                    while ((line = bout.readLine()) != null)
                    {
                        synchronized(outArr)
                        {
                            outArr.add(line);
                        }
                        //Log.d(TAG, "Exec: out: " + line);
                    }
                    bout.close();
                }
                catch(IOException e)
                {
                }
            }
        });
        tout.start();
        Thread terr = new Thread(new Runnable()
        {
            public void run()
            {
                BufferedReader berr = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String line;
                try
                {
                    while((line = berr.readLine()) != null)
                    {
                        synchronized(outArr)
                        {
                            outArr.add(line);
                        }
                        //Log.d(TAG, "Exec: err: " + line);
                    }
                    berr.close();
                }
                catch(IOException e)
                {
                }
            }
        });
        terr.start();
        try
        {
            status = p.waitFor();
            tout.join();
            terr.join();
        }
        catch(InterruptedException e)
        {
        }
        out = outArr.toArray(out);
        //Log.d(TAG, "Exec: exit status: " + status);
        p.destroy();
    }

    static public final String TAG = "CupsExec";
}