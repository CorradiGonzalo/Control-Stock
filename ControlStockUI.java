import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ControlStockUI extends JFrame {

    private final HerramientaDAO dao = new HerramientaDAOImpl();
    private final CotizadorService cotizador = new CotizadorService();

    private JTable tablaInventario;
    private DefaultTableModel modeloTabla;
    private JTextField txtCodigoCotizar;
    private JSpinner spnCantidad;
    private JTextArea areaResultadoCotizar;

    private JComboBox<String> comboTipo;
    private JTextField txtCodigo, txtPrecioBase, txtStock;
    private JTextField txtDientes, txtTipoCorte;
    private JTextField txtMaterial, txtAngulo;
    private JPanel panelFresa, panelMecha;

    public ControlStockUI() {
        setTitle("Control de Stock - Herramientas");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 500);
        setLocationRelativeTo(null);

        aplicarLookAndFeel();

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Inventario", crearPanelInventario());
        tabs.addTab("Cotizar", crearPanelCotizar());
        tabs.addTab("Agregar herramienta", crearPanelAgregar());

        add(tabs);
    }

    private void aplicarLookAndFeel() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) { }
    }

    private JPanel crearPanelInventario() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));

        String[] columnas = { "Código", "Tipo", "Stock", "Precio base", "Detalles" };
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tablaInventario = new JTable(modeloTabla);
        tablaInventario.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaInventario.getTableHeader().setReorderingAllowed(false);
        JScrollPane scroll = new JScrollPane(tablaInventario);

        JButton btnActualizar = new JButton("Actualizar inventario");
        btnActualizar.addActionListener(e -> actualizarTabla());

        panel.add(scroll, BorderLayout.CENTER);
        panel.add(btnActualizar, BorderLayout.SOUTH);
        actualizarTabla();
        return panel;
    }

    private void actualizarTabla() {
        modeloTabla.setRowCount(0);
        List<Herramienta> lista = dao.listarTodos();
        for (Herramienta h : lista) {
            String tipo = h instanceof Fresa ? "Fresa" : "Mecha";
            String detalles = "";
            if (h instanceof Fresa f) {
                detalles = f.getNumeroDeDientes() + " dientes, " + f.getTipoDeCorte();
            } else if (h instanceof Mecha m) {
                detalles = m.getMaterial() + ", " + m.getAnguloPunta() + "°";
            }
            modeloTabla.addRow(new Object[]{
                h.getCodigo(),
                tipo,
                h.getStock(),
                String.format("%.2f", h.getPrecioBase()),
                detalles
            });
        }
    }

    private JPanel crearPanelCotizar() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));

        JPanel superior = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        superior.add(new JLabel("Código:"));
        txtCodigoCotizar = new JTextField(12);
        superior.add(txtCodigoCotizar);
        superior.add(new JLabel("Cantidad:"));
        spnCantidad = new JSpinner(new SpinnerNumberModel(1, 1, 9999, 1));
        superior.add(spnCantidad);
        JButton btnCotizar = new JButton("Generar cotización");
        btnCotizar.addActionListener(e -> generarCotizacion());
        superior.add(btnCotizar);

        areaResultadoCotizar = new JTextArea(8, 50);
        areaResultadoCotizar.setEditable(false);
        areaResultadoCotizar.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        JScrollPane scrollResultado = new JScrollPane(areaResultadoCotizar);

        panel.add(superior, BorderLayout.NORTH);
        panel.add(scrollResultado, BorderLayout.CENTER);
        return panel;
    }

    private void generarCotizacion() {
        String codigo = txtCodigoCotizar.getText().trim();
        if (codigo.isEmpty()) {
            areaResultadoCotizar.setText("Ingrese un código de producto.");
            return;
        }
        int cantidad = (Integer) spnCantidad.getValue();
        areaResultadoCotizar.setText("");

        try {
            double precioFinal = cotizador.generarCotizacion(codigo, cantidad);
            Herramienta h = dao.buscarPorCodigo(codigo);
            StringBuilder sb = new StringBuilder();
            sb.append("--- COTIZACIÓN ---\n");
            sb.append("Producto: ").append(codigo).append("\n");
            sb.append("Cantidad: ").append(cantidad).append("\n");
            sb.append("Precio unitario (lista): $").append(String.format("%.2f", h.calcularPrecioLista())).append("\n");
            sb.append("TOTAL: $").append(String.format("%.2f", precioFinal)).append("\n\n");
            sb.append("Stock en inventario: ").append(h.getStock()).append(" unidades.");
            if (h.getStock() < cantidad) {
                sb.append("\n\n⚠ ALERTA: Stock insuficiente para esta cantidad.");
            }
            areaResultadoCotizar.setText(sb.toString());
        } catch (Exception ex) {
            areaResultadoCotizar.setText("Error: " + ex.getMessage());
        }
    }

    private JPanel crearPanelAgregar() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));

        JPanel pTipo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pTipo.add(new JLabel("Tipo:"));
        comboTipo = new JComboBox<>(new String[] { "Fresa", "Mecha" });
        comboTipo.addActionListener(e -> alternarCamposTipo());
        pTipo.add(comboTipo);
        panel.add(pTipo);

        JPanel pBase = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pBase.add(new JLabel("Código:"));
        txtCodigo = new JTextField(12);
        pBase.add(txtCodigo);
        pBase.add(new JLabel("Precio base:"));
        txtPrecioBase = new JTextField(8);
        pBase.add(txtPrecioBase);
        pBase.add(new JLabel("Stock:"));
        txtStock = new JTextField(6);
        pBase.add(txtStock);
        panel.add(pBase);

        panelFresa = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelFresa.add(new JLabel("Dientes:"));
        txtDientes = new JTextField(6);
        panelFresa.add(txtDientes);
        panelFresa.add(new JLabel("Tipo de corte:"));
        txtTipoCorte = new JTextField(12);
        panelFresa.add(txtTipoCorte);
        panel.add(panelFresa);

        panelMecha = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelMecha.add(new JLabel("Material:"));
        txtMaterial = new JTextField(12);
        panelMecha.add(txtMaterial);
        panelMecha.add(new JLabel("Ángulo punta (°):"));
        txtAngulo = new JTextField(6);
        panelMecha.add(txtAngulo);
        panel.add(panelMecha);
        panelMecha.setVisible(false);

        JButton btnGuardar = new JButton("Guardar herramienta");
        btnGuardar.addActionListener(e -> guardarHerramienta());
        panel.add(btnGuardar);

        return panel;
    }

    private void alternarCamposTipo() {
        boolean esFresa = "Fresa".equals(comboTipo.getSelectedItem());
        panelFresa.setVisible(esFresa);
        panelMecha.setVisible(!esFresa);
    }

    private void guardarHerramienta() {
        String codigo = txtCodigo.getText().trim();
        if (codigo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese un código.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        double precioBase;
        int stock;
        try {
            precioBase = Double.parseDouble(txtPrecioBase.getText().trim().replace(',', '.'));
            stock = Integer.parseInt(txtStock.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Precio base y stock deben ser números válidos.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Herramienta h;
        if ("Fresa".equals(comboTipo.getSelectedItem())) {
            int dientes;
            try {
                dientes = Integer.parseInt(txtDientes.getText().trim());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Dientes debe ser un número entero.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String tipoCorte = txtTipoCorte.getText().trim();
            if (tipoCorte.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Ingrese el tipo de corte.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            h = new Fresa(codigo, precioBase, stock, dientes, tipoCorte);
        } else {
            String material = txtMaterial.getText().trim();
            if (material.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Ingrese el material.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int angulo;
            try {
                angulo = Integer.parseInt(txtAngulo.getText().trim());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Ángulo debe ser un número entero.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            h = new Mecha(codigo, precioBase, stock, material, angulo);
        }

        if (dao.buscarPorCodigo(codigo) != null) {
            JOptionPane.showMessageDialog(this, "Ya existe una herramienta con el código " + codigo + ". Use otro código.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        dao.guardar(h);
        actualizarTabla();
        JOptionPane.showMessageDialog(this, "Herramienta guardada: " + codigo, "Éxito", JOptionPane.INFORMATION_MESSAGE);
        limpiarFormularioAgregar();
    }

    private void limpiarFormularioAgregar() {
        txtCodigo.setText("");
        txtPrecioBase.setText("");
        txtStock.setText("");
        txtDientes.setText("");
        txtTipoCorte.setText("");
        txtMaterial.setText("");
        txtAngulo.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            HerramientaDAO dao = new HerramientaDAOImpl();
            dao.crearTabla();
            ControlStockUI ui = new ControlStockUI();
            ui.setVisible(true);
        });
    }
}
