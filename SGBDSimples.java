import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class Gerenc_Arc extends JFrame {

    public static class Tema {
        public static final Color FUNDO = new Color(40, 42, 54);
        public static final Color PAINEL = new Color(68, 71, 90);
        public static final Color SIDEBAR = new Color(33, 34, 44);
        public static final Color TEXTO = new Color(248, 248, 242);
        public static final Color DESTAQUE = new Color(189, 147, 249);
    }

    private String pastaRaiz = System.getProperty("user.home") + File.separator + "Documents" + File.separator
            + "Meus_Bancos";
    private File bancoAtual = null;
    private JComboBox<String> cbBancos = new JComboBox<>();
    private DefaultListModel<String> modTabelas = new DefaultListModel<>();
    private JList<String> listaTabelas = new JList<>(modTabelas);
    private DefaultTableModel modDados = new DefaultTableModel();
    private JTable tabelaDados = new JTable(modDados);
    private JLabel lblStatus = new JLabel(" Pronto. Selecione ou crie um banco de dados.");

    public Gerenc_Arc() {
        setTitle("NanoDB");
        setSize(950, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        new File(pastaRaiz).mkdirs();

        JSplitPane divisoria = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, montarEsquerda(), montarCentro());
        divisoria.setDividerLocation(220);
        divisoria.setDividerSize(3);
        divisoria.setBorder(null);

        lblStatus.setOpaque(true);
        lblStatus.setBackground(Tema.SIDEBAR);
        lblStatus.setForeground(Tema.DESTAQUE);
        lblStatus.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Tema.PAINEL));

        add(divisoria, BorderLayout.CENTER);
        add(lblStatus, BorderLayout.SOUTH);
    }

    private JPanel montarEsquerda() {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setPreferredSize(new Dimension(220, 0));
        painel.setBackground(Tema.SIDEBAR);
        painel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel pSeletor = new JPanel(new BorderLayout(0, 5));
        pSeletor.setOpaque(false);
        JLabel lblBancos = new JLabel("Bancos de Dados:");
        lblBancos.setForeground(Tema.DESTAQUE);
        cbBancos.setBackground(Tema.PAINEL);
        cbBancos.setForeground(Tema.TEXTO);
        cbBancos.addActionListener(e -> selecionarBanco());
        carregarBancos();

        JButton btnNovo = criarBotao("Novo Banco");
        btnNovo.addActionListener(e -> criarNovoBanco());
        pSeletor.add(lblBancos, BorderLayout.NORTH);
        pSeletor.add(cbBancos, BorderLayout.CENTER);
        pSeletor.add(btnNovo, BorderLayout.SOUTH);

        JPanel pTabelas = new JPanel(new BorderLayout(0, 5));
        pTabelas.setOpaque(false);
        pTabelas.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        JLabel lblTab = new JLabel("Tabelas:");
        lblTab.setForeground(Tema.DESTAQUE);

        listaTabelas.setBackground(Tema.PAINEL);
        listaTabelas.setForeground(Tema.TEXTO);
        listaTabelas.setSelectionBackground(Tema.DESTAQUE);
        listaTabelas.setSelectionForeground(Tema.FUNDO);
        listaTabelas.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && listaTabelas.getSelectedValue() != null)
                carregarDados(listaTabelas.getSelectedValue());
        });

        JScrollPane scroll = new JScrollPane(listaTabelas);
        scroll.setBorder(BorderFactory.createLineBorder(Tema.PAINEL, 1));
        pTabelas.add(lblTab, BorderLayout.NORTH);
        pTabelas.add(scroll, BorderLayout.CENTER);

        painel.add(pSeletor, BorderLayout.NORTH);
        painel.add(pTabelas, BorderLayout.CENTER);
        return painel;
    }

    private JPanel montarCentro() {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBackground(Tema.FUNDO);

        JToolBar toolBar = new JToolBar();
        toolBar.setBackground(Tema.PAINEL);
        toolBar.setFloatable(false);
        toolBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Tema.PAINEL));

        JButton btnNovaTab = criarBotao("📝 Adicionar Entidade");
        btnNovaTab.addActionListener(e -> criarNovaTabela());
        JButton btnInserir = criarBotao("➕ Inserir Registro");
        btnInserir.addActionListener(e -> inserirRegistro());
        JButton btnAtualizar = criarBotao("🔄 Atualizar Grade");
        btnAtualizar.addActionListener(e -> {
            if (listaTabelas.getSelectedValue() != null)
                carregarDados(listaTabelas.getSelectedValue());
        });

        toolBar.add(btnNovaTab);
        toolBar.addSeparator();
        toolBar.add(btnInserir);
        toolBar.addSeparator();
        toolBar.add(btnAtualizar);

        tabelaDados.setRowHeight(25);
        tabelaDados.getTableHeader().setReorderingAllowed(false);
        tabelaDados.setBackground(Tema.FUNDO);
        tabelaDados.setForeground(Tema.TEXTO);
        tabelaDados.setGridColor(Tema.PAINEL);
        tabelaDados.setSelectionBackground(Tema.DESTAQUE);
        tabelaDados.setSelectionForeground(Tema.FUNDO);
        tabelaDados.getTableHeader().setBackground(Tema.PAINEL);
        tabelaDados.getTableHeader().setForeground(Tema.TEXTO);

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        tabelaDados.setDefaultRenderer(Object.class, center);

        JScrollPane scroll = new JScrollPane(tabelaDados);
        scroll.getViewport().setBackground(Tema.FUNDO);
        scroll.setBorder(BorderFactory.createEmptyBorder());

        painel.add(toolBar, BorderLayout.NORTH);
        painel.add(scroll, BorderLayout.CENTER);
        return painel;
    }

    private JButton criarBotao(String txt) {
        JButton b = new JButton(txt);
        b.setForeground(Tema.TEXTO);
        b.setBackground(Tema.PAINEL);
        b.setFocusPainted(false);
        return b;
    }

    private void carregarBancos() {
        cbBancos.removeAllItems();
        File[] pastas = new File(pastaRaiz).listFiles(File::isDirectory);
        if (pastas != null)
            for (File p : pastas)
                cbBancos.addItem(p.getName());
    }

    private void selecionarBanco() {
        String sel = (String) cbBancos.getSelectedItem();
        if (sel != null) {
            bancoAtual = new File(pastaRaiz, sel);
            lblStatus.setText(" Banco selecionado: " + sel);
            carregarTabelas();
        }
    }

    private void carregarTabelas() {
        modTabelas.clear();
        modDados.setRowCount(0);
        modDados.setColumnCount(0);
        if (bancoAtual != null && bancoAtual.exists()) {
            File[] arqs = bancoAtual.listFiles((d, n) -> n.endsWith(".txt"));
            if (arqs != null)
                for (File f : arqs)
                    modTabelas.addElement(f.getName());
        }
    }

    private void criarNovoBanco() {
        String nome = JOptionPane.showInputDialog(this, "Nome do novo banco:");
        if (nome != null && !nome.trim().isEmpty() && new File(pastaRaiz, nome).mkdir()) {
            carregarBancos();
            cbBancos.setSelectedItem(nome);
        }
    }

    private void criarNovaTabela() {
        if (bancoAtual == null)
            return;
        String nome = JOptionPane.showInputDialog(this, "Nome da tabela:");
        if (nome == null || nome.trim().isEmpty())
            return;
        String cols = JOptionPane.showInputDialog(this, "Colunas (ex: nome;funcao):");
        if (cols == null)
            return;

        String estr = cols.trim().isEmpty() ? "id" : (cols.toLowerCase().startsWith("id;") ? cols : "id;" + cols);
        try {
            Files.writeString(new File(bancoAtual, nome + ".txt").toPath(), estr + "\n");
            lblStatus.setText(" Tabela " + nome + " criada.");
            carregarTabelas();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage());
        }
    }

    private void carregarDados(String arquivo) {
        modDados.setRowCount(0);
        modDados.setColumnCount(0);
        try {
            List<String> linhas = Files.readAllLines(new File(bancoAtual, arquivo).toPath());
            if (linhas.isEmpty())
                return;
            for (String col : linhas.get(0).split(";"))
                modDados.addColumn(col);
            for (int i = 1; i < linhas.size(); i++) {
                if (!linhas.get(i).trim().isEmpty())
                    modDados.addRow(linhas.get(i).split(";", -1));
            }
            lblStatus.setText(" " + (linhas.size() - 1) + " registro(s) carregado(s).");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage());
        }
    }

    private void inserirRegistro() {
        String tab = listaTabelas.getSelectedValue();
        int qtdCol = modDados.getColumnCount();
        if (bancoAtual == null || tab == null || qtdCol == 0)
            return;

        JPanel pForm = new JPanel(new GridLayout(qtdCol, 2, 5, 5));
        pForm.setBackground(Tema.PAINEL);
        JTextField[] campos = new JTextField[qtdCol];

        for (int i = 0; i < qtdCol; i++) {
            String nomeCol = modDados.getColumnName(i);
            JLabel lbl = new JLabel(nomeCol + ":");
            lbl.setForeground(Tema.TEXTO);
            pForm.add(lbl);

            campos[i] = new JTextField();
            campos[i].setForeground(Tema.TEXTO);
            campos[i].setCaretColor(Tema.TEXTO);

            if (i == 0 && nomeCol.equalsIgnoreCase("id")) {
                campos[i].setText(String.valueOf(modDados.getRowCount() + 1));
                campos[i].setBackground(Tema.SIDEBAR);
                campos[i].setEditable(false);
            } else {
                campos[i].setBackground(Tema.FUNDO);
                campos[i].addActionListener(e -> ((JTextField) e.getSource()).transferFocus());
            }
            pForm.add(campos[i]);
        }

        if (JOptionPane.showConfirmDialog(this, pForm, "Novo Registro", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION) {
            Vector<String> linha = new Vector<>();
            for (JTextField c : campos)
                linha.add(c.getText().trim());
            try {
                Files.writeString(new File(bancoAtual, tab).toPath(), String.join(";", linha) + "\n",
                        StandardOpenOption.APPEND);
                modDados.addRow(linha);
                lblStatus.setText(" Registro inserido.");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Gerenc_Arc().setVisible(true));
    }
}
