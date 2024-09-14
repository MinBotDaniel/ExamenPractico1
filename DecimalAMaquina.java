import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DecimalAMaquina extends JFrame {

    private JTextField cajaTexto1;
    private JButton botonIngresar;
    private String[] opciones;
    private JComboBox<String> sistema;

    public DecimalAMaquina() {
        setTitle("Decimal a Little Endian");
        setSize(520, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        cajaTexto1 = new JTextField(20);
        botonIngresar = new JButton("Ingresar");
        opciones = new String[]{"16 bits", "32 bits", "64 bits", "80 bits"};
        sistema = new JComboBox<>(opciones);

        JLabel label = new JLabel("Ingrese el dato:");
        label.setBounds(20, 20, 200, 25);
        add(label);

        cajaTexto1.setBounds(220, 20, 150, 25);
        add(cajaTexto1);

        botonIngresar.setBounds(20, 60, 150, 25);
        add(botonIngresar);

        sistema.setBounds(220, 60, 150, 25);
        add(sistema);

        botonIngresar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resultados();
            }
        });
    }

    private void resultados() {
        String input = cajaTexto1.getText();
        int seleccion = sistema.getSelectedIndex();
        if (seleccion > 1) {
            seleccion += 1; 
        }
        int bits = 16 + (seleccion * 16);

        try {
            double valorDecimal = Double.parseDouble(input);
            String binario = convertirABinario(valorDecimal);
            String binarioIEEE754 = conversion(valorDecimal, bits, binario);
            String hexadecimal = binarioAHexadecimal(binarioIEEE754);
            String littleEndian = convertirALittleEndian(hexadecimal);

            JOptionPane.showMessageDialog(null, "BIN: " + binario + "\n" + "BIN IEEE754: " + binarioIEEE754 + "\n" + "HEX: " + hexadecimal + "\n" + "LITTLE ENDIAN: " + littleEndian + "\n");
        } 
        catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Error en la entrada: " + ex.getMessage());
        }
    }

    private String conversion(double valorDecimal, int bits, String valorBinario) {
        StringBuilder binario = new StringBuilder();

        int signo;
        if (valorDecimal < 0) {
            signo = 1;
            valorDecimal = valorDecimal * (-1);
        }
        else {
            signo = 0;
        }
        binario.append(signo);

        binario.append(exponente(valorBinario, bits));
        binario.append(mantisa(valorBinario, bits));
        return binario.toString(); 
    }

    private String mantisa(String binario, int bits) {
        int bitsMantisa;
        StringBuilder mantisa = new StringBuilder();
        int contUno = 0;
    
        for (int i = 0; i < binario.length(); i++) {
            char caracter = binario.charAt(i);
    
            if (contUno == 1) {
                contUno +=1;
            }

            if (caracter == '1') {
                contUno += 1;
            }

            if (contUno > 1) {
                if (caracter != '.') {
                    mantisa.append(caracter);
                }
            }
        }
    
        if (bits == 16) {
            bitsMantisa = 10;
        } else if (bits == 32) {
            bitsMantisa = 23;
        } else if (bits == 64) {
            bitsMantisa = 52;
        } else {
            bitsMantisa = 63;
        }
    
        while (mantisa.length() < bitsMantisa) {
            mantisa.append("0");
        }
    
        if (mantisa.length() > bitsMantisa) {
            mantisa.setLength(bitsMantisa);
        }
    
        return mantisa.toString();
    }
    
    private String exponente(String binario, int bits) {
        int valorASumar;
        int longitudExponente;
    
        if (bits == 16) {
            valorASumar = 15;
            longitudExponente = 5;
        } 
        else if (bits == 32) {
            valorASumar = 127;
            longitudExponente = 8;
        } 
        else if (bits == 64) {
            valorASumar = 1023; 
            longitudExponente = 11;
        } 
        else if (bits == 80) {
            valorASumar = 16383;
            longitudExponente = 15;
        } 
        else {
            throw new IllegalArgumentException("Formato no soportado: " + bits + " bits");
        }

        int punto = binario.indexOf('.');
        int primerUno = binario.indexOf('1');
        int contador = 0;
    
        if (primerUno != -1) {
            if (primerUno < punto) {
                contador = punto - primerUno - 1;
            } 
            else {
                contador = punto - primerUno;
            }
        } 
        else {
            contador = 0;
        }
    
        int resultado = valorASumar + contador;
    
        return String.format("%" + longitudExponente + "s", Integer.toBinaryString(resultado)).replace(' ', '0');
    }
    
    
    private String convertirABinario(double decimal) {
        if (decimal < 0) {
            decimal = decimal * (-1);
        }
        StringBuilder binario = new StringBuilder();
        int parteEntera = (int) decimal;
        double parteDecimal = decimal - parteEntera;
        binario.append(Integer.toBinaryString(parteEntera));
        binario.append(".");
        do {
            parteDecimal *= 2;
            if (parteDecimal >= 1) {
                binario.append("1");
                parteDecimal -= 1;
            } else {
                binario.append("0");
            }
        }while (parteDecimal > 0);
        return binario.toString();
    }

    private String binarioAHexadecimal(String binario) {
        while (binario.length() % 4 != 0) {
            binario = "0" + binario;
        }

        StringBuilder hexadecimal = new StringBuilder();
        for (int i = 0; i < binario.length(); i += 4) {
            String digito = binario.substring(i, i + 4);
            hexadecimal.append(Integer.toHexString(Integer.parseInt(digito, 2)));
        }
        return hexadecimal.toString().toUpperCase();
    }

    private String convertirALittleEndian(String hexadecimal) {
        while (hexadecimal.length() % 2 != 0) {
            hexadecimal = "0" + hexadecimal;
        }

        StringBuilder littleEndian = new StringBuilder();
        for (int i = hexadecimal.length() - 2; i >= 0; i -= 2) {
            littleEndian.append(hexadecimal, i, i + 2);
        }
        return littleEndian.toString();
    }
}