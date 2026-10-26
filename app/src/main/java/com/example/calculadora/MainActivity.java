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

import java.math.BigDecimal;
import java.math.MathContext;

public class MainActivity extends AppCompatActivity {

    // límite de dígitos que se pueden escribir y cifras significativas de los resultados
    private static final int MAX_DIGITOS = 12;
    private static final MathContext PRECISION = new MathContext(MAX_DIGITOS);
    private static final String ERROR = "Error";

    private TextView tvPantalla;
    // BigDecimal en vez de double para evitar errores de redondeo (ej: 0.1 + 0.2 = 0.30000000000000004)
    private BigDecimal acumulado = BigDecimal.ZERO; // primer operando
    private String operacion = null;    // "+", "-", "×", "÷" o null si no hay ninguna pendiente
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
            case "×":
            case "÷":
                if (pantalla.equals(ERROR)) {
                    break;
                }
                // si ya había una operación pendiente, resolverla primero (ej: 5 + 3 × ...)
                if (operacion != null && !nuevoNumero) {
                    BigDecimal resultado = calcular(acumulado, new BigDecimal(pantalla), operacion);
                    if (resultado == null) {
                        mostrarError();
                        break;
                    }
                    acumulado = resultado;
                    tvPantalla.setText(formatear(acumulado));
                } else {
                    acumulado = new BigDecimal(pantalla);
                }
                operacion = texto;
                nuevoNumero = true;
                break;
            case "=":
                if (operacion != null) {
                    BigDecimal resultado = calcular(acumulado, new BigDecimal(pantalla), operacion);
                    if (resultado == null) {
                        mostrarError();
                        break;
                    }
                    tvPantalla.setText(formatear(resultado));
                    operacion = null;
                    nuevoNumero = true;
                }
                break;
            case "C":
                // borrar último carácter
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
                acumulado = BigDecimal.ZERO;
                operacion = null;
                nuevoNumero = true;
                break;
            case ".":
                // empezar con "0." si es un número nuevo; ignorar un segundo punto
                if (nuevoNumero) {
                    tvPantalla.setText("0.");
                } else if (!pantalla.contains(".")) {
                    tvPantalla.setText(pantalla + ".");
                }
                nuevoNumero = false;
                break;
            default:
                // es un número: añadirlo a la pantalla
                if (nuevoNumero || pantalla.equals("0")) {
                    tvPantalla.setText(texto);
                } else if (pantalla.replaceAll("[^0-9]", "").length() < MAX_DIGITOS) {
                    tvPantalla.setText(pantalla + texto);
                }
                nuevoNumero = false;
                break;
        }
    }

    // devuelve null si la operación no es válida (división entre cero)
    private BigDecimal calcular(BigDecimal a, BigDecimal b, String op) {
        switch (op) {
            case "+":
                return a.add(b);
            case "-":
                return a.subtract(b);
            case "×":
                return a.multiply(b);
            default:
                if (b.signum() == 0) {
                    return null;
                }
                return a.divide(b, PRECISION);
        }
    }

    // redondear y quitar ceros sobrantes; notación científica si el número es demasiado largo
    private String formatear(BigDecimal valor) {
        BigDecimal redondeado = valor.round(PRECISION).stripTrailingZeros();
        String texto = redondeado.toPlainString();
        return texto.length() > MAX_DIGITOS + 2 ? redondeado.toString() : texto;
    }

    private void mostrarError() {
        tvPantalla.setText(ERROR);
        acumulado = BigDecimal.ZERO;
        operacion = null;
        nuevoNumero = true;
    }
}
