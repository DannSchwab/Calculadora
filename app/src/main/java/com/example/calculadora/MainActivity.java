package com.example.calculadora;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    // límite de dígitos para que el número quepa en un long
    private static final int MAX_DIGITOS = 15;

    private TextView tvPantalla;
    private long acumulado = 0;         // primer operando
    private String operacion = null;    // "+", "-" o null si no hay ninguna pendiente
    private boolean nuevoNumero = true; // el próximo dígito empieza un número nuevo

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvPantalla = findViewById(R.id.tvPantalla);
        GridLayout grid = findViewById(R.id.gridBotones);

        View.OnClickListener listener = v -> {
            String texto = ((Button) v).getText().toString();
            pulsar(texto);
        };

        for (int i = 0; i < grid.getChildCount(); i++) {
            grid.getChildAt(i).setOnClickListener(listener);
        }
    }

    private void pulsar(String texto) {
        String pantalla = tvPantalla.getText().toString();

        switch (texto) {
            case "+":
            case "-":
                // si ya había una operación pendiente, resolverla primero (ej: 5 + 3 - ...)
                if (operacion != null && !nuevoNumero) {
                    acumulado = calcular(acumulado, Long.parseLong(pantalla), operacion);
                    tvPantalla.setText(String.valueOf(acumulado));
                } else {
                    acumulado = Long.parseLong(pantalla);
                }
                operacion = texto;
                nuevoNumero = true;
                break;
            case "=":
                if (operacion != null) {
                    long resultado = calcular(acumulado, Long.parseLong(pantalla), operacion);
                    tvPantalla.setText(String.valueOf(resultado));
                    operacion = null;
                    nuevoNumero = true;
                }
                break;
            case "C":
                // borrar último dígito
                if (nuevoNumero) {
                    break;
                }
                pantalla = pantalla.substring(0, pantalla.length() - 1);
                if (pantalla.isEmpty() || pantalla.equals("-")) {
                    pantalla = "0";
                }
                tvPantalla.setText(pantalla);
                break;
            case "CA":
                // borrarlo todo
                tvPantalla.setText("0");
                acumulado = 0;
                operacion = null;
                nuevoNumero = true;
                break;
            default:
                // es un número: añadirlo a la pantalla
                if (nuevoNumero || pantalla.equals("0")) {
                    tvPantalla.setText(texto);
                } else if (pantalla.replace("-", "").length() < MAX_DIGITOS) {
                    tvPantalla.setText(pantalla + texto);
                }
                nuevoNumero = false;
                break;
        }
    }

    private long calcular(long a, long b, String op) {
        return op.equals("+") ? a + b : a - b;
    }
}