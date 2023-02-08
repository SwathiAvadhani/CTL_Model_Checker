package view;
import controller.CTLFormulaFileObj;
import model.Kripke;
import model.State;
import utils.Utilities;


import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;


public class UserInterface extends JFrame {

    private final JLabel model;
    private final JTextArea modelText;
	private final JTextField ctlFormula;
    private final JTextArea resultArea;
    private final JComboBox<String> jComboBox;
    private final JFrame jFrame;

    private static Kripke kripke;

    public UserInterface(){
        //constructor
        jFrame = new JFrame();
        jFrame.setTitle("CTL Model Checker");
        jFrame.setResizable(true);
        jFrame.setSize(800,1000);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel p = new JPanel();
        p.setLayout(null);
        
        JLabel title = new JLabel("CTL Model Checker - Group 1");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setSize(800, 30);
        title.setForeground(Color.blue);
        title.setLocation(380, 30);
        p.add(title);
        
        p.setBorder(new EmptyBorder(15, 15, 15, 15));
        Border border = BorderFactory.createRaisedBevelBorder();
        p.setBorder(border);

        JLabel file = new JLabel("Please select the file: ");
        file.setForeground(Color.black);
        file.setFont(new Font("Arial", Font.PLAIN, 20));
        file.setSize(500, 20);
        file.setLocation(100, 100);
        p.add(file);
        
        JButton button1 = new JButton("Upload File");
        button1.addActionListener(new UploadFileListener());
        button1.setFont(new Font("Arial", Font.PLAIN, 20));
        button1.setSize(250, 30);
        button1.setLocation(400, 100);
        button1.setBackground(Color.gray);
        button1.setForeground(Color.red);
        button1.setBorder(BorderFactory.createRaisedBevelBorder());
        p.add(button1);
        p.setBorder(new EmptyBorder(15, 15, 15, 15));

        model = new JLabel("Model: ");
        model.setFont(new Font("Arial", Font.PLAIN, 20));
        model.setSize(100, 20);
        model.setLocation(100, 150);
        p.add(model);

        modelText = new JTextArea(25, 40);
        modelText.setEditable(false);
        modelText.setFont(new Font("Arial", Font.PLAIN, 12));
        modelText.setBackground(Color.lightGray);
        modelText.setSize(700, 125);
        modelText.setLocation(300, 150);
        modelText.setLineWrap(true);
        modelText.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(modelText);
        scrollPane.setLocation(300, 150);
        modelText.setBorder(border);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setPreferredSize(new Dimension(250, 250));
        p.add(modelText);
        //p.add(scrollPane, BorderLayout.CENTER);
        //p.add(scrollPane);

        jComboBox = new JComboBox<>();
        jComboBox.setSize(150, 20);
        jComboBox.setLocation(400, 300);
        jComboBox.setBackground(Color.lightGray);
        jComboBox.setForeground(Color.blue);
        jComboBox.setFont(new Font("Arial", Font.BOLD, 15));
        
        JLabel state = new JLabel("Please select the state: ");
        state.setFont(new Font("Arial", Font.PLAIN, 20));
        state.setSize(300, 20);
        state.setLocation(100, 300);
        p.add(state);
        p.add(jComboBox);
        
        JLabel formula = new JLabel("Enter the CTL Formula: ");
        formula.setFont(new Font("Arial", Font.PLAIN, 20));
        formula.setSize(300, 20);
        formula.setLocation(100, 360);
        p.add(formula);
        
        ctlFormula =  new JTextField(4);
        ctlFormula.setFont(new Font("Arial", Font.PLAIN, 15));
        ctlFormula.setSize(300, 50);
        ctlFormula.setLocation(400, 350);
        p.add(ctlFormula);
        p.setBorder(new EmptyBorder(1, 1, 2, 1));

        JButton button2 = new JButton("Check");
        button2.addActionListener(new UserInterface.CheckActionListener());
        button2.setBackground(Color.gray);
        button2.setForeground(Color.red);
        button2.setBorder(BorderFactory.createRaisedBevelBorder());
        button2.setFont(new Font("Calibri", Font.BOLD, 20));
        button2.setSize(200, 30);
        button2.setLocation(750, 360);
        
        p.add(button2);
        p.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel result = new JLabel("Result: ",JLabel.CENTER);
        result.setFont(new Font("Arial", Font.PLAIN, 20));
        result.setSize(100, 20);
        result.setLocation(80, 450);
        
        p.add(result);

        resultArea = new JTextArea(3, 25);
        resultArea.setEditable(false);
        resultArea.setBorder(border);
        resultArea.setFont(new Font("Calibri", Font.ITALIC, 20));
        resultArea.setSize(550, 50);
        resultArea.setBackground(Color.lightGray);
        resultArea.setLocation(400, 450);
        
        p.add(resultArea);

        jFrame.setPreferredSize(new Dimension(1100, 650));
        jFrame.setContentPane(p);
        jFrame.pack();
        jFrame.setVisible(true);
        jFrame.setLocationRelativeTo(null);

    }

    private void ClearModel() {
        modelText.setText("");
        model.setText("Model");
        if(jComboBox.getSelectedIndex() != -1) {
            DefaultComboBoxModel theModel = (DefaultComboBoxModel) jComboBox.getModel();
            theModel.removeAllElements();
        }
    }

    public class UploadFileListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            ClearModel();
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new java.io.File("."));
            fileChooser.setFileFilter(new FileNameExtensionFilter("TEXT FILES", "txt", "text"));
            fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            int Value  = fileChooser.showOpenDialog(jFrame);
            if(Value == JFileChooser.APPROVE_OPTION) {
                try {
                    new BufferedReader(new FileReader(fileChooser.getSelectedFile()));
                    System.out.println("Selected File"+fileChooser.getSelectedFile());
                    File filePath = fileChooser.getSelectedFile();

                    try {
                        if(filePath == null) {
                            String message  = "Please upload a File!";
                            JOptionPane.showMessageDialog(new JFrame(), message, "Comment",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                        assert filePath != null;
                        System.out.println("PATH: "+ filePath.getAbsolutePath());
                        String file = Utilities.readFile((filePath.getAbsolutePath()));
                        kripke = new Kripke(Utilities.cleanText(file));

                        ClearModel();
                        for(String s: kripke.getAllStates()) {
                            jComboBox.addItem(s);
                        }
                        String modelName = filePath.getAbsolutePath().substring(filePath.getAbsolutePath().lastIndexOf('M'));
                        model.setText(modelName);
                        modelText.setText(kripke.toString());
                    }catch(Exception kse) {
                        kse.printStackTrace();
                        JOptionPane.showMessageDialog(new JFrame(), kse.getMessage(), "Dialog",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }catch(IOException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(new JFrame(), ex.getMessage(), "Dialog",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    public class CheckActionListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            resultArea.setText("");
            System.out.println("Clicked: " + e.getActionCommand()+" "+ctlFormula.getText());
            try {
                if (kripke == null)
                {
                    throw new Exception("Please load Kripke model");
                }
                if(ctlFormula.getText().isEmpty()) {
                    throw new Exception("Please enter CTL formula!");
                }
                String originalExpression = ctlFormula.getText();
                String expression = originalExpression.replaceAll("\\s", "");
                System.out.println("Model.State  "+ Objects.requireNonNull(jComboBox.getSelectedItem()));
                String checkedStateID = jComboBox.getSelectedItem().toString();

                State checkedState = new State(checkedStateID);

                CTLFormulaFileObj ctlFormula = new CTLFormulaFileObj(expression, checkedState, kripke);
                boolean isSatisfy = ctlFormula.IsSatisfy();
                String message = Utilities.GetMessage(isSatisfy, originalExpression, checkedStateID);
                resultArea.setText("");
                resultArea.append(message);
                System.out.println(message);
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                JOptionPane.showMessageDialog(new JFrame(), e1.getMessage(), "Dialog",
                        JOptionPane.ERROR_MESSAGE);
            }

        }
    }


}
