package com.rx.text;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.PrintManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;

public class ListOfDocActivity extends AppCompatActivity {
    ListView lv;
    RecyclerView recyclerview;
    MyRecyclerViewAdapter adapter;
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    private Context primaryBaseActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_doc);
        //   lv= (ListView)findViewById(R.id.list_view);
        recyclerview = (RecyclerView) findViewById(R.id.recyclerview);

        if (ContextCompat.checkSelfPermission(ListOfDocActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(ListOfDocActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {


            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(ListOfDocActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.


            }
        } else {

            ArrayList<String> filesinfolder = GetFiles(getExternalFilesDir(null).getPath() + "/mypdf/");

            if (filesinfolder != null && filesinfolder.size() > 0) {

                recyclerview.setLayoutManager(new LinearLayoutManager(this));
                adapter = new MyRecyclerViewAdapter(ListOfDocActivity.this, filesinfolder);
//            adapter.setClickListener((MyRecyclerViewAdapter.ItemClickListener) this);
                recyclerview.setAdapter(adapter);

            }
           /* ArrayAdapter<String> adapter
                    = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1,
                    filesinfolder);

            lv.setAdapter(adapter);*/


        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    ArrayList<String> filesinfolder = GetFiles(getExternalFilesDir(null).getPath() + "/mypdf/");
                  /*  ArrayAdapter<String> adapter
                            = new ArrayAdapter<String>(this,
                            android.R.layout.simple_list_item_1,
                            filesinfolder);*/
                    // set up the RecyclerView
                    if (filesinfolder != null && filesinfolder.size() > 0) {
                        recyclerview.setLayoutManager(new LinearLayoutManager(this));
                        adapter = new MyRecyclerViewAdapter(ListOfDocActivity.this, filesinfolder);
                        // adapter.setClickListener((MyRecyclerViewAdapter.ItemClickListener) this);
                        recyclerview.setAdapter(adapter);
                    }
                    /*  lv.setAdapter(adapter);*/

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
    @Override
    protected void attachBaseContext(Context newBase) {
        primaryBaseActivity=newBase;//SAVE ORIGINAL INSTANCE

        /*Some locale handling stuff right here*/
        /*LocaleHelper's onAttach is returning a *new* context in Android N which will void PrintManager's context*/
        super.attachBaseContext(newBase);

    }
    public ArrayList<String> GetFiles(String directorypath) {
        ArrayList<String> Myfiles = new ArrayList<String>();
        File f = new File(directorypath);
        f.mkdirs();
        File[] files = f.listFiles();
        if (files.length == 0) {
            return null;
        } else {
            for (int i = 0; i < files.length; i++)
                Myfiles.add(files[i].getName());
        }
        return Myfiles;
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void printPDF(String path ) {
        //PrintManager printManager = (PrintManager) getSystemService(PRINT_SERVICE);
        PrintManager printManager=(PrintManager)  getSystemService(Context.PRINT_SERVICE);
        try
        {
            PrintDocumentAdapter printAdapter = new PdfDocumentAdapter(ListOfDocActivity.this,path );
            printManager.print("Document", printAdapter,new PrintAttributes.Builder().build());


        }
       catch (Exception e)
       {
        //Logger.logError(e);
       }

    }

    public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {

        private ArrayList<String> mData;
        private LayoutInflater mInflater;
       // private ItemClickListener mClickListener;
        Context context;

        // data is passed into the constructor
        MyRecyclerViewAdapter(Context context, ArrayList<String> data) {
            this.mInflater = LayoutInflater.from(context);
            this.mData = data;
            this.context = context;
        }

        // inflates the row layout from xml when needed
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = mInflater.inflate(R.layout.recyclerview_row, parent, false);
            return new ViewHolder(view);
        }

        // binds the data to the TextView in each row
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            String animal = mData.get(position);

            holder.myTextView.setText(animal);

            holder.myTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                 /*   MediaPlayer mediaPlayer = MediaPlayer.create(v.getContext(), R.raw.select_click);
                    mediaPlayer.start();*/

                    final Dialog dialog = new Dialog(context);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.profile_view_popup);
                    dialog.setCancelable(true);
                    dialog.getWindow().setBackgroundDrawable(
                            new ColorDrawable(android.graphics.Color.TRANSPARENT));
                    dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    if (dialog != null) {
                        dialog.show();

                    }
                    TextView alrem = dialog.findViewById(R.id.alrem);
                    TextView view_doc = dialog.findViewById(R.id.view_doc);
                    TextView print_pdf = dialog.findViewById(R.id.print_pdf);


                    alrem.setOnClickListener(view -> {


                        openTimePickerDialog(true, context, position);
                        dialog.dismiss();
                    });
                    view_doc.setOnClickListener(view -> {

                        dialog.dismiss();
                        File f = new File(context.getExternalFilesDir(null).getPath() + "/mypdf/");
                        f.mkdirs();
                        File[] files = f.listFiles();
                        if (files.length != 0) {

                            Uri path = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", new File(files[position].getAbsolutePath()));

                            // Uri path = Uri.fromFile(new File(files[position].getAbsolutePath()));
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_VIEW);
                            intent.setDataAndType(path, "application/pdf");
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                            try {

                            } catch (ActivityNotFoundException e) {

                            }
                        }
                    });

                    print_pdf.setOnClickListener(new View.OnClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                        @Override
                        public void onClick(View v) {
                            //PDF file is now ready to be sent to the bluetooth printer using PrintShare
                            File f = new File(context.getExternalFilesDir(null).getPath() + "/mypdf/");
                            f.mkdirs();
                            File[] files = f.listFiles();
                            if (files.length != 0) {

                                printPDF(files[position].getAbsolutePath());


                            }
                        }
                    });

                }
            });

        }
        @Override
        public int getItemCount() {
            return mData.size();
        }


        // stores and recycles views as they are scrolled off screen
        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView myTextView;

            ViewHolder(View itemView) {
                super(itemView);
                myTextView = itemView.findViewById(R.id.tvAnimalName);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
             //   if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
            }
        }

        // convenience method for getting data at click position
        String getItem(int id) {
            return mData.get(id);
        }

        /*// allows clicks events to be caught
        void setClickListener(ItemClickListener itemClickListener) {
            this.mClickListener = itemClickListener;
        }*/

        // parent activity will implement this method to respond to click events
        /*public interface ItemClickListener {
            void onItemClick(View view, int position);
        }*/
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void printPDF1(String path) {



    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public class PdfDocumentAdapter extends PrintDocumentAdapter {

        Context context = null;
        String pathName = "";
        public PdfDocumentAdapter(Context ctxt, String pathName) {
            context = ctxt;
            this.pathName = pathName;
        }
        @Override
        public void onLayout(PrintAttributes printAttributes, PrintAttributes printAttributes1, CancellationSignal cancellationSignal, LayoutResultCallback layoutResultCallback, Bundle bundle) {
            if (cancellationSignal.isCanceled()) {
                layoutResultCallback.onLayoutCancelled();
            }
            else {
                PrintDocumentInfo.Builder builder=
                        new PrintDocumentInfo.Builder(" file name");
                builder.setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                        .setPageCount(PrintDocumentInfo.PAGE_COUNT_UNKNOWN)
                        .build();
                layoutResultCallback.onLayoutFinished(builder.build(),
                        !printAttributes1.equals(printAttributes));
            }
        }

        @Override
        public void onWrite(PageRange[] pageRanges, ParcelFileDescriptor parcelFileDescriptor, CancellationSignal cancellationSignal, WriteResultCallback writeResultCallback) {
            InputStream in=null;
            OutputStream out=null;
            try {
                File file = new File(pathName);
                in = new FileInputStream(file);
                out=new FileOutputStream(parcelFileDescriptor.getFileDescriptor());

                byte[] buf=new byte[16384];
                int size;

                while ((size=in.read(buf)) >= 0
                        && !cancellationSignal.isCanceled()) {
                    out.write(buf, 0, size);
                }

                if (cancellationSignal.isCanceled()) {
                    writeResultCallback.onWriteCancelled();
                }
                else {
                    writeResultCallback.onWriteFinished(new PageRange[] { PageRange.ALL_PAGES });
                }
            }
            catch (Exception e) {
                writeResultCallback.onWriteFailed(e.getMessage());
               // Logger.logError( e);
            }
            finally {
                try {
                    in.close();
                    out.close();
                }
                catch (IOException e) {
                    //Logger.logError( e);
                }
            }
        }}



    public static void showProfileViewAlert(Context context) {
        boolean appContact = false;
        final Dialog dialog = new Dialog(context);
        ;
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.profile_view_popup);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        if (dialog != null) {
            dialog.show();

        }
        TextView alrem = dialog.findViewById(R.id.alrem);
        TextView view_doc = dialog.findViewById(R.id.view_doc);


        alrem.setOnClickListener(view -> {

            //CallMethodHelper.processAudioCall(context, numberToDial, "AUDIO");
            dialog.dismiss();
        });
        view_doc.setOnClickListener(view -> {

            dialog.dismiss();
        });


    }


    private static void openTimePickerDialog(boolean is24r, Context context, int position) {
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog.OnTimeSetListener onTimeSetListener
                = new TimePickerDialog.OnTimeSetListener() {

            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                Calendar calNow = Calendar.getInstance();
                Calendar calSet = (Calendar) calNow.clone();

                calSet.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calSet.set(Calendar.MINUTE, minute);
                calSet.set(Calendar.SECOND, 0);
                calSet.set(Calendar.MILLISECOND, 0);

                if (calSet.compareTo(calNow) <= 0) {
                    //Today Set time passed, count to tomorrow
                    calSet.add(Calendar.DATE, 1);
                }

                setAlarm(context, calSet, position);
            }
        };
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                context,
                onTimeSetListener,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                is24r);
        timePickerDialog.setTitle("Set Alarm Time");

        timePickerDialog.show();

    }


    private static void setAlarm(Context context, Calendar targetCal, int postion) {

        Log.e("alarm", "alarm-->" + "Alarm is set@ " + targetCal.getTime());

      /*  textAlarmPrompt.setText(
                "\n\n***\n"
                        + "Alarm is set@ " + targetCal.getTime() + "\n"
                        + "***\n");*/
     /*  Intent intent = new Intent(context, MyAlarmReciever.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, 280192, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 14);
        calendar.set(Calendar.MINUTE, 40);
        AlarmManager   alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);

        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_HOUR, pendingIntent);*/

        Intent intent = new Intent(context, MyAlarmReciever.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, postion, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        // alarmManager.set(AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis(), pendingIntent);
        // alarmManager.set(AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis(), pendingIntent);


        if (Build.VERSION.SDK_INT >= 23) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
                    targetCal.getTimeInMillis(), pendingIntent);
        } else if (Build.VERSION.SDK_INT >= 19) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis(), pendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis(), pendingIntent);
        }


    }

    private static void setAlarm1(Context context, Calendar targetCal) {

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmBroadCastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        if (Build.VERSION.SDK_INT >= 23) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis(), pendingIntent);
        } else if (Build.VERSION.SDK_INT >= 19) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis(), pendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis(), pendingIntent);
        }
    }
}