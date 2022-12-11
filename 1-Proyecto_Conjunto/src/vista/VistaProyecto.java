package vista;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import controlador.ControladorProyecto;

import javax.swing.JLabel;
import java.awt.Font;
import java.awt.Image;

import javax.swing.JRadioButton;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.SwingConstants;
import java.awt.Color;
import javax.swing.JScrollBar;
import javax.swing.border.LineBorder;
import javax.swing.JButton;
import javax.swing.JScrollPane;

public class VistaProyecto extends JFrame {

	private JPanel contentPane;
	public JComboBox cBFiltros;
	public JRadioButton rdbtnPorTipo, rdbtnPorProvincia;
	public JButton btnIniciar;
	public JPanel panelInformacionParques, panelFotoParques, panelParques, panelInicio, panelPortada;
	public JLabel lblCategoriaMostrar, lblNombreMostrar, lblSDTMostrar, lblFechaDeclaracionMostrar, lblProvinciaMostrar, lblFotoParque, lblFotoPortada;
	public JList listaParques;
	private JLabel lblTituloPortada;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					VistaProyecto frame = new VistaProyecto();
					ControladorProyecto controlador = new ControladorProyecto(frame);
					frame.setLocationRelativeTo(null);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public VistaProyecto() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1132, 810);
		contentPane = new JPanel();
		contentPane.setBackground(new Color(60, 179, 113));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		panelInicio = new JPanel();
		panelInicio.setBackground(new Color(189, 183, 107));
		panelInicio.setBounds(0, 0, 1118, 773);
		contentPane.add(panelInicio);
		panelInicio.setVisible(false);
		panelInicio.setLayout(null);
		
		panelInformacionParques = new JPanel();
		panelInformacionParques.setBorder(new LineBorder(new Color(0, 0, 0), 3));
		panelInformacionParques.setBounds(10, 10, 404, 469);
		panelInicio.add(panelInformacionParques);
		panelInformacionParques.setBackground(new Color(240, 248, 255));
		panelInformacionParques.setLayout(null);
		
		JLabel lblCategoria = new JLabel("Categoria:");
		lblCategoria.setForeground(new Color(255, 255, 255));
		lblCategoria.setBackground(new Color(95, 158, 160));
		lblCategoria.setFont(new Font("Segoe UI Variable", Font.BOLD, 20));
		lblCategoria.setBounds(10, 99, 384, 27);
		lblCategoria.setOpaque(true);
		panelInformacionParques.add(lblCategoria);
		
		lblCategoriaMostrar = new JLabel("_______________________________________________");
		lblCategoriaMostrar.setFont(new Font("Segoe UI Variable", Font.PLAIN, 20));
		lblCategoriaMostrar.setBounds(10, 136, 384, 27);
		panelInformacionParques.add(lblCategoriaMostrar);
		
		lblNombreMostrar = new JLabel("_______________________________________________");
		lblNombreMostrar.setFont(new Font("Segoe UI Variable", Font.PLAIN, 20));
		lblNombreMostrar.setBounds(10, 210, 384, 27);
		panelInformacionParques.add(lblNombreMostrar);
		
		JLabel lblNombre = new JLabel("Nombre:");
		lblNombre.setForeground(new Color(255, 255, 255));
		lblNombre.setBackground(new Color(95, 158, 160));
		lblNombre.setFont(new Font("Segoe UI Variable", Font.BOLD, 20));
		lblNombre.setBounds(10, 173, 384, 27);
		lblNombre.setOpaque(true);
		panelInformacionParques.add(lblNombre);
		
		lblSDTMostrar = new JLabel("_______________________________________________");
		lblSDTMostrar.setFont(new Font("Segoe UI Variable", Font.PLAIN, 20));
		lblSDTMostrar.setBounds(10, 284, 384, 27);
		panelInformacionParques.add(lblSDTMostrar);
		
		JLabel lblSDT = new JLabel("Superficie Declarada Total:");
		lblSDT.setForeground(new Color(255, 255, 255));
		lblSDT.setBackground(new Color(95, 158, 160));
		lblSDT.setFont(new Font("Segoe UI Variable", Font.BOLD, 20));
		lblSDT.setBounds(10, 247, 384, 27);
		lblSDT.setOpaque(true);
		panelInformacionParques.add(lblSDT);
		
		lblFechaDeclaracionMostrar = new JLabel("_______________________________________________");
		lblFechaDeclaracionMostrar.setFont(new Font("Segoe UI Variable", Font.PLAIN, 20));
		lblFechaDeclaracionMostrar.setBounds(10, 358, 384, 27);
		panelInformacionParques.add(lblFechaDeclaracionMostrar);
		
		JLabel lblFechaDeclaracion = new JLabel("Fecha Declaracion:");
		lblFechaDeclaracion.setForeground(new Color(255, 255, 255));
		lblFechaDeclaracion.setBackground(new Color(95, 158, 160));
		lblFechaDeclaracion.setFont(new Font("Segoe UI Variable", Font.BOLD, 20));
		lblFechaDeclaracion.setBounds(10, 321, 384, 27);
		lblFechaDeclaracion.setOpaque(true);
		panelInformacionParques.add(lblFechaDeclaracion);
		
		lblProvinciaMostrar = new JLabel("_______________________________________________");
		lblProvinciaMostrar.setFont(new Font("Segoe UI Variable", Font.PLAIN, 20));
		lblProvinciaMostrar.setBounds(10, 432, 384, 27);
		panelInformacionParques.add(lblProvinciaMostrar);
		
		JLabel lblProvincia = new JLabel("Provincia:");
		lblProvincia.setForeground(new Color(255, 255, 255));
		lblProvincia.setBackground(new Color(95, 158, 160));
		lblProvincia.setFont(new Font("Segoe UI Variable", Font.BOLD, 20));
		lblProvincia.setBounds(10, 395, 384, 27);
		lblProvincia.setOpaque(true);
		panelInformacionParques.add(lblProvincia);
		
		JLabel lblLogo = new JLabel("");
		lblLogo.setBackground(Color.WHITE);
		lblLogo.setFont(new Font("Tahoma", Font.PLAIN, 30));
		lblLogo.setHorizontalAlignment(SwingConstants.CENTER);
		lblLogo.setBounds(10, 10, 384, 79);
		lblLogo.setOpaque(true);
		ImageIcon img2 = new ImageIcon(getClass().getResource("/resources/logo.png"));
		ImageIcon ico2 = new ImageIcon(img2.getImage().getScaledInstance(lblLogo.getWidth(), lblLogo.getHeight(), Image.SCALE_SMOOTH));
		lblLogo.setIcon(ico2);
		lblLogo.setBorder(new LineBorder(new Color(0, 0, 0), 3));
		panelInformacionParques.add(lblLogo);
		
		panelFotoParques = new JPanel();
		panelFotoParques.setBounds(479, 10, 629, 449);
		panelInicio.add(panelFotoParques);
		panelFotoParques.setBackground(new Color(240, 248, 255));
		panelFotoParques.setLayout(null);
		
		lblFotoParque = new JLabel("");
		lblFotoParque.setBounds(0, 0, 629, 449);
		ImageIcon img = new ImageIcon(getClass().getResource("/resources/Castilla.png"));
		ImageIcon ico = new ImageIcon(img.getImage().getScaledInstance(lblFotoParque.getWidth(), lblFotoParque.getHeight(), Image.SCALE_SMOOTH));
		lblFotoParque.setIcon(ico);
		lblFotoParque.setBorder(new LineBorder(new Color(0, 0, 0), 4));
		panelFotoParques.add(lblFotoParque);
		
		panelParques = new JPanel();
		panelParques.setBounds(10, 537, 1098, 224);
		panelInicio.add(panelParques);
		panelParques.setBackground(new Color(0, 0, 128));
		panelParques.setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 10, 1078, 204);
		panelParques.add(scrollPane);
		
		listaParques = new JList();
		scrollPane.setViewportView(listaParques);
		
		JLabel lblFiltro = new JLabel("Filtro:");
		lblFiltro.setBounds(10, 481, 69, 46);
		panelInicio.add(lblFiltro);
		lblFiltro.setFont(new Font("Tahoma", Font.BOLD, 20));
		
		rdbtnPorProvincia = new JRadioButton("Por Provincia");
		rdbtnPorProvincia.setBounds(115, 480, 129, 50);
		panelInicio.add(rdbtnPorProvincia);
		rdbtnPorProvincia.setBackground(new Color(189, 183, 107));
		rdbtnPorProvincia.setFont(new Font("Tahoma", Font.PLAIN, 17));
		
		rdbtnPorTipo = new JRadioButton("Por Tipo");
		rdbtnPorTipo.setBounds(287, 481, 129, 50);
		panelInicio.add(rdbtnPorTipo);
		rdbtnPorTipo.setBackground(new Color(189, 183, 107));
		rdbtnPorTipo.setFont(new Font("Tahoma", Font.PLAIN, 17));
		
		cBFiltros = new JComboBox();
		cBFiltros.setBounds(861, 482, 247, 29);
		cBFiltros.setBorder(new LineBorder(new Color(0, 0, 0), 3));
		panelInicio.add(cBFiltros);
		
		panelPortada = new JPanel();
		panelPortada.setBounds(0, 0, 1118, 773);
		contentPane.add(panelPortada);
		panelPortada.setLayout(null);
		
		lblFotoPortada = new JLabel("");
		lblFotoPortada.setBounds(0, 0, 1118, 773);
		ImageIcon img3 = new ImageIcon(getClass().getResource("/resources/parque.jpg"));
		ImageIcon ico3 = new ImageIcon(img3.getImage().getScaledInstance(lblFotoPortada.getWidth(), lblFotoPortada.getHeight(), Image.SCALE_SMOOTH));
		
		btnIniciar = new JButton("Ver los Parques");
		btnIniciar.setBackground(new Color(143, 188, 143));
		btnIniciar.setFont(new Font("Nirmala UI", Font.BOLD, 20));
		btnIniciar.setBounds(440, 666, 226, 45);
		panelPortada.add(btnIniciar);
		
		lblTituloPortada = new JLabel("Parques Nacionales de Castilla La-Mancha");
		lblTituloPortada.setOpaque(true);
		lblTituloPortada.setHorizontalAlignment(SwingConstants.CENTER);
		lblTituloPortada.setFont(new Font("Segoe UI Variable", Font.BOLD, 40));
		lblTituloPortada.setBounds(10, 42, 1098, 88);
		panelPortada.add(lblTituloPortada);
		lblFotoPortada.setIcon(ico3);
		panelPortada.add(lblFotoPortada);
		
		lblTituloPortada.setBackground(new Color(143, 188, 143));
	}
}
