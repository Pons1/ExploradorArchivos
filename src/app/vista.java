package app;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextArea;
import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JFileChooser;

import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Date;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.awt.event.ActionEvent;
import javax.swing.JCheckBox;
import org.apache.pdfbox.*;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

/**
 * Vista principal de l'aplicació de gestió d'arxius, desenvolupada per Adrián
 * Pons Choví. Aquesta vista permet cercar i reemplaçar textos dins d'arxius de
 * text i PDFs d'una carpeta.
 */
public class vista extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private final JLabel lblRuta = new JLabel("Ruta:");
	private JTextField txtFRuta;
	private JTextArea txtResultado;
	private JScrollPane scrollPane;
	private JTextField textFBuscar;
	private JButton btnBorrarResultado;
	private JCheckBox chBoxAccents;
	private JCheckBox chBoxMajus;
	private JTextField txtFRemplaçar;
	private JButton btnRemplaçar;

	/**
	 * Metode principal que inicia l'aplicació.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					vista frame = new vista();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Constructor de la classe vista. Defineix la interfície gràfica i les funcions
	 * d'usuari.
	 */
	public vista() {

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1076, 673);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		lblRuta.setFont(new Font("Tahoma", Font.PLAIN, 24));
		lblRuta.setBounds(10, 47, 126, 36);
		contentPane.add(lblRuta);

		txtFRuta = new JTextField();
		txtFRuta.setFont(new Font("Tahoma", Font.PLAIN, 20));
		txtFRuta.setBounds(81, 47, 383, 36);
		contentPane.add(txtFRuta);
		txtFRuta.setColumns(10);

		JButton btnCargar = new JButton("Cargar");
		btnCargar.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				String ruta = txtFRuta.getText();
				File directori = new File(ruta);
				String resultat = "";

				if (directori.isDirectory()) {
					resultat = llistarArchiusRecursius(directori, "");
				} else {
					JOptionPane.showMessageDialog(null, "Seleccione un directorio", "Alerta",
							JOptionPane.INFORMATION_MESSAGE);
				}
				txtResultado.setText(resultat);
			}
		});

		btnCargar.setFont(new Font("Tahoma", Font.PLAIN, 24));
		btnCargar.setBounds(495, 47, 126, 36);
		contentPane.add(btnCargar);

		JLabel lblTítulo = new JLabel("Gestor d´archius. Adrián Pons Choví");
		lblTítulo.setFont(new Font("Tahoma", Font.PLAIN, 24));
		lblTítulo.setBounds(529, 0, 400, 36);
		contentPane.add(lblTítulo);

		scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 246, 907, 323);
		contentPane.add(scrollPane);

		txtResultado = new JTextArea();
		scrollPane.setViewportView(txtResultado);
		txtResultado.setBackground(new Color(192, 192, 192));

		JLabel lblBuscador = new JLabel("Buscador:");
		lblBuscador.setFont(new Font("Tahoma", Font.PLAIN, 24));
		lblBuscador.setBounds(10, 116, 126, 36);
		contentPane.add(lblBuscador);

		textFBuscar = new JTextField();
		textFBuscar.setFont(new Font("Tahoma", Font.PLAIN, 20));
		textFBuscar.setColumns(10);
		textFBuscar.setBounds(128, 116, 336, 36);
		contentPane.add(textFBuscar);

		JButton btnBuscar = new JButton("Buscar");
		btnBuscar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!textFBuscar.getText().equals("")) {
					txtFRemplaçar.setEnabled(true);
					btnRemplaçar.setEnabled(true);
				} else {
					JOptionPane.showMessageDialog(null, "Fica una cadena", "Error", JOptionPane.ERROR_MESSAGE);
				}
				String busqueda = textFBuscar.getText();
				String ruta = txtFRuta.getText();
				File directori = new File(ruta);
				String resultat = "";

				if (directori.isDirectory()) {
					if (!busqueda.equals("")) {

						resultat = llistarArchiusRecursiusYBuscar(directori, "", busqueda);
					} else {
						JOptionPane.showMessageDialog(null, "Inserte Busqueda", "Alerta",
								JOptionPane.INFORMATION_MESSAGE);
					}
				} else {
					JOptionPane.showMessageDialog(null, "Seleccione un directorio", "Alerta",
							JOptionPane.INFORMATION_MESSAGE);
				}
				txtResultado.setText(resultat);
			}
		});

		btnBuscar.setFont(new Font("Tahoma", Font.PLAIN, 24));
		btnBuscar.setBounds(495, 116, 126, 36);
		contentPane.add(btnBuscar);

		btnBorrarResultado = new JButton("Borrar");
		btnBorrarResultado.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				txtFRuta.setText("");
				txtResultado.setText("");
				textFBuscar.setText("");
				chBoxMajus.setSelected(true);
				chBoxAccents.setSelected(true);
			}
		});
		btnBorrarResultado.setFont(new Font("Tahoma", Font.PLAIN, 24));
		btnBorrarResultado.setBounds(653, 47, 126, 36);
		contentPane.add(btnBorrarResultado);

		chBoxMajus = new JCheckBox("Respectar Majuscules");
		chBoxMajus.setFont(new Font("Tahoma", Font.PLAIN, 20));
		chBoxMajus.setBounds(653, 108, 231, 21);
		contentPane.add(chBoxMajus);

		chBoxAccents = new JCheckBox("Respectar Accents");
		chBoxAccents.setFont(new Font("Tahoma", Font.PLAIN, 20));
		chBoxAccents.setBounds(653, 142, 231, 21);
		contentPane.add(chBoxAccents);
		chBoxMajus.setSelected(true);
		chBoxAccents.setSelected(true);

		JLabel lblRemplaçar = new JLabel("Remplaçar:");
		lblRemplaçar.setFont(new Font("Tahoma", Font.PLAIN, 24));
		lblRemplaçar.setBounds(10, 177, 126, 36);
		contentPane.add(lblRemplaçar);

		txtFRemplaçar = new JTextField();
		txtFRemplaçar.setEnabled(false);
		txtFRemplaçar.setFont(new Font("Tahoma", Font.PLAIN, 20));
		txtFRemplaçar.setColumns(10);
		txtFRemplaçar.setBounds(142, 177, 336, 36);
		contentPane.add(txtFRemplaçar);

		btnRemplaçar = new JButton("Remplaçar");
		btnRemplaçar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!textFBuscar.getText().equals("")) {
					txtFRemplaçar.setEnabled(true);
					btnRemplaçar.setEnabled(true);
				} else {
					JOptionPane.showMessageDialog(null, "Fica una cadena", "Error", JOptionPane.ERROR_MESSAGE);
				}
				String busqueda = textFBuscar.getText();
				String ruta = txtFRuta.getText();
				File directori = new File(ruta);
				String resultat = "";

				if (directori.isDirectory()) {
					if (!busqueda.equals("")) {

						resultat = llistarArchiusRecursiusYReemplazar(directori, "", busqueda);
					} else {
						JOptionPane.showMessageDialog(null, "Inserte Busqueda", "Alerta",
								JOptionPane.INFORMATION_MESSAGE);
					}
				} else {
					JOptionPane.showMessageDialog(null, "Seleccione un directorio", "Alerta",
							JOptionPane.INFORMATION_MESSAGE);
				}
				txtResultado.setText(resultat);
			}
		});
		btnRemplaçar.setEnabled(false);
		btnRemplaçar.setFont(new Font("Tahoma", Font.PLAIN, 24));
		btnRemplaçar.setBounds(495, 177, 162, 36);
		contentPane.add(btnRemplaçar);

	}

	/**
	 * Llista els arxius d'un directori de manera recursiva.
	 * 
	 * @param directori El directori a llistar.
	 * @param indent    Identació per a visualitzar correctament els subdirectoris.
	 * @return La llista d'arxius en format text.
	 */
	private String llistarArchiusRecursius(File directori, String indent) {
		File[] archius = directori.listFiles();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		String resultado = "";

		if (archius != null) {
			for (File file : archius) {
				if (file.isDirectory()) {
					if (file.list().length == 0) {

					} else {
						resultado += indent + "|-- \\" + file.getName() + "\n";
						resultado += llistarArchiusRecursius(file, indent + "|   ");
					}

				} else {
					String tamaño = String.format("%.1f KB", file.length() / 1024.0);
					Date fechaMod = new Date(file.lastModified());
					String fecha = sdf.format(fechaMod);
					resultado += indent + "|-- " + file.getName() + " (" + tamaño + " – " + fecha + ")\n";
				}
			}
		}

		return resultado;
	}

	/**
	 * Llista i busca una cadena de text dins dels arxius d'un directori de manera
	 * recursiva.
	 * 
	 * @param directori El directori a llistar.
	 * @param indent    Identació per a visualitzar correctament els subdirectoris.
	 * @param busqueda  La cadena de text a cercar.
	 * @return El resultat de la cerca en format text.
	 */
	private String llistarArchiusRecursiusYBuscar(File directori, String indent, String busqueda) {
		File[] archius = directori.listFiles();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		String resultado = "";
		int coincidencies = 0;

		if (archius != null) {

			for (File file : archius) {
				coincidencies = 0;
				if (file.isDirectory()) {
					if (!(file.list().length == 0)) {

						resultado += indent + "|-- \\" + file.getName() + "\n";
						resultado += llistarArchiusRecursiusYBuscar(file, indent + "|   ", busqueda);

					}
				} else {
					try (BufferedReader br = new BufferedReader(new FileReader(file))) {
						String linea;
						switch (comprobarChBox()) {
						case 1: {

							if (file.getName().endsWith(".txt")) {
								coincidencies = 0;
								while ((linea = br.readLine()) != null) {
									coincidencies += contarCoincidenciasEnLinea(linea, busqueda);

								}
							} else if (file.getName().endsWith(".pdf")) {
								coincidencies = 0;
								PDDocument pdDocument = null;
								try {
									pdDocument = Loader.loadPDF(file);
									PDFTextStripper pdfStripper = new PDFTextStripper();

									String text = pdfStripper.getText(pdDocument);

									coincidencies += contarCoincidenciasEnLinea(text, busqueda);

								} catch (IOException e) {
									e.printStackTrace();
								}

							}

							break;
						}
						case 2: {
							if (file.getName().endsWith(".txt")) {
								while ((linea = br.readLine()) != null) {
									coincidencies += contarCoincidenciasEnLinea(linea.toLowerCase(),
											busqueda.toLowerCase());
								}
							} else if (file.getName().endsWith(".pdf")) {
								coincidencies = 0;
								PDDocument pdDocument = null;
								try {
									pdDocument = Loader.loadPDF(file);
									PDFTextStripper pdfStripper = new PDFTextStripper();

									String text = pdfStripper.getText(pdDocument);

									coincidencies += contarCoincidenciasEnLinea(text.toLowerCase(),
											busqueda.toLowerCase());

								} catch (IOException e) {
									e.printStackTrace();
								}
							}
							break;
						}

						case 3: {
							if (file.getName().endsWith(".txt")) {
								while ((linea = br.readLine()) != null) {
									coincidencies += contarCoincidenciasEnLinea(senseAccents(linea),
											senseAccents(busqueda));
								}
							} else if (file.getName().endsWith(".pdf")) {
								coincidencies = 0;
								PDDocument pdDocument = null;
								try {
									pdDocument = Loader.loadPDF(file);
									PDFTextStripper pdfStripper = new PDFTextStripper();

									String text = pdfStripper.getText(pdDocument);

									coincidencies += contarCoincidenciasEnLinea(senseAccents(text),
											senseAccents(busqueda));

								} catch (IOException e) {
									e.printStackTrace();
								}
							}
							break;
						}
						case 4: {
							if (file.getName().endsWith(".txt")) {
								while ((linea = br.readLine()) != null) {
									coincidencies += contarCoincidenciasEnLinea(senseAccents(linea.toLowerCase()),
											senseAccents(busqueda.toLowerCase()));
								}
							} else if (file.getName().endsWith(".pdf")) {
								coincidencies = 0;
								PDDocument pdDocument = null;
								try {
									pdDocument = Loader.loadPDF(file);
									PDFTextStripper pdfStripper = new PDFTextStripper();

									String text = pdfStripper.getText(pdDocument);

									coincidencies += contarCoincidenciasEnLinea(senseAccents(text.toLowerCase()),
											senseAccents(busqueda.toLowerCase()));

								} catch (IOException e) {
									e.printStackTrace();
								}
							}
							break;
						}
						default: {
							break;
						}

						}
						resultado += indent + "|-- " + file.getName() + " (" + "Coincidencias: " + coincidencies + ")"
								+ "\n";
					} catch (IOException e) {
						System.err.println("Error al leer el archivo: " + e.getMessage());
					}
				}
			}
		}
		return resultado;

	}

	/**
	 * Llista i reemplaça una cadena de text dins dels arxius d'un directori de
	 * manera recursiva.
	 * 
	 * @param directori El directori a llistar.
	 * @param indent    Identació per a visualitzar correctament els subdirectoris.
	 * @param busqueda  La cadena de text a reemplaçar.
	 * @return El resultat del reemplaçament en format text.
	 */
	private String llistarArchiusRecursiusYReemplazar(File directori, String indent, String busqueda) {
		File[] archius = directori.listFiles();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		String resultado = "";

		if (archius != null) {
			for (File file : archius) {
				if (file.isDirectory()) {
					if (file.list().length == 0) {

					} else {

						resultado += indent + "|-- \\" + file.getName() + "\n";
						resultado += llistarArchiusRecursiusYReemplazar(file, indent + "|   ", busqueda);

					}
				} else {

					int coincidencies = 0;
					try (BufferedReader br = new BufferedReader(new FileReader(file))) {
						String linea;
						switch (comprobarChBox()) {
						case 1: {
							if (file.getName().endsWith(".txt")) {
								while ((linea = br.readLine()) != null) {
									coincidencies += contarCoincidenciasEnLinea(linea, busqueda);
								}
								if (coincidencies >= 1) {
									remplazarPalabra(file, busqueda, txtFRemplaçar.getText());
								}

							}

						}
						case 2: {
							if (file.getName().endsWith(".txt")) {
								while ((linea = br.readLine()) != null) {
									coincidencies += contarCoincidenciasEnLinea(linea.toLowerCase(),
											busqueda.toLowerCase());
								}
								if (coincidencies >= 1) {
									remplazarPalabra(file, busqueda, txtFRemplaçar.getText());
								}
							}

						}
						case 3: {
							if (file.getName().endsWith(".txt")) {
								while ((linea = br.readLine()) != null) {
									coincidencies += contarCoincidenciasEnLinea(senseAccents(linea),
											senseAccents(busqueda));
								}
								if (coincidencies >= 1) {
									remplazarPalabra(file, busqueda, txtFRemplaçar.getText());
								}
							}

						}
						case 4: {
							if (file.getName().endsWith(".txt")) {
								while ((linea = br.readLine()) != null) {
									coincidencies += contarCoincidenciasEnLinea(senseAccents(linea.toLowerCase()),
											senseAccents(busqueda.toLowerCase()));
								}
								if (coincidencies >= 1) {
									remplazarPalabra(file, busqueda, txtFRemplaçar.getText());
								}
							}

						}
						default: {
						}
							;

							resultado += indent + "|-- " + file.getName() + " (" + "Reemplaços: " + coincidencies + ")"
									+ "\n";

						}
					} catch (IOException e) {
						System.err.println("Error al leer el archivo: " + e.getMessage());
					}
				}
			}
		}
		return resultado;
	}

	/**
	 * Conta el numero de voltes que apareix una cadena de text en un text.
	 * 
	 * @param linea    La linea de text on se vol buscar la cadena de text.
	 * @param busqueda La cadena de text a reemplaçar.
	 * @return El numero de coincidencies.
	 */
	private int contarCoincidenciasEnLinea(String linea, String busqueda) {
		int count = 0;
		int index = 0;
		while ((index = linea.indexOf(busqueda, index)) != -1) {
			count++;
			index += busqueda.length();
		}
		return count;
	}

	/**
	 * Agafa un text i reemplaça la cadena seleccionada per altra y guarda el text
	 * modificat en un archiu nou.
	 * 
	 * @param file     El archiu en el que se desitja reemplaçar la cadena de text.
	 * @param palabra  La cadena de text a reemplaçar.
	 * @param remplazo La cadena de text per la que es va a remplaçar.
	 */
	private void remplazarPalabra(File file, String palabra, String remplazo) {
		FileReader fr;
		try {

			fr = new FileReader(file);
			BufferedReader bf = new BufferedReader(fr);

			String linea;
			String lineaMOD;
			StringBuilder texto = new StringBuilder();

			File fileMod = new File(file.getParent() + File.separator + "MOD_" + file.getName());

			while ((linea = bf.readLine()) != null) {
				lineaMOD = linea.replace(palabra, remplazo);
				texto.append(lineaMOD).append("\n");
			}

			FileWriter fw = new FileWriter(fileMod);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(texto.toString());

			bw.close();
			bf.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Comprova si els checkBoxs de la busqueda estàn seleccionats.
	 * 
	 * @return Un numero que indica el estats dels checkBoxs.
	 */
	private int comprobarChBox() {
		if (chBoxMajus.isSelected() && chBoxAccents.isSelected()) {
			return 1;
		} else if (!chBoxMajus.isSelected() && chBoxAccents.isSelected()) {
			return 2;
		} else if (chBoxMajus.isSelected() && !chBoxAccents.isSelected()) {
			return 3;
		} else {
			return 4;
		}

	}

	/**
	 * LLeva els accents del text passat per paràmetre.
	 * 
	 * @param str Text a llevar els accents.
	 * @return El text sense accents.
	 */
	public static String senseAccents(String str) {
		String normalized = Normalizer.normalize(str, Normalizer.Form.NFD);
		return normalized.replaceAll("\\p{M}", "");
	}

}
