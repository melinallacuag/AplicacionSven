package com.anggastudio.sample;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.anggastudio.printama.Printama;
import com.anggastudio.sample.WebApiSVEN.Parameters.GlobalInfo;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_BLUETOOTH_PERMISSION = 1;
    private static final String PREF_TIPO_PAPEL = "pref_tipo_papel";
    RadioGroup radioGroup;
    TextView textotipopapel;
    NFCUtil nfcUtil;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        nfcUtil = new NFCUtil(this);
        findViewById(R.id.btn_printer_settings).setOnClickListener(v -> showPrinterList());

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_BLUETOOTH_PERMISSION);
        } else {
            getSavedPrinter();
        }


        radioGroup     = findViewById(R.id.radioFormaPago);
        textotipopapel = findViewById(R.id.textotipopapel);

        radioGroup.setVisibility(View.GONE);
        textotipopapel.setVisibility(View.GONE);

       /* String tipoPapelPredeterminado = GlobalInfo.getTipoPapel10;
        int radioButtonId = -1;

        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            RadioButton radioButton = (RadioButton) radioGroup.getChildAt(i);
            if (radioButton.getText().toString().equals(tipoPapelPredeterminado)) {
                radioButtonId = radioButton.getId();
                break;
            }
        }

        if (radioButtonId != -1) {
            radioGroup.check(radioButtonId);
        }

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton radioButton = findViewById(checkedId);
            GlobalInfo.getTipoPapel10 = radioButton.getText().toString();
            saveTipoPapel(GlobalInfo.getTipoPapel10);
        });*/

    }

    private void saveTipoPapel(String tipoPapel) {
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PREF_TIPO_PAPEL, tipoPapel);
        editor.apply();
    }

  private void showPrinterList() {
        Printama.showPrinterList(this, R.color.colorBlue, printerName -> {
            Toast.makeText(this, printerName, Toast.LENGTH_SHORT).show();
            TextView connectedTo = findViewById(R.id.tv_printer_info);
            String text = "Connected to : " + printerName;
            connectedTo.setText(text);
            if (!printerName.contains("failed")) {
                findViewById(R.id.btn_printer_test).setVisibility(View.VISIBLE);

                setupButtonClickListener();
            }
        });
    }

    private void setupButtonClickListener() {
        findViewById(R.id.btn_printer_test).setOnClickListener(v -> {
            printQrReceipt2();
        });
    }

    private void printQrReceipt2() {
        Bitmap logo = Printama.getBitmapFromVector(this, R.drawable.logo_app);
        String nota = "Some Text";
        Printama.with(this).connect(printama -> {
            printama.printImage(logo, 200);
            printama.addNewLine();
            printama.printTextln("Title Text", Printama.CENTER);
            printama.printTextln("Title Text", Printama.CENTER);
            printama.printTextln("Title Text", Printama.CENTER);
            printama.printTextln("Title Text", Printama.CENTER);
            printama.printTextln("Title Text", Printama.CENTER);
            printama.printDashedLine();
            printama.addNewLine();
            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix bitMatrix;
            try {
                bitMatrix = writer.encode(nota, BarcodeFormat.QR_CODE, 200, 200);
                int width = bitMatrix.getWidth();
                int height = bitMatrix.getHeight();
                Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
                for (int x = 0; x < width; x++) {
                    for (int y = 0; y < height; y++) {
                        int color = Color.WHITE;
                        if (bitMatrix.get(x, y)) color = Color.BLACK;
                        bitmap.setPixel(x, y, color);
                    }
                }
                if (bitmap != null) {
                    printama.printImage(bitmap);
                }
            } catch (WriterException e) {
                e.printStackTrace();
            }

            printama.addNewLine();
            printama.printTextln("Autorizado mediante resolucion de Superintendencia Nro. 203-2015 SUNAT. Representacion impresa de la boleta de venta electronica. Consulte desde\n"+ "http://4-fact.com/sven/auth/consulta");
            printama.feedPaper();
            printama.cutPaper();
            printama.close();
        }, this::showToast);
    }


    private void testPrinter() {
        Printama.with(this).printTest();
    }

    private void getSavedPrinter() {
        BluetoothDevice connectedPrinter = Printama.with(this).getConnectedPrinter();
        if (connectedPrinter != null) {
            TextView connectedTo = findViewById(R.id.tv_printer_info);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            String text = "Connected to : " + connectedPrinter.getName();
            connectedTo.setText(text);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String printerName = Printama.getPrinterResult(resultCode, requestCode, data);
        showResult(printerName);
    }

    private void showResult(String printerName) {
        showToast(printerName);
        TextView connectedTo = findViewById(R.id.tv_printer_info);
        String text = "Connected to : " + printerName;
        connectedTo.setText(text);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        nfcUtil.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        nfcUtil.onPause();
    }


}
