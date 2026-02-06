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
    
    // Componentes Cotizador
    private JTextField txtCodigoCotizar;
    private JSpinner spnCantidad;
    private JTextArea areaResultadoCotizar;

    // Componentes Agregar
    private JComboBox<String> comboTipo;
    private JTextField txtCodigo, txtDescripcion, txtDeposito, txtPrecioBase, txtCantidad;
    private JComboBox<Unidad> comboUnidad;
    
    // Paneles Específicos
    private JPanel panelFresa, panelMecha, panelPolvo, panelBarra, panelBuje, panelVarios;
    
    // Campos Específicos
    private JTextField txtDientes, txtTipoCorte; // Fresa
    private JTextField txtMatMecha, txtAngulo; // Mecha
    private JTextField txtMatPolvo; // Polvo
    // Barra
    private JTextField txtMatBarra, txtLargoBarra, txtDiamBarra;
    private JCheckBox chkPerforada;
    private JPanel panelDetallesPerforacion;
    private JComboBox<String> comboTipoPerf;
    private JTextField txtDiamAgujeros, txtSepAgujeros;
    // Buje
    private JTextField txtMatBuje, txtODBuje, txtIDBuje, txtLargoBuje;
    // Varios
    private JComboBox<String> comboTipoVarios;
    private JTextField txtSpecsVarios;

    public ControlStockUI() {
        setTitle("Sistema Antares PRO v3.1 - Gestión de Planta");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 750); // Un poco más ancho para que entren los botones
        setLocationRelativeTo(null);
        aplicarLookAndFeel();

        JTabbedPane tabs = new JTabbedPane();
        
        // --- AQUÍ ESTÁ EL CAMBIO DE ORDEN ---
        tabs.addTab("Inventario General", crearPanelInventario()); // 1. Lo más importante
        tabs.addTab("Alta de Items", crearPanelAgregar());       // 2. Carga de datos
        tabs.addTab("Cotizar", crearPanelCotizar());             // 3. Al final (menos usado)

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

        String[] columnas = { "Cód", "Desc", "Stock", "Unid", "Ubic", "$ Base", "Tipo", "Detalles Técnicos" };
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tablaInventario = new JTable(modeloTabla);
        tablaInventario.getColumnModel().getColumn(1).setPreferredWidth(150); 
        tablaInventario.getColumnModel().getColumn(7).setPreferredWidth(300);
        
        JScrollPane scroll = new JScrollPane(tablaInventario);
        panel.add(scroll, BorderLayout.CENTER);

        // --- BOTONERA DE ACCIONES (CRUD) ---
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        
        JButton btnEntrada = new JButton("➕ Entrada (Sumar)");
        btnEntrada.setBackground(new Color(188, 245, 188)); // Verde
        btnEntrada.addActionListener(e -> ajustarStock(true));
        
        JButton btnSalida = new JButton("➖ Salida (Restar)");
        btnSalida.setBackground(new Color(255, 204, 204)); // Rojo
        btnSalida.addActionListener(e -> ajustarStock(false));

        JButton btnEliminar = new JButton("🗑️ Eliminar Item");
        btnEliminar.addActionListener(e -> eliminarItem());

        JButton btnRefrescar = new JButton("🔄 Refrescar");
        btnRefrescar.addActionListener(e -> actualizarTabla());

        panelBotones.add(btnEntrada);
        panelBotones.add(btnSalida);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnRefrescar);

        panel.add(panelBotones, BorderLayout.SOUTH);

        actualizarTabla();
        return panel;
    }

    private void actualizarTabla() {
        modeloTabla.setRowCount(0);
        List<Herramienta> lista = dao.listarTodos();
        for (Herramienta h : lista) {
            String tipo = "";
            String detalles = "";
            
            if (h instanceof Fresa f) {
                tipo = "FRESA";
                detalles = "Z=" + f.getNumeroDeDientes() + ", " + f.getTipoDeCorte();
            } else if (h instanceof Mecha m) {
                tipo = "MECHA";
                detalles = m.getMaterial() + ", " + m.getAnguloPunta() + "°";
            } else if (h instanceof Polvo p) {
                tipo = "POLVO";
                detalles = p.getMaterial();
            } else if (h instanceof Barra b) {
                tipo = "BARRA";
                detalles = String.format("ø%.1f x %smm | %s", b.getDiametro(), b.getLargo(), b.getMaterial());
                if (b.isEsPerforada()) detalles += " [Perf]";
            } else if (h instanceof Buje bu) {
                tipo = "BUJE";
                detalles = String.format("OD:%.1f ID:%.1f L:%.1f | %s", 
                    bu.getDiametroExterior(), bu.getDiametroInterior(), bu.getLargo(), bu.getMaterial());
            } else if (h instanceof Varios v) {
                tipo = "VARIOS";
                detalles = v.getTipoEspecifico() + ": " + v.getEspecificaciones();
            }
            
            modeloTabla.addRow(new Object[]{
                h.getCodigo(), h.getDescripcion(), h.getCantidad(), h.getUnidad().getSimbolo(),
                h.getDeposito(), String.format("$%.2f", h.getPrecioBase()), tipo, detalles
            });
        }
    }

    private JPanel crearPanelCotizar() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));
        JPanel superior = new JPanel(new FlowLayout(FlowLayout.LEFT));
        superior.add(new JLabel("Código:"));
        txtCodigoCotizar = new JTextField(12);
        superior.add(txtCodigoCotizar);
        superior.add(new JLabel("Cantidad:"));
        spnCantidad = new JSpinner(new SpinnerNumberModel(1.0, 0.0, 99999.0, 0.5));
        superior.add(spnCantidad);
        JButton btn = new JButton("Cotizar");
        btn.addActionListener(e -> generarCotizacion());
        superior.add(btn);
        areaResultadoCotizar = new JTextArea(10, 50);
        panel.add(superior, BorderLayout.NORTH);
        panel.add(new JScrollPane(areaResultadoCotizar), BorderLayout.CENTER);
        return panel;
    }

    private void generarCotizacion() {
        try {
            String cod = txtCodigoCotizar.getText();
            double cant = (Double) spnCantidad.getValue();
            double total = cotizador.generarCotizacion(cod, cant);
            Herramienta h = dao.buscarPorCodigo(cod);
            areaResultadoCotizar.setText("Producto: " + h.getDescripcion() + "\nTotal Estimado: $" + total);
        } catch (Exception e) {
            areaResultadoCotizar.setText(e.getMessage());
        }
    }

    private JPanel crearPanelAgregar() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));

        // 1. Selector de Tipo
        JPanel pTipo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pTipo.add(new JLabel("TIPO DE ITEM:"));
        comboTipo = new JComboBox<>(new String[] { "Fresa", "Mecha", "Polvo", "Barra", "Buje", "Varios" });
        comboTipo.addActionListener(e -> actualizarFormulario());
        comboTipo.setFont(new Font("Arial", Font.BOLD, 14));
        pTipo.add(comboTipo);
        panel.add(pTipo);

        // 2. Datos Generales
        panel.add(crearFila("Código:", txtCodigo = new JTextField(10)));
        panel.add(crearFila("Descripción:", txtDescripcion = new JTextField(25)));
        
        JPanel pUnid = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pUnid.add(new JLabel("Unidad:"));
        comboUnidad = new JComboBox<>(Unidad.values());
        pUnid.add(comboUnidad);
        pUnid.add(new JLabel(" Cantidad:"));
        txtCantidad = new JTextField(6);
        pUnid.add(txtCantidad);
        panel.add(pUnid);

        panel.add(crearFila("Ubicación:", txtDeposito = new JTextField(15)));
        panel.add(crearFila("Precio Base ($):", txtPrecioBase = new JTextField(8)));

        panel.add(new JSeparator());

        // --- PANELES ESPECÍFICOS ---

        // Panel Fresa
        panelFresa = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelFresa.add(new JLabel("Dientes:"));
        txtDientes = new JTextField(4);
        panelFresa.add(txtDientes);
        panelFresa.add(new JLabel("Corte:"));
        txtTipoCorte = new JTextField(10);
        panelFresa.add(txtTipoCorte);
        panel.add(panelFresa);

        // Panel Mecha
        panelMecha = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelMecha.add(new JLabel("Material:"));
        txtMatMecha = new JTextField(10);
        panelMecha.add(txtMatMecha);
        panelMecha.add(new JLabel("Ángulo:"));
        txtAngulo = new JTextField(4);
        panelMecha.add(txtAngulo);
        panel.add(panelMecha);

        // Panel Polvo
        panelPolvo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelPolvo.add(new JLabel("Material Químico:"));
        txtMatPolvo = new JTextField(15);
        panelPolvo.add(txtMatPolvo);
        panel.add(panelPolvo);

        // Panel Barra
        panelBarra = new JPanel();
        panelBarra.setLayout(new BoxLayout(panelBarra, BoxLayout.Y_AXIS));
        panelBarra.setBorder(BorderFactory.createTitledBorder("Datos Barra"));
        JPanel pBarra1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pBarra1.add(new JLabel("Mat:")); txtMatBarra = new JTextField(8); pBarra1.add(txtMatBarra);
        pBarra1.add(new JLabel("Largo:")); txtLargoBarra = new JTextField(5); pBarra1.add(txtLargoBarra);
        pBarra1.add(new JLabel("ø Ext:")); txtDiamBarra = new JTextField(5); pBarra1.add(txtDiamBarra);
        panelBarra.add(pBarra1);
        chkPerforada = new JCheckBox("¿Perforada?");
        chkPerforada.addActionListener(e -> panelDetallesPerforacion.setVisible(chkPerforada.isSelected()));
        panelBarra.add(chkPerforada);
        panelDetallesPerforacion = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelDetallesPerforacion.add(new JLabel("Tipo:"));
        comboTipoPerf = new JComboBox<>(new String[]{"Recta", "Helicoidal"});
        panelDetallesPerforacion.add(comboTipoPerf);
        panelDetallesPerforacion.add(new JLabel("ø Aguj:")); txtDiamAgujeros = new JTextField(4); panelDetallesPerforacion.add(txtDiamAgujeros);
        panelDetallesPerforacion.add(new JLabel("Sep:")); txtSepAgujeros = new JTextField(4); panelDetallesPerforacion.add(txtSepAgujeros);
        panelBarra.add(panelDetallesPerforacion);
        panel.add(panelBarra);

        // Panel Buje
        panelBuje = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelBuje.setBorder(BorderFactory.createTitledBorder("Datos Buje"));
        panelBuje.add(new JLabel("Mat:")); txtMatBuje = new JTextField(8); panelBuje.add(txtMatBuje);
        panelBuje.add(new JLabel("OD:")); txtODBuje = new JTextField(4); panelBuje.add(txtODBuje);
        panelBuje.add(new JLabel("ID:")); txtIDBuje = new JTextField(4); panelBuje.add(txtIDBuje);
        panelBuje.add(new JLabel("Largo:")); txtLargoBuje = new JTextField(4); panelBuje.add(txtLargoBuje);
        panel.add(panelBuje);

        // Panel Varios
        panelVarios = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelVarios.setBorder(BorderFactory.createTitledBorder("Clasificación Varios"));
        panelVarios.add(new JLabel("Tipo:"));
        comboTipoVarios = new JComboBox<>(new String[]{"Inserto Triangular", "Inserto Redondo", "Placa", "Tornillo", "Otro"});
        panelVarios.add(comboTipoVarios);
        panelVarios.add(new JLabel("Especificaciones:"));
        txtSpecsVarios = new JTextField(15);
        panelVarios.add(txtSpecsVarios);
        panel.add(panelVarios);

        // Botón Guardar
        JButton btnGuardar = new JButton("💾 GUARDAR ITEM");
        btnGuardar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnGuardar.addActionListener(e -> guardar());
        panel.add(Box.createVerticalStrut(15));
        panel.add(btnGuardar);

        actualizarFormulario(); 
        return panel;
    }

    private void actualizarFormulario() {
        String tipo = (String) comboTipo.getSelectedItem();
        panelFresa.setVisible("Fresa".equals(tipo));
        panelMecha.setVisible("Mecha".equals(tipo));
        panelPolvo.setVisible("Polvo".equals(tipo));
        panelBarra.setVisible("Barra".equals(tipo));
        panelBuje.setVisible("Buje".equals(tipo));
        panelVarios.setVisible("Varios".equals(tipo));
        
        if ("Barra".equals(tipo)) panelDetallesPerforacion.setVisible(chkPerforada.isSelected());
    }

    private void guardar() {
        try {
            String tipo = (String) comboTipo.getSelectedItem();
            String cod = txtCodigo.getText();
            String desc = txtDescripcion.getText();
            Unidad uni = (Unidad) comboUnidad.getSelectedItem();
            String depo = txtDeposito.getText();
            double cant = Double.parseDouble(txtCantidad.getText().replace(',', '.'));
            double precio = Double.parseDouble(txtPrecioBase.getText().replace(',', '.'));

            Herramienta h = null;

            if ("Fresa".equals(tipo)) {
                h = new Fresa(cod, desc, uni, depo, cant, precio, Integer.parseInt(txtDientes.getText()), txtTipoCorte.getText());
            } else if ("Mecha".equals(tipo)) {
                h = new Mecha(cod, desc, uni, depo, cant, precio, txtMatMecha.getText(), Integer.parseInt(txtAngulo.getText()));
            } else if ("Polvo".equals(tipo)) {
                h = new Polvo(cod, desc, uni, depo, cant, precio, txtMatPolvo.getText());
            } else if ("Barra".equals(tipo)) {
                boolean perf = chkPerforada.isSelected();
                h = new Barra(cod, desc, uni, depo, cant, precio, txtMatBarra.getText(), Double.parseDouble(txtLargoBarra.getText()), Double.parseDouble(txtDiamBarra.getText()), perf, perf ? (String)comboTipoPerf.getSelectedItem() : "MACIZA", perf ? Double.parseDouble(txtDiamAgujeros.getText()) : 0, perf ? Double.parseDouble(txtSepAgujeros.getText()) : 0);
            } else if ("Buje".equals(tipo)) {
                h = new Buje(cod, desc, uni, depo, cant, precio, 
                    txtMatBuje.getText(), 
                    Double.parseDouble(txtODBuje.getText()), 
                    Double.parseDouble(txtIDBuje.getText()), 
                    Double.parseDouble(txtLargoBuje.getText()));
            } else if ("Varios".equals(tipo)) {
                h = new Varios(cod, desc, uni, depo, cant, precio, 
                    (String) comboTipoVarios.getSelectedItem(), 
                    txtSpecsVarios.getText());
            }

            dao.guardar(h);
            JOptionPane.showMessageDialog(this, "Guardado Correctamente");
            actualizarTabla();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al guardar (revise datos numéricos): " + e.getMessage());
        }
    }

    // --- MÉTODOS DE LÓGICA DE STOCK (SUMAR / RESTAR / ELIMINAR) ---

    private void ajustarStock(boolean sumar) {
        int fila = tablaInventario.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un item de la tabla primero.");
            return;
        }

        String codigo = (String) modeloTabla.getValueAt(fila, 0);
        String desc = (String) modeloTabla.getValueAt(fila, 1);
        double stockActual = (Double) modeloTabla.getValueAt(fila, 2);
        String unidad = (String) modeloTabla.getValueAt(fila, 3);

        String accion = sumar ? "INGRESAR" : "RETIRAR";
        String input = JOptionPane.showInputDialog(this, 
                "Item: " + desc + "\nStock Actual: " + stockActual + " " + unidad + 
                "\n\nCantidad a " + accion + ":");

        if (input != null && !input.isEmpty()) {
            try {
                double cantidad = Double.parseDouble(input.replace(',', '.'));
                if (cantidad <= 0) {
                    JOptionPane.showMessageDialog(this, "La cantidad debe ser mayor a 0.");
                    return;
                }

                double nuevoStock;
                if (sumar) {
                    nuevoStock = stockActual + cantidad;
                } else {
                    if (cantidad > stockActual) {
                        JOptionPane.showMessageDialog(this, "⚠️ Error: No puedes retirar más de lo que hay.");
                        return;
                    }
                    nuevoStock = stockActual - cantidad;
                }

                dao.actualizarStock(codigo, nuevoStock);
                actualizarTabla(); 
                JOptionPane.showMessageDialog(this, "✅ Stock actualizado. Nuevo total: " + nuevoStock);

            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Número no válido.");
            }
        }
    }

    private void eliminarItem() {
        int fila = tablaInventario.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un item para eliminar.");
            return;
        }

        String codigo = (String) modeloTabla.getValueAt(fila, 0);
        String desc = (String) modeloTabla.getValueAt(fila, 1);

        int confirm = JOptionPane.showConfirmDialog(this, 
            "¿Estás seguro de ELIMINAR definitivamente:\n" + codigo + " - " + desc + "?",
            "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            dao.eliminar(codigo);
            actualizarTabla();
            JOptionPane.showMessageDialog(this, "Item eliminado.");
        }
    }

    private JPanel crearFila(String lbl, JComponent cmp) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel l = new JLabel(lbl);
        l.setPreferredSize(new Dimension(100, 20));
        p.add(l);
        p.add(cmp);
        return p;
    }
}