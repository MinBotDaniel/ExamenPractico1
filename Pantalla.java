import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Pantalla extends JFrame {

    private JButton botonDecimal;
    private JButton botonLittleEndian;

    public Pantalla() {
        setTitle("Examen Práctico");
        setSize(390, 100);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        botonDecimal = new JButton("Decimal");
        botonDecimal.setBounds(20, 20, 150, 25);
        add(botonDecimal);

        botonLittleEndian = new JButton("Endian");
        botonLittleEndian.setBounds(175, 20, 200, 25);
        add(botonLittleEndian);

        botonDecimal.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DecimalAMaquina obj1 = new DecimalAMaquina();
                obj1.setVisible(true);
            }
        });

        botonLittleEndian.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MaquinaADecimal obj2 = new MaquinaADecimal();
                obj2.setVisible(true);
            }
        });
    }
}
