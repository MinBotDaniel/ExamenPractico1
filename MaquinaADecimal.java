import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigInteger;

public class MaquinaADecimal extends JFrame {

    private JTextField cajaTexto;
    private JButton botonLittleEndian;
    private JButton botonBigEndian;
    private JButton botonValidar;
    private boolean littleEndian;

    public MaquinaADecimal() {
        setTitle("Endian a decimal");
        setSize(520, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        cajaTexto = new JTextField(20);
        botonLittleEndian = new JButton("Little Endian");
        botonBigEndian = new JButton("Big Endian");
        botonValidar = new JButton("Encontrar");

        JLabel label = new JLabel("Ingrese el dato:");
        label.setBounds(20, 20, 200, 25);
        add(label);

        cajaTexto.setBounds(220, 20, 150, 25);
        add(cajaTexto);

        botonLittleEndian.setBounds(20, 60, 150, 25);
        add(botonLittleEndian);

        botonBigEndian.setBounds(180, 60, 150, 25);
        add(botonBigEndian);

        botonValidar.setBounds(340, 60, 150, 25);
        add(botonValidar);

        botonLittleEndian.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                littleEndian = true;
            }
        });

        botonBigEndian.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                littleEndian = false;
            }
        });

        botonValidar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                convertirYMostrar();
            }
        });
    }

    private void convertirYMostrar() {
        String input = cajaTexto.getText().replaceAll("\\s+", "").toUpperCase();
        if (!esHexadecimalValido(input)) {
            JOptionPane.showMessageDialog(null, "Por favor, ingrese un número hexadecimal válido.");
            return;
        }

        if (littleEndian) {
            input = convertirABigEndian(input);
        }
        String binario = calculaBinario(input);
        double decimal = convertirABinarioADecimal(binario);
        JOptionPane.showMessageDialog(null, "HEX: " + input + "\nBIN: " + binario + "\nDecimal: " + decimal);
    }

    private boolean esHexadecimalValido(String cadena) {
        return cadena.matches("^[0-9A-Fa-f]+$");
    }

    private String convertirABigEndian(String le) {
        StringBuilder bigEndian = new StringBuilder();
        for (int i = le.length() - 2; i >= 0; i -= 2) {
            bigEndian.append(le, i, i + 2);
        }
        return bigEndian.toString();
    }

    private String calculaBinario(String numHexa) {
        BigInteger decimal = new BigInteger(numHexa, 16);
        String binario = decimal.toString(2);
        int numBits = numHexa.length() * 4;
        return String.format("%" + numBits + "s", binario).replace(' ', '0');
    }

    private double convertirABinarioADecimal(String binario) {
        int bits = binario.length();
        if (bits == 16) {
            return convertirIEEE754(binario, 5, 10);
        } else if (bits == 32) {
            return convertirIEEE754(binario, 8, 23);
        } else if (bits == 64) {
            return convertirIEEE754(binario, 11, 52);
        } else if (bits == 80) {
            return convertirIEEE754(binario, 15, 63);
        } else {
            throw new IllegalArgumentException("Número de bits no soportado.");
        }
    }

    private double convertirIEEE754(String binario, int bitsExp, int mantisaBits) {
        int bitSigno = binario.charAt(0) - '0';
        String exponenteBin = binario.substring(1, 1 + bitsExp);
        String mantisaBin = binario.substring(1 + bitsExp);
        if (bitsExp == 15) {
            mantisaBin = binario.substring(1 + bitsExp + 1);
        }

        int exponente = Integer.parseInt(exponenteBin, 2);
        int valorConstante = 0;
        if (bitsExp == 5) {
            valorConstante = 15;
        }
        else if (bitsExp == 8) {
            valorConstante = 127;
        }
        else if(bitsExp == 11) {
            valorConstante = 1023;
        }
        else if (bitsExp == 15) {
            valorConstante = 16383;
        }
        int exponenteReal = exponente - valorConstante;

        double mantisaDecimal = 1.0;
        for (int i = 0; i < mantisaBin.length(); i++) {
            if (i < mantisaBin.length()) {
                mantisaDecimal += (mantisaBin.charAt(i) - '0') * Math.pow(2, -(i + 1));
            }
        }

        double valorDecimal = mantisaDecimal * Math.pow(2, exponenteReal);
        if (bitSigno == 1) {
            valorDecimal = -valorDecimal;
        }
        return valorDecimal;
    }
}