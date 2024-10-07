import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.Graphics2D;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class ConvertidorMonedaGUI extends JFrame {
    private JTextField importeField;
    private JComboBox<String> monedaOrigenCombo, monedaDestinoCombo;
    private JLabel resultadoLabel;
    private JButton convertirButton;
    private JTable historialTable;
    private DefaultTableModel tableModel;
    private DecimalFormat df = new DecimalFormat("#,##0.00");
    private Map<String, Double> tasasDeCambio = new HashMap<>();
    private static final String API_KEY = "da60150152a8e15f94eb9934";
    private static final String API_URL = "https://v6.exchangerate-api.com/v6/" + API_KEY + "/latest/USD";
    private JLabel simboloMonedaLabel;
    private JPanel importePanel;
    private JPanel mainPanel;
    private JPanel headerPanel;
    private JLabel titleLabel;
    private JLabel subtitleLabel;
    private JLabel importeLabel;

    private static final Map<String, String> NOMBRES_MONEDAS = new HashMap<>();
    static {
        NOMBRES_MONEDAS.put("USD", "Dólar estadounidense");
        NOMBRES_MONEDAS.put("EUR", "Euro");
        NOMBRES_MONEDAS.put("GBP", "Libra esterlina");
        NOMBRES_MONEDAS.put("JPY", "Yen japonés");
        NOMBRES_MONEDAS.put("CAD", "Dólar canadiense");
        NOMBRES_MONEDAS.put("AUD", "Dólar australiano");
        NOMBRES_MONEDAS.put("CHF", "Franco suizo");
        NOMBRES_MONEDAS.put("COP", "Peso colombiano");
        NOMBRES_MONEDAS.put("MXN", "Peso mexicano");
        NOMBRES_MONEDAS.put("BRL", "Real brasileño");
        NOMBRES_MONEDAS.put("ARS", "Peso argentino");
    }

    public ConvertidorMonedaGUI() {
        setTitle("Conversor de divisas de Juan Diego");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        add(createHeaderPanel(), BorderLayout.NORTH);
        mainPanel = createMainPanel();
        add(new JScrollPane(mainPanel), BorderLayout.CENTER);
        add(createHistorialPanel(), BorderLayout.SOUTH);

        obtenerTasasDeCambio();

        setSize(700, 500);
        setMinimumSize(new Dimension(500, 400));
        setLocationRelativeTo(null);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                adjustComponentSizes();
            }
        });
    }

    private JPanel createHeaderPanel() {
        headerPanel = new JPanel();
        headerPanel.setBackground(new Color(0, 51, 161));
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));

        titleLabel = new JLabel("Conversor de divisas de Juan Diego");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        subtitleLabel = new JLabel("Convierte tus divisas de manera sencilla y rápida.");
        subtitleLabel.setForeground(Color.WHITE);
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(Box.createVerticalStrut(10));
        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(5));
        headerPanel.add(subtitleLabel);
        headerPanel.add(Box.createVerticalStrut(10));

        return headerPanel;
    }

    private JPanel createMainPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Centrar la etiqueta "Importe"
        importeLabel = new JLabel("Importe:", SwingConstants.CENTER);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 5;
        panel.add(importeLabel, gbc);

        // Campo de importe centrado
        importeField = new JTextField(10);
        simboloMonedaLabel = new JLabel("$");
        importePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        importePanel.add(simboloMonedaLabel);
        importePanel.add(importeField);
        gbc.gridy = 1;
        panel.add(importePanel, gbc);

        String[] monedas = {"USD", "EUR", "GBP", "JPY", "CAD", "AUD", "CHF", "COP", "MXN", "BRL", "ARS"};
        monedaOrigenCombo = new JComboBox<>(monedas);
        monedaDestinoCombo = new JComboBox<>(monedas);
        styleComboBox(monedaOrigenCombo);
        styleComboBox(monedaDestinoCombo);

        resultadoLabel = new JLabel("Resultado: ");

        // Botón convertir
        convertirButton = new JButton("Convertir") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 15, 15));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        convertirButton.setPreferredSize(new Dimension(120, 30));
        convertirButton.setBackground(new Color(0, 102, 204));
        convertirButton.setForeground(Color.WHITE);
        convertirButton.setFocusPainted(false);
        convertirButton.setContentAreaFilled(false);
        convertirButton.setBorderPainted(false);

        JButton intercambiarButton = new JButton("⇄");
        intercambiarButton.addActionListener(e -> intercambiarMonedas());

        // Campos de divisa (manteniendo la disposición original)
        gbc.gridy = 2; gbc.gridwidth = 1;
        panel.add(new JLabel("De:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 1;
        panel.add(monedaOrigenCombo, gbc);
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE;
        panel.add(intercambiarButton, gbc);
        gbc.gridx = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(new JLabel("A:"), gbc);
        gbc.gridx = 4; gbc.gridwidth = 1;
        panel.add(monedaDestinoCombo, gbc);

        // Botón Convertir (centrado)
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 5;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(convertirButton);
        panel.add(buttonPanel, gbc);

        // Resultado
        gbc.gridy = 4;
        panel.add(resultadoLabel, gbc);

        convertirButton.addActionListener(e -> realizarConversion());

        monedaOrigenCombo.addActionListener(e -> actualizarSimboloMoneda());

        return panel;
    }

    private JPanel createHistorialPanel() {
        JPanel historialPanel = new JPanel(new BorderLayout());
        String[] columnNames = {"Fecha/Hora", "Conversión"};
        tableModel = new DefaultTableModel(columnNames, 0);
        historialTable = new JTable(tableModel);

        // Establecer una fuente inicial
        Font initialFont = new Font("Arial", Font.PLAIN, 12);
        historialTable.setFont(initialFont);
        historialTable.getTableHeader().setFont(initialFont);
        historialTable.setRowHeight(20); // Altura inicial de fila

        JScrollPane scrollPane = new JScrollPane(historialTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Historial de Conversiones"));
        historialPanel.add(scrollPane, BorderLayout.CENTER);
        return historialPanel;
    }

    private void realizarConversion() {
        try {
            double importe = Double.parseDouble(importeField.getText());
            String monedaOrigen = (String) monedaOrigenCombo.getSelectedItem();
            String monedaDestino = (String) monedaDestinoCombo.getSelectedItem();

            double tasaOrigen = tasasDeCambio.get(monedaOrigen);
            double tasaDestino = tasasDeCambio.get(monedaDestino);
            double resultado = importe / tasaOrigen * tasaDestino;

            String resultadoStr = String.format("%s %s = %s %s",
                    df.format(importe), monedaOrigen, df.format(resultado), monedaDestino);
            resultadoLabel.setText("Resultado: " + resultadoStr);

            // Agregar al historial
            LocalDateTime ahora = LocalDateTime.now();
            String fechaHora = ahora.format(DateTimeFormatter.ofPattern("dd/MM/yy HH:mm"));
            tableModel.addRow(new Object[]{fechaHora, resultadoStr});
        } catch (NumberFormatException ex) {
            resultadoLabel.setText("Error: Ingrese un número válido");
        } catch (Exception ex) {
            resultadoLabel.setText("Error en la conversión: " + ex.getMessage());
        }
    }

    private void obtenerTasasDeCambio() {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(API_URL)).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String jsonString = response.body();

            String ratesString = jsonString.split("\"conversion_rates\":\\{")[1].split("\\}")[0];
            String[] ratePairs = ratesString.split(",");
            for (String pair : ratePairs) {
                String[] keyValue = pair.split(":");
                String currency = keyValue[0].replaceAll("\"", "").trim();
                double rate = Double.parseDouble(keyValue[1]);
                tasasDeCambio.put(currency, rate);
            }
            System.out.println("Tasas de cambio obtenidas: " + tasasDeCambio);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al obtener tasas de cambio: " + e.getMessage());
        }
    }

    private void intercambiarMonedas() {
        Object temp = monedaOrigenCombo.getSelectedItem();
        monedaOrigenCombo.setSelectedItem(monedaDestinoCombo.getSelectedItem());
        monedaDestinoCombo.setSelectedItem(temp);
    }

    private void actualizarSimboloMoneda() {
        String monedaSeleccionada = (String) monedaOrigenCombo.getSelectedItem();
        switch (monedaSeleccionada) {
            case "USD":
            case "ARS": simboloMonedaLabel.setText("$"); break;
            case "EUR": simboloMonedaLabel.setText("€"); break;
            case "GBP": simboloMonedaLabel.setText("£"); break;
            case "JPY": simboloMonedaLabel.setText("¥"); break;
            case "BRL": simboloMonedaLabel.setText("R$"); break;
            case "COP": simboloMonedaLabel.setText("$"); break;
            case "MXN": simboloMonedaLabel.setText("$"); break;
            case "CAD": simboloMonedaLabel.setText("$"); break;
            case "AUD": simboloMonedaLabel.setText("$"); break;
            case "CHF": simboloMonedaLabel.setText("Fr."); break;
            default: simboloMonedaLabel.setText(monedaSeleccionada);
        }
    }

    private void styleComboBox(JComboBox<String> comboBox) {
        comboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof String) {
                    String codigo = (String) value;
                    String nombreCompleto = NOMBRES_MONEDAS.get(codigo);
                    setText(codigo + " - " + nombreCompleto);
                }
                return this;
            }
        });
        // Eliminar el tamaño fijo
        // comboBox.setPreferredSize(new Dimension(250, 30));
    }

    private void adjustComponentSizes() {
        int width = getWidth();
        int height = getHeight();

        // Ajustar tamaños de componentes basados en el tamaño de la ventana
        Font buttonFont = new Font("Arial", Font.BOLD, Math.max(14, width / 40));

        // Buscar el botón de intercambio y el botón "Convertir"
        JButton intercambiarButton = null;
        JButton convertirButton = null;
        for (Component comp : mainPanel.getComponents()) {
            if (comp instanceof JButton && "⇄".equals(((JButton) comp).getText())) {
                intercambiarButton = (JButton) comp;
            } else if (comp instanceof JPanel) {
                for (Component innerComp : ((JPanel) comp).getComponents()) {
                    if (innerComp instanceof JButton && "Convertir".equals(((JButton) innerComp).getText())) {
                        convertirButton = (JButton) innerComp;
                        break;
                    }
                }
            }
            if (intercambiarButton != null && convertirButton != null) {
                break;
            }
        }

        // Ajustar el tamaño de ambos botones
        if (intercambiarButton != null && convertirButton != null) {
            Dimension buttonSize = new Dimension(width / 5, height / 15);
            intercambiarButton.setPreferredSize(buttonSize);
            intercambiarButton.setFont(buttonFont);
            convertirButton.setPreferredSize(buttonSize);
            convertirButton.setFont(buttonFont);
        }

        // Ajustar el tamaño del panel de encabezado
        int headerHeight = height / 6; // El encabezado ocupará 1/6 de la altura total
        headerPanel.setPreferredSize(new Dimension(width, headerHeight));

        // Ajustar las fuentes del encabezado
        int titleFontSize = Math.max(20, width / 30);
        int subtitleFontSize = Math.max(14, width / 50);
        titleLabel.setFont(new Font("Arial", Font.BOLD, titleFontSize));
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, subtitleFontSize));

        // Ajustar el espacio vertical
        int verticalStrut = headerHeight / 10;
        ((Box.Filler) headerPanel.getComponent(0)).changeShape(new Dimension(0, verticalStrut), new Dimension(0, verticalStrut), new Dimension(0, verticalStrut));
        ((Box.Filler) headerPanel.getComponent(2)).changeShape(new Dimension(0, verticalStrut), new Dimension(0, verticalStrut), new Dimension(0, verticalStrut));
        ((Box.Filler) headerPanel.getComponent(4)).changeShape(new Dimension(0, verticalStrut), new Dimension(0, verticalStrut), new Dimension(0, verticalStrut));

        // Ajustar el tamaño de la etiqueta "Importe"
        importeLabel.setFont(new Font("Arial", Font.BOLD, Math.max(14, width / 45)));

        // Ajustar el tamaño del campo de importe
        int importeFieldWidth = width / 3;
        int importeFieldHeight = height / 20;
        importeField.setPreferredSize(new Dimension(importeFieldWidth, importeFieldHeight));
        importeField.setFont(new Font("Arial", Font.PLAIN, Math.max(12, width / 55)));

        // Ajustar el tamaño del símbolo de moneda
        simboloMonedaLabel.setFont(new Font("Arial", Font.PLAIN, Math.max(12, width / 55)));

        // Ajustar el tamaño de la fuente y el campo para las divisas
        Font divisaFont = new Font("Arial", Font.PLAIN, Math.max(12, width / 60));
        int comboBoxHeight = height / 15; // Aumentamos la altura vertical

        for (Component comp : mainPanel.getComponents()) {
            if (comp instanceof JLabel && comp != importeLabel) {
                comp.setFont(buttonFont);
            } else if (comp instanceof JButton) {
                comp.setFont(buttonFont);
                ((JButton) comp).setPreferredSize(new Dimension(width / 5, height / 15));
            } else if (comp instanceof JComboBox) {
                JComboBox<?> comboBox = (JComboBox<?>) comp;
                comboBox.setFont(divisaFont);
                comboBox.setPreferredSize(new Dimension(width / 3, comboBoxHeight));

                // Ajustar el renderizador de la lista para usar la nueva fuente
                ListCellRenderer<?> renderer = comboBox.getRenderer();
                if (renderer instanceof JLabel) {
                    ((JLabel) renderer).setFont(divisaFont);
                }
            }
        }

        // Ajustar específicamente monedaOrigenCombo y monedaDestinoCombo
        monedaOrigenCombo.setFont(divisaFont);
        monedaDestinoCombo.setFont(divisaFont);
        monedaOrigenCombo.setPreferredSize(new Dimension(width / 3, comboBoxHeight));
        monedaDestinoCombo.setPreferredSize(new Dimension(width / 3, comboBoxHeight));

        // Actualizar el renderizador de la lista para los ComboBox de divisas
        updateComboBoxRenderer(monedaOrigenCombo, divisaFont);
        updateComboBoxRenderer(monedaDestinoCombo, divisaFont);

        // Ajustar el tamaño de la tabla de historial
        int historialHeight = Math.max(100, height / 4); // Mínimo 100 píxeles o 1/4 de la altura de la ventana
        historialTable.setPreferredScrollableViewportSize(new Dimension(width, historialHeight));

        // Ajustar el tamaño de la fuente de la tabla de historial
        int fontSize = Math.max(12, width / 60); // Tamaño mínimo de 12, aumenta con el ancho de la ventana
        Font historialFont = new Font("Arial", Font.PLAIN, fontSize);
        historialTable.setFont(historialFont);
        historialTable.getTableHeader().setFont(historialFont);

        // Ajustar la altura de las filas de la tabla
        int rowHeight = Math.max(20, height / 25); // Altura mínima de 20, aumenta con la altura de la ventana
        historialTable.setRowHeight(rowHeight);

        // Ajustar el tamaño del panel que contiene la tabla de historial
        Component historialPanel = getContentPane().getComponent(2); // Asumiendo que es el tercer componente
        if (historialPanel instanceof JPanel) {
            historialPanel.setPreferredSize(new Dimension(width, historialHeight + 30)); // +30 para el borde y el título
        }

        revalidate();
        repaint();
    }

    private void updateComboBoxRenderer(JComboBox<String> comboBox, Font font) {
        comboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel rendererComponent = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof String) {
                    String codigo = (String) value;
                    String nombreCompleto = NOMBRES_MONEDAS.get(codigo);
                    rendererComponent.setText(codigo + " - " + nombreCompleto);
                }
                rendererComponent.setFont(font);
                return rendererComponent;
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ConvertidorMonedaGUI conversor = new ConvertidorMonedaGUI();
            conversor.setVisible(true);
        });
    }
}
