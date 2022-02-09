package com.rx.text;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.PrintManager;
import android.print.pdf.PrintedPdfDocument;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class PrintPDFActivity extends AppCompatActivity {
    private static final String LOG_TAG = "GeneratePDF";

    private EditText preparedBy;
    private File pdfFile;
    private String filename = "Sample.pdf";
    private String filepath = "MyInvoices";

    //  private BaseFont bfBold;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_p_d_f);


    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void printPDF(View view) {
        PrintManager printManager = (PrintManager) getSystemService(PRINT_SERVICE);
        printManager.print("print_any_view_job_name", new ViewPrintAdapter(this,
                findViewById(R.id.relativeLayout)), null);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public class ViewPrintAdapter extends PrintDocumentAdapter {

        private PrintedPdfDocument mDocument;
        private Context mContext;
        private View mView;
        String path;

        public ViewPrintAdapter(Context context, View view) {
            mContext = context;
            mView = view;
        }

        public ViewPrintAdapter(Context context, String path) {
            mContext = context;
            path = path;
        }

        @Override
        public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes,
                             CancellationSignal cancellationSignal,
                             LayoutResultCallback callback, Bundle extras) {


            mDocument = new PrintedPdfDocument(mContext, newAttributes);

            if (cancellationSignal.isCanceled()) {
                callback.onLayoutCancelled();
                return;
            }

            PrintDocumentInfo.Builder builder = new PrintDocumentInfo
                    .Builder("print_output.pdf")
                    .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                    .setPageCount(1);

            PrintDocumentInfo info = builder.build();
            callback.onLayoutFinished(info, true);
        }

        @Override
        public void onWrite(PageRange[] pages, ParcelFileDescriptor destination,
                            CancellationSignal cancellationSignal,
                            WriteResultCallback callback) {

            InputStream in = null;
            OutputStream out = null;
            try {
                File file = new File(path);
                in = new FileInputStream(file);
                out = new FileOutputStream(destination.getFileDescriptor());
                byte[] buff = new byte[1024];
                int size;
                while ((size = in.read(buff)) >= 0 && cancellationSignal.isCanceled()) {
                    out.write(buff, 0, size);
                }

                if (cancellationSignal.isCanceled())
                    callback.onWriteCancelled();
                else {
                    callback.onWriteFinished(new PageRange[]{new PageRange(0, 0)});
                }

            } catch (Exception e) {
                callback.onWriteFailed(e.getMessage());
                e.printStackTrace();
            } finally {
                try {


                    in.close();
                    out.close();
                } catch (IOException ex) {

                    Log.e("Error ", "error--> " + ex);
                }
              /*  // Start the page
                PdfDocument.Page page = mDocument.startPage(0);
                // Create a bitmap and put it a canvas for the view to draw to. Make it the size of the view
                Bitmap bitmap = Bitmap.createBitmap(mView.getWidth(), mView.getHeight(),
                        Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                mView.draw(canvas);
                // create a Rect with the view's dimensions.
                Rect src = new Rect(0, 0, mView.getWidth(), mView.getHeight());
                // get the page canvas and measure it.
                Canvas pageCanvas = page.getCanvas();
                float pageWidth = pageCanvas.getWidth();
                float pageHeight = pageCanvas.getHeight();
                // how can we fit the Rect src onto this page while maintaining aspect ratio?
                float scale = Math.min(pageWidth/src.width(), pageHeight/src.height());
                float left = pageWidth / 2 - src.width() * scale / 2;
                float top = pageHeight / 2 - src.height() * scale / 2;
                float right = pageWidth / 2 + src.width() * scale / 2;
                float bottom = pageHeight / 2 + src.height() * scale / 2;
                RectF dst = new RectF(left, top, right, bottom);

                pageCanvas.drawBitmap(bitmap, src, dst, null);
                mDocument.finishPage(page);

                try {
                    mDocument.writeTo(new FileOutputStream(
                            destination.getFileDescriptor()));
                } catch (IOException e) {
                    callback.onWriteFailed(e.toString());
                    return;
                } finally {
                    mDocument.close();
                    mDocument = null;
                }*/
                callback.onWriteFinished(new PageRange[]{new PageRange(0, 0)});
            }
        }
    }
}
            //get reference to the edittext so pull data out
      /*  preparedBy = (EditText) findViewById(R.id.preparedBy);
        //need to load license from the raw resources for iText
        //skip this if you are going to use droidText
        InputStream license = this.getResources().openRawResource(R.raw.itextkey);
        LicenseKey.loadLicenseFile(license);

        //check if external storage is available so that we can dump our PDF file there
        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
            Log.v(LOG_TAG, "External Storage not available or you don't have permission to write");
        }
        else {
            //path for the PDF file in the external storage
            pdfFile = new File(getExternalFilesDir(filepath), filename);
        }

    }

    public void printPDF(View v) {

        switch (v.getId()) {

            //start the process of creating the PDF and then print it
            case R.id.printPDF:
                String personName = preparedBy.getText().toString();
                generatePDF(personName);
                break;

        }

    }

    private void generatePDF(String personName){

        //create a new document
        Document document = new Document();

        try {

            PdfWriter docWriter = PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
            document.open();


            PdfContentByte cb = docWriter.getDirectContent();
            //initialize fonts for text printing
            initializeFonts();

            //the company logo is stored in the assets which is read only
            //get the logo and print on the document
            InputStream inputStream = getAssets().open("olympic_logo.png");
            Bitmap bmp = BitmapFactory.decodeStream(inputStream);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
            Image companyLogo = Image.getInstance(stream.toByteArray());
            companyLogo.setAbsolutePosition(25,700);
            companyLogo.scalePercent(25);
            document.add(companyLogo);

            //creating a sample invoice with some customer data
            createHeadings(cb,400,780,"Company Name");
            createHeadings(cb,400,765,"Address Line 1");
            createHeadings(cb,400,750,"Address Line 2");
            createHeadings(cb,400,735,"City, State - ZipCode");
            createHeadings(cb,400,720,"Country");

            //list all the products sold to the customer
            float[] columnWidths = {1.5f, 2f, 5f, 2f,2f};
            //create PDF table with the given widths
            PdfPTable table = new PdfPTable(columnWidths);
            // set table width a percentage of the page width
            table.setTotalWidth(500f);

            PdfPCell cell = new PdfPCell(new Phrase("Qty"));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Item Number"));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Item Description"));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Price"));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Ext Price"));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(cell);
            table.setHeaderRows(1);

            DecimalFormat df = new DecimalFormat("0.00");
            for(int i=0; i < 15; i++ ){
                double price = Double.valueOf(df.format(Math.random() * 10));
                double extPrice = price * (i+1) ;
                table.addCell(String.valueOf(i+1));
                table.addCell("ITEM" + String.valueOf(i+1));
                table.addCell("Product Description - SIZE " + String.valueOf(i+1));
                table.addCell(df.format(price));
                table.addCell(df.format(extPrice));
            }

            //absolute location to print the PDF table from
            table.writeSelectedRows(0, -1, document.leftMargin(), 650, docWriter.getDirectContent());

            //print the signature image along with the persons name
            inputStream = getAssets().open("signature.png");
            bmp = BitmapFactory.decodeStream(inputStream);
            stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
            Image signature = Image.getInstance(stream.toByteArray());
            signature.setAbsolutePosition(400f, 150f);
            signature.scalePercent(25f);
            document.add(signature);

            createHeadings(cb,450,135,personName);

            document.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }

        //PDF file is now ready to be sent to the bluetooth printer using PrintShare
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setPackage("com.dynamixsoftware.printershare");
        i.setDataAndType(Uri.fromFile(pdfFile),"application/pdf");
        startActivity(i);

    }

    private void createHeadings(PdfContentByte cb, float x, float y, String text){

        cb.beginText();
        cb.setFontAndSize(bfBold, 8);
        cb.setTextMatrix(x,y);
        cb.showText(text.trim());
        cb.endText();

    }


    private static boolean isExternalStorageReadOnly() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    private static boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    private void initializeFonts(){


        try {
            bfBold = BaseFont.createFont(BaseFont.HELVETICA_BOLD, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);

        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }*/


