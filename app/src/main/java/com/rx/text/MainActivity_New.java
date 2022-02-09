package com.rx.text;

import android.Manifest;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.github.zagum.speechrecognitionview.RecognitionProgressView;
import com.github.zagum.speechrecognitionview.adapters.RecognitionListenerAdapter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainActivity_New extends AppCompatActivity {

    public static final Integer RecordAudioRequestCode = 1;
    private SpeechRecognizer speechRecognizer;
    private EditText voiceInput;
    private ImageView micButton;
    GPSTracker gps;
    double latitude, longitude;
    EditText patientName1, Edate, medicine;
    int pageHeight = 1120;
    int pagewidth = 792;
    RecognitionProgressView recognitionProgressView;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        patientName1 = (EditText) findViewById(R.id.patientName);
        Edate = (EditText) findViewById(R.id.date);
        medicine = (EditText) findViewById(R.id.medicine);


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            checkPermission();
        }
        // Create class object
        gps = new GPSTracker(MainActivity_New.this);

        // Check if GPS enabled
        if (gps.canGetLocation()) {

            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();
            Log.e("latitude", "latitude-->" + latitude + "---" + longitude);
            // \n is for new line
            Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
        } else {
            // Can't get location.
            // GPS or network is not enabled.
            // Ask user to enable GPS/network in settings.
            gps.showSettingsAlert();
        }


        voiceInput = findViewById(R.id.voiceInput);
        micButton = findViewById(R.id.button);
        Edate = findViewById(R.id.date);
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);

        final Intent speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        //  speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-IN");


        int[] colors = {
                ContextCompat.getColor(this, R.color.color1),
                ContextCompat.getColor(this, R.color.color2),
                ContextCompat.getColor(this, R.color.color3),
                ContextCompat.getColor(this, R.color.color4),
                ContextCompat.getColor(this, R.color.color5)
        };
        int[] heights = {20, 24, 18, 23, 16};

        recognitionProgressView = (RecognitionProgressView) findViewById(R.id.recognition_view);
        recognitionProgressView.setSpeechRecognizer(speechRecognizer);
        recognitionProgressView.setRecognitionListener(new RecognitionListenerAdapter() {
            @Override
            public void onResults(Bundle results) {

            }
        });
        recognitionProgressView.setColors(colors);
        recognitionProgressView.setBarMaxHeightsInDp(heights);
        recognitionProgressView.setCircleRadiusInDp(2);
        recognitionProgressView.setSpacingInDp(2);
        recognitionProgressView.setIdleStateAmplitudeInDp(2);
        recognitionProgressView.setRotationRadiusInDp(10);
        recognitionProgressView.play();
        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {
                Log.e("onReadyForSpeech", "onReadyForSpeech-->");

            }

            @Override
            public void onBeginningOfSpeech() {
                Log.e("Listening", "Listening-->");
                voiceInput.setHint("Listening...");

                recognitionProgressView.stop();
                recognitionProgressView.play();
                micButton.setVisibility(View.GONE);

            }

            @Override
            public void onRmsChanged(float v) {
                //  Log.e("onRmsChanged","onRmsChanged-->");
            }

            @Override
            public void onBufferReceived(byte[] bytes) {
                Log.e("onBufferReceived", "onBufferReceived-->");

            }

            @Override
            public void onEndOfSpeech() {
                recognitionProgressView.stop();
                recognitionProgressView.setVisibility(View.GONE);
                micButton.setVisibility(View.VISIBLE);
                Log.e("onEndOfSpeech", "onEndOfSpeech-->");
            }

            @Override
            public void onError(int i) {

                recognitionProgressView.stop();
                recognitionProgressView.setVisibility(View.GONE);
                micButton.setVisibility(View.VISIBLE);
                Log.e("onError", "onError-->");
            }

            @Override
            public void onResults(Bundle bundle) {
                Log.e("stop", "stop-->");
                recognitionProgressView.stop();
                micButton.setVisibility(View.VISIBLE);
                micButton.setImageResource(R.drawable.ic_baseline_mic_24);

                ArrayList<String> data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                voiceInput.setText(voiceInput.getText().toString() + " " + data.get(0));
                voiceInput.setSelection(voiceInput.getText().length());
                voiceInput.setFocusable(true);
                voiceInput.requestFocus();
            }

            @Override
            public void onPartialResults(Bundle bundle) {
                Log.e("onPartialResults", "onPartialResults-->");
            }

            @Override
            public void onEvent(int i, Bundle bundle) {
                Log.e("onEvent", "onEvent-->");

            }
        });

        micButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                Log.e("motionEvent", "motionEvent-->"+motionEvent);
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    speechRecognizer.stopListening();
                    recognitionProgressView.stop();
                    micButton.setVisibility(View.VISIBLE);
                }
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    recognitionProgressView.play();
                    // micButton.setImageResource(R.drawable.ic_baseline_mic_24);
                    recognitionProgressView.setVisibility(View.VISIBLE);
                    micButton.setVisibility(View.GONE);
                    speechRecognizer.startListening(speechRecognizerIntent);
                }
                return false;
            }
        });


        findViewById(R.id.date).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
                        Date date1 = calSet.getTime();
                        DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy hh:mm a");
                        String strDate = dateFormat.format(date1);
                        Log.e("date", "date-->" + strDate);
                        Log.e("date", "date-->" + DateFormat.getTimeInstance().format(date1));

                        Edate.setText(strDate);
                    }
                };
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        MainActivity_New.this,
                        onTimeSetListener,
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE),
                        true);
                timePickerDialog.setTitle("Select Time and Date");

                timePickerDialog.show();

            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        speechRecognizer.destroy();
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, RecordAudioRequestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RecordAudioRequestCode && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_add:

                createDirIfNotExists(getApplicationContext());
                if (!voiceInput.getText().toString().isEmpty()) {

                    String details = patientName1.getText().toString() + " \n" + Edate.getText().toString() + "\n" + medicine.getText().toString();

                    // createPdf1(voiceInput.getText().toString(),details);

                    if (patientName1.getText() != null && patientName1.getText().toString().length() != 0) {

                        CreatePdf(voiceInput.getText().toString());

                    } else if (patientName1.getText().toString().length() == 0) {
                        Toast.makeText(this, "please enter the patient name", Toast.LENGTH_LONG).show();
                    }


                } else {
                    Toast.makeText(this, "Please Speak Something", Toast.LENGTH_LONG).show();
                }
                voiceInput.setText(" ");
                return true;
            case R.id.list_pdf:
                Intent intent = new Intent(MainActivity_New.this, ListOfDocActivity.class);
                startActivity(intent);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public static boolean createDirIfNotExists(Context mContext) {
        try {

            if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {


                Boolean isSDPresent = true;

                if (isSDPresent) {

                    File file = new File("SpeechtoText" + "/PDF");
                    if (!file.exists()) {
                        if (!file.mkdirs()) {
                            //LOG.info("Problem creating a folder");
                            return false;
                        }
                    }
                    return true;

                } else {

                    //LOG.info("NO SD-card is present\n");
                    return false;
                }
            } else {
                return false;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;


    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void CreatePdf(String s) {
        // creating an object variable
        // for our PDF document.
        PdfDocument pdfDocument = new PdfDocument();

        // two variables for paint "paint" is used
        // for drawing shapes and we will use "title"
        // for adding text in our PDF file.
        Paint paint = new Paint();
        Paint title = new Paint();

        // we are adding page info to our PDF file
        // in which we will be passing our pageWidth,
        // pageHeight and number of pages and after that
        // we are calling it to create our PDF.
        PdfDocument.PageInfo mypageInfo = new PdfDocument.PageInfo.Builder(pagewidth, pageHeight, 1).create();

        // below line is used for setting
        // start page for our PDF file.
        PdfDocument.Page myPage = pdfDocument.startPage(mypageInfo);

        // creating a variable for canvas
        // from our page of PDF.
        Canvas canvas = myPage.getCanvas();

        String doctorNameHeader = "Asian Institute Of Gastroenterology, Hyderabad";
        //String doctorNameHeader = "MANA PHARMACY";
        title.setTypeface(Typeface.create(Typeface.DEFAULT_BOLD, Typeface.ITALIC));
        title.setTextSize(25);
        //title.setTextSize(35);
        title.setColor(ContextCompat.getColor(this, R.color.black));
        title.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(doctorNameHeader, 396, 80, title);
        //canvas.drawText("A portal for IT professionals.", 209, 100, title);
        String doctorNameHeaderBelow = "MBBS, MD (General Medicine)";
        //String doctorNameHeaderBelow = "#-4-8-447,NAIDUPET 5th LINE KORITEPADU CENTER,, GUNTUR";
        title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        title.setTextSize(18);
        //title.setTextSize(24);
        title.setColor(ContextCompat.getColor(this, R.color.Dimgrey));
        title.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(doctorNameHeaderBelow, 396, 105, title);

        String doctorNameHeaderBelowLast = "6-3-661, Red Rose Cafe Ln, Sangeet Nagar, Somajiguda, Hyderabad, Telangana 500082";
        //String doctorNameHeaderBelowLast = "Tel -9440885470";
        title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        title.setTextSize(18);
        //title.setTextSize(24);
        title.setColor(ContextCompat.getColor(this, R.color.Dimgrey));
        title.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(doctorNameHeaderBelowLast, 396, 130, title);

        String GeneralPatientInfo = "GENERAL PATIENT INFORMATION";
        title.setTypeface(Typeface.create(Typeface.DEFAULT_BOLD, Typeface.ITALIC));
        title.setTextSize(22);
        title.setUnderlineText(true);
        title.setColor(ContextCompat.getColor(this, R.color.black));
        title.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(GeneralPatientInfo, 396, 175, title);
        title.setUnderlineText(false);

        String doctorName1 = "Dr. Chavan";
        String doctorDesi = "MBBS, MD (General Medicine)";
        String doctorClinicName = "Asian Institute Of Gastroenterology, Hyderabad";
        String doctorAddress = "6-3-661, Red Rose Cafe Ln, Sangeet Nagar, Somajiguda, Hyderabad, Telangana 500082";
        String patientName = patientName1.getText().toString();
        String patientNumber = "9502393045";
        String patientAddress = "vijayawada";

        String Name = "Name   : ";
        title.setTypeface(Typeface.create(Typeface.DEFAULT_BOLD, Typeface.NORMAL));
        title.setTextSize(18);
        title.setColor(ContextCompat.getColor(this, R.color.black));
        title.setTextAlign(Paint.Align.LEFT);
        canvas.drawText(Name, 80, 225, title);
        title.setTypeface(Typeface.create(Typeface.DEFAULT_BOLD, Typeface.NORMAL));
        title.setTextSize(16);
        title.setColor(ContextCompat.getColor(this, R.color.Dimgrey));
        title.setTextAlign(Paint.Align.LEFT);
        canvas.drawText(patientName, 160, 225, title);

        String Number = "Number : ";
        title.setTypeface(Typeface.create(Typeface.DEFAULT_BOLD, Typeface.NORMAL));
        title.setTextSize(18);
        title.setColor(ContextCompat.getColor(this, R.color.black));
        title.setTextAlign(Paint.Align.LEFT);
        canvas.drawText(Number, 80, 255, title);
        title.setTypeface(Typeface.create(Typeface.DEFAULT_BOLD, Typeface.NORMAL));
        title.setTextSize(16);
        title.setColor(ContextCompat.getColor(this, R.color.Dimgrey));
        title.setTextAlign(Paint.Align.LEFT);
        canvas.drawText(patientNumber, 160, 255, title);

        String Address = "Address: ";
        title.setTypeface(Typeface.create(Typeface.DEFAULT_BOLD, Typeface.NORMAL));
        title.setTextSize(18);
        title.setColor(ContextCompat.getColor(this, R.color.black));
        title.setTextAlign(Paint.Align.LEFT);
        canvas.drawText(Address, 80, 285, title);
        title.setTypeface(Typeface.create(Typeface.DEFAULT_BOLD, Typeface.NORMAL));
        title.setTextSize(16);
        title.setColor(ContextCompat.getColor(this, R.color.Dimgrey));
        title.setTextAlign(Paint.Align.LEFT);
        canvas.drawText(patientAddress, 160, 285, title);

        /*String Doctor = "Doctor     : ";
        title.setTypeface(Typeface.create(Typeface.DEFAULT_BOLD, Typeface.NORMAL));
        title.setTextSize(18);
        title.setColor(ContextCompat.getColor(this, R.color.black));
        title.setTextAlign(Paint.Align.LEFT);
        canvas.drawText(Doctor, 300, 225, title);
        title.setTypeface(Typeface.create(Typeface.DEFAULT_BOLD, Typeface.NORMAL));
        title.setTextSize(16);
        title.setColor(ContextCompat.getColor(this, R.color.Dimgrey));
        title.setTextAlign(Paint.Align.LEFT);
        canvas.drawText(doctorName1, 400, 225, title);

        String designation = "Designation: ";
        title.setTypeface(Typeface.create(Typeface.DEFAULT_BOLD, Typeface.NORMAL));
        title.setTextSize(18);
        title.setColor(ContextCompat.getColor(this, R.color.black));
        title.setTextAlign(Paint.Align.LEFT);
        canvas.drawText(designation, 300, 255, title);
        title.setTypeface(Typeface.create(Typeface.DEFAULT_BOLD, Typeface.NORMAL));
        title.setTextSize(16);
        title.setColor(ContextCompat.getColor(this, R.color.Dimgrey));
        title.setTextAlign(Paint.Align.LEFT);
        canvas.drawText(doctorDesi, 400, 255, title);


        String dAddress = "Address    : ";
        title.setTypeface(Typeface.create(Typeface.DEFAULT_BOLD, Typeface.NORMAL));
        title.setTextSize(18);
        title.setColor(ContextCompat.getColor(this, R.color.black));
        title.setTextAlign(Paint.Align.LEFT);
        canvas.drawText(dAddress, 300, 285, title);
        title.setTypeface(Typeface.create(Typeface.DEFAULT_BOLD, Typeface.NORMAL));
        title.setTextSize(16);
        title.setColor(ContextCompat.getColor(this, R.color.Dimgrey));
        title.setTextAlign(Paint.Align.LEFT);
        canvas.drawText(doctorAddress, 400, 285, title);*/


        String Clinic = "Doctor Name: " + doctorName1;
        title.setTypeface(Typeface.create(Typeface.DEFAULT_BOLD, Typeface.NORMAL));
        title.setTextSize(18);
        title.setColor(ContextCompat.getColor(this, R.color.black));
        title.setTextAlign(Paint.Align.LEFT);
        canvas.drawText(Clinic, 400, 225, title);
        title.setTypeface(Typeface.create(Typeface.DEFAULT_BOLD, Typeface.NORMAL));
        title.setTextSize(16);
        title.setColor(ContextCompat.getColor(this, R.color.Dimgrey));
        title.setTextAlign(Paint.Align.LEFT);
        canvas.drawText(Clinic, 400, 285, title);



       /* mytime = java.text.DateFormat.getTimeInstance().format(Calendar.getInstance().getTime());
        Log.i("TimeCurrent","timr===="+mytime);*/
        String ArrivalTime = "Appointment Date : " + Edate.getText().toString();
        title.setTypeface(Typeface.create(Typeface.DEFAULT_BOLD, Typeface.NORMAL));
        title.setTextSize(18);
        title.setColor(ContextCompat.getColor(this, R.color.black));
        title.setTextAlign(Paint.Align.LEFT);
        canvas.drawText(ArrivalTime, 400, 255, title);

      /*  String mydate =java.text.DateFormat.getDateInstance().format(Calendar.getInstance().getTime());
        String date = "Appointment Date   : "+mydate;
        title.setTypeface(Typeface.create(Typeface.DEFAULT_BOLD, Typeface.NORMAL));
        title.setTextSize(18);
        title.setColor(ContextCompat.getColor(this, R.color.black));
        title.setTextAlign(Paint.Align.LEFT);
        canvas.drawText(date, 400, 285, title);*/

        title.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        title.setColor(ContextCompat.getColor(this, R.color.black));
        title.setTextSize(18);
        title.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("Doctor Suggestion :", 80, 335, title);
        int x = 100;
        TextPaint mTextPaint = new TextPaint();
        StaticLayout mTextLayout = new StaticLayout(s, mTextPaint, canvas.getWidth() - 100,
                Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        canvas.save();
        canvas.translate(x, 355);
        mTextLayout.draw(canvas);
        canvas.restore();


        pdfDocument.finishPage(myPage);

        // below line is used to set the name of
        // our PDF file and its path.


        try {


            String directory_path = getExternalFilesDir(null).getPath() + "/mypdf/";
            Log.e("directory_path", "directory_path-->" + directory_path);
            File file = new File(directory_path);
            if (!file.exists()) {
                file.mkdirs();
            }
            String date1 = (String) android.text.format.DateFormat.format("yyyy-MM-dd kk:mm:ss", new Date());

            String targetPdf = directory_path + patientName1.getText().toString() + "_" + date1 + ".pdf";
            File filePath = new File(targetPdf);

            pdfDocument.writeTo(new FileOutputStream(filePath));
            Edate.setText("");
            patientName1.setText("");
            Toast.makeText(this, "Done", Toast.LENGTH_LONG).show();


           /* File directoryToStore;
            directoryToStore = getBaseContext().getExternalFilesDir("patientFolder");
            if (!directoryToStore.exists()) {
                if (directoryToStore.mkdir()) ; //directory is created;
            }
            Log.i("path","filepath"+getExternalFilesDir(null).getAbsolutePath());
            //File file = new File(Environment.getExternalStorageDirectory().getPath(), "patient "+patientName+".pdf");
            File file = new File(getExternalFilesDir("i").getAbsolutePath(), "patient"+patientName+".pdf");
            Log.i("path","filepath"+file);
            pdfDocument.writeTo(new FileOutputStream(file));
            Toast.makeText(MainActivity.this, "PDF file generated successfully.", Toast.LENGTH_SHORT).show();*/


        } catch (IOException e) {
            // below line is used
            // to handle error
            Log.i("path", "Path Error");
            e.printStackTrace();
        }
        // after storing our pdf to that
        // location we are closing our PDF file.
        pdfDocument.close();

    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void createPdf1(String sometext, String details) {
        createDirIfNotExists(getApplicationContext());
        String directory_path = getExternalFilesDir(null).getPath() + "/mypdf/";
        Log.e("directory_path", "directory_path-->" + directory_path);
        File file = new File(directory_path);
        if (!file.exists()) {
            file.mkdirs();
        }
        String date = (String) android.text.format.DateFormat.format("yyyy-MM-dd kk:mm:ss", new Date());

        String targetPdf = directory_path + "Doc_" + date + ".pdf";
        File filePath = new File(targetPdf);
        try {
            PdfDocument document = new PdfDocument();
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
            PdfDocument.Page page = document.startPage(pageInfo);
            Canvas canvas = page.getCanvas();
            //*this part i took from the post for multiline breaks*
            TextPaint mTextPaint = new TextPaint();
            Paint title = new Paint();
            Paint patient_information = new Paint();
            title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
            title.setTextSize(25);
            title.setColor(ContextCompat.getColor(this, R.color.purple_200));

            patient_information.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
            patient_information.setTextSize(18);

            canvas.drawText("PATIENT INFORMATION FORM", 150, 50, title);

            canvas.drawText(details, 50, 100, patient_information);


            StaticLayout mTextLayout = new StaticLayout(sometext, mTextPaint, canvas.getWidth() - 100, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            canvas.save();
            int textX = 50;
            int textY = 150;


       /*     float x = 100 +100 + 100 +1 + danMuView.levelBitmapWidth + danMuView.textMarginLeft;
            float top = (int) (danMuView.getY()) + danMuChannel.height / 2 - mTextLayout.getHeight() / 2;
            canvas.save();
            canvas.translate((int) x, top);*/


            // below line is used for setting
            // our text to center of PDF.
            /*title.setTextAlign(Paint.Align.CENTER);
            canvas.drawText("This is sample document which we have created.", 396, 560, title);*/

            canvas.translate(textX, textY);
//**

            mTextLayout.draw(canvas);
            canvas.restore();
            document.finishPage(page);
            // document.writeTo(fOut);
            //  document.close();

            document.writeTo(new FileOutputStream(filePath));
            Toast.makeText(this, "Done", Toast.LENGTH_LONG).show();

            // close the document
            document.close();
        } catch (IOException e) {
            Log.e("main", "error " + e.toString());
            Toast.makeText(this, "Something wrong: " + e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void createPdf(String sometext, String details) {
        createDirIfNotExists(getApplicationContext());
        String directory_path = getExternalFilesDir(null).getPath() + "/mypdf/";
        Log.e("directory_path", "directory_path-->" + directory_path);
        File file = new File(directory_path);
        if (!file.exists()) {
            file.mkdirs();
        }
        String date = (String) android.text.format.DateFormat.format("yyyy-MM-dd kk:mm:ss", new Date());

        String targetPdf = directory_path + "Doc_" + date + ".pdf";
        File filePath = new File(targetPdf);
        try {
            PdfDocument document = new PdfDocument();
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
            PdfDocument.Page page = document.startPage(pageInfo);
            Canvas canvas = page.getCanvas();


            //*this part i took from the post for multiline breaks*
            TextPaint mTextPaint = new TextPaint();
            Paint title = new Paint();
            Paint patient_information = new Paint();
            title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
            title.setTextSize(25);
            title.setColor(ContextCompat.getColor(this, R.color.purple_200));

            patient_information.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
            patient_information.setTextSize(18);

            canvas.drawText("PATIENT INFORMATION FORM", 150, 50, title);

            canvas.drawText(details, 50, 100, patient_information);


            StaticLayout mTextLayout = new StaticLayout(sometext, mTextPaint, canvas.getWidth() - 100, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            canvas.save();
            int textX = 50;
            int textY = 150;


            mTextLayout.draw(canvas);
            canvas.restore();
            document.finishPage(page);

            document.writeTo(new FileOutputStream(filePath));
            Toast.makeText(this, "Done", Toast.LENGTH_LONG).show();

            // close the document
            document.close();
        } catch (IOException e) {
            Log.e("main", "error " + e.toString());
            Toast.makeText(this, "Something wrong: " + e.toString(), Toast.LENGTH_LONG).show();
        }
    }


}
