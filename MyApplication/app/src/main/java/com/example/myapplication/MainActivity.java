package com.example.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.SocketFactory;

public class MainActivity extends AppCompatActivity {

    // Setup Server information
    protected static String server = "172.20.10.12";
    protected static int port = 7070;
    Spinner combo_empleados;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Capturamos el boton de Enviar
        View button = findViewById(R.id.button_send);

        // Llama al listener del boton Enviar
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });

        // Añadimos spinner:
        combo_empleados = findViewById(R.id.idSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.combo_empleados, android.R.layout.simple_spinner_item);
        combo_empleados.setAdapter(adapter);
    }


    // Creación de un cuadro de dialogo para confirmar pedido
    private void showDialog() throws Resources.NotFoundException {
        CheckBox camas = (CheckBox) findViewById(R.id.checkBox_camas);
        CheckBox mesas = (CheckBox) findViewById(R.id.checkBox_mesas);
        CheckBox sillas = (CheckBox) findViewById(R.id.checkBox_sillas);
        CheckBox sillones = (CheckBox) findViewById(R.id.checkBox_sillones);
        Boolean centinela = Boolean.TRUE;

        if (!camas.isChecked() && !mesas.isChecked() && !sillas.isChecked() && !sillones.isChecked() ) {
            // Mostramos un mensaje emergente;
            Toast.makeText(getApplicationContext(), "Selecciona al menos un elemento", Toast.LENGTH_SHORT).show();
        } else {
            // Restricción numérica cantidad:
            if(camas.isChecked()){
                EditText cantidadCamas = findViewById(R.id.editTextNumberCamas);
                String s = cantidadCamas.getText().toString();
                Integer cCamas = Integer.valueOf(s);
                if(cCamas < 0 || cCamas > 300){
                    Toast.makeText(getApplicationContext(), "Cantidad Incorrecta de camas", Toast.LENGTH_SHORT).show();
                    centinela = Boolean.FALSE;
                }
            }
            if(mesas.isChecked()){
                EditText cantidadMesas = findViewById(R.id.editTextNumberMesas);
                String s = cantidadMesas.getText().toString();
                Integer cMesas = Integer.valueOf(s);
                if(cMesas < 0 || cMesas > 300){
                    Toast.makeText(getApplicationContext(), "Cantidad Incorrecta de mesas", Toast.LENGTH_SHORT).show();
                    centinela = Boolean.FALSE;
                }
            }
            if(sillas.isChecked()){
                EditText cantidadCamas = findViewById(R.id.editTextNumberSillas);
                String s = cantidadCamas.getText().toString();
                Integer cSillas = Integer.valueOf(s);
                if(cSillas < 0 || cSillas > 300){
                    Toast.makeText(getApplicationContext(), "Cantidad Incorrecta de sillas", Toast.LENGTH_SHORT).show();
                    centinela = Boolean.FALSE;
                }
            }
            if(sillones.isChecked()){
                EditText cantidadSillones = findViewById(R.id.editTextNumberSillas);
                String s = cantidadSillones.getText().toString();
                Integer cSillones = Integer.valueOf(s);
                if(cSillones < 0 || cSillones > 300){
                    Toast.makeText(getApplicationContext(), "Cantidad Incorrecta de sillones", Toast.LENGTH_SHORT).show();
                    centinela = Boolean.FALSE;
                }
            }
            // Si centinela es TRUE, la selección ha sido correcta:
            if(centinela == Boolean.TRUE){
                new AlertDialog.Builder(this)
                        .setTitle("Enviar")
                        .setMessage("Se va a proceder al envio")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                    // Catch ok button and send information
                                    public void onClick(DialogInterface dialog, int whichButton) {

                                        // 1. Extraer los datos de la vista

                                        // 2. Firmar los datos

                                        // 3. Enviar los datos

                                        Toast.makeText(MainActivity.this, "Petición enviada correctamente", Toast.LENGTH_SHORT).show();
                                    }
                                }

                        )
                                .

                        setNegativeButton(android.R.string.no, null)

                                .

                        show();
            }
        }
    }



}
