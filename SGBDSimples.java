import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
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
    private String pastaRaiz = System.getProperty("user.home") + java.io.File.separator + "Documents"
            + java.io.File.separator + "Meus_Bancos";
    private File bancoAtual = null;

    // Componentes da Interface
    private JComboBox<String> cbBancos;
    private DefaultListModel<String> modeloTabelas;
    private JList<String> listaTabelas;
    private JTable tabelaDados;
    private DefaultTableModel modeloTabelaDados;
    private JLabel lblStatus;

    public Gerenc_Arc() {
        setTitle("SGBD Java - MySQL Style (Auto-ID)");
        setSize(950, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- PALETA DE CORES ---
        Color fundoEscuro = new Color(18, 18, 18);
        Color fundoMedio = new Color(30, 30, 30);
        Color laranjaGamer = new Color(255, 130, 0);
        Color textoClaro = new Color(230, 230, 230);

        // Cria a pasta raiz se não existir
        new File(pastaRaiz).mkdirs();

        // ==========================================
        // 1. PAINEL ESQUERDO (Navegador de Bancos)
        // ==========================================
        JPanel painelEsquerdo = new JPanel(new BorderLayout());
        painelEsquerdo.setPreferredSize(new Dimension(220, 0));
        painelEsquerdo.setBackground(fundoEscuro);
        painelEsquerdo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel painelSeletor = new JPanel(new BorderLayout(0, 5));
        painelSeletor.setOpaque(false);
        JLabel lblBancos = new JLabel("Bancos de Dados:");
        lblBancos.setForeground(laranjaGamer);

        cbBancos = new JComboBox<>();
        cbBancos.setBackground(fundoMedio);
        cbBancos.setForeground(textoClaro);
        carregarBancos();
        cbBancos.addActionListener(e -> selecionarBanco());

        JButton btnNovoBanco = new JButton("Novo Banco");
        btnNovoBanco.setBackground(laranjaGamer);
        btnNovoBanco.setForeground(Color.BLACK);
        btnNovoBanco.setFocusPainted(false);
        btnNovoBanco.addActionListener(e -> criarNovoBanco());

        painelSeletor.add(lblBancos, BorderLayout.NORTH);
        painelSeletor.add(cbBancos, BorderLayout.CENTER);
        painelSeletor.add(btnNovoBanco, BorderLayout.SOUTH);

        JPanel painelTabelas = new JPanel(new BorderLayout(0, 5));
        painelTabelas.setOpaque(false);
        painelTabelas.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        JLabel lblTabelas = new JLabel("Tabelas:");
        lblTabelas.setForeground(laranjaGamer);

        modeloTabelas = new DefaultListModel<>();
        listaTabelas = new JList<>(modeloTabelas);
        listaTabelas.setBackground(fundoMedio);
        listaTabelas.setForeground(textoClaro);
        listaTabelas.setSelectionBackground(laranjaGamer);
        listaTabelas.setSelectionForeground(Color.BLACK);
        listaTabelas.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && listaTabelas.getSelectedValue() != null) {
                carregarDadosDaTabela(listaTabelas.getSelectedValue());
            }
        });

        JScrollPane scrollTabelas = new JScrollPane(listaTabelas);
        scrollTabelas.setBorder(BorderFactory.createLineBorder(laranjaGamer, 1));

        painelTabelas.add(lblTabelas, BorderLayout.NORTH);
        painelTabelas.add(scrollTabelas, BorderLayout.CENTER);

        painelEsquerdo.add(painelSeletor, BorderLayout.NORTH);
        painelEsquerdo.add(painelTabelas, BorderLayout.CENTER);

        // ==========================================
        // 2. PAINEL CENTRAL (Grade de Dados)
        // ==========================================
        JPanel painelCentral = new JPanel(new BorderLayout());
        painelCentral.setBackground(fundoEscuro);

        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.setBackground(fundoMedio);
        toolBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, laranjaGamer));

        JButton btnNovaTabela = criarBotaoToolBar("📝 Nova Tabela", laranjaGamer);
        JButton btnInserir = criarBotaoToolBar("➕ Inserir Registro", laranjaGamer);
        JButton btnAddColuna = criarBotaoToolBar("🛠️ Adicionar Coluna", laranjaGamer);
        JButton btnAtualizar = criarBotaoToolBar("🔄 Atualizar Grade", laranjaGamer);

        btnNovaTabela.addActionListener(e -> criarNovaTabela());
        btnInserir.addActionListener(e -> inserirRegistroDinamicamente());
        btnAddColuna.addActionListener(e -> adicionarNovaColuna());
        btnAtualizar.addActionListener(e -> {
            if (listaTabelas.getSelectedValue() != null) {
                carregarDadosDaTabela(listaTabelas.getSelectedValue());
            }
        });

        toolBar.add(btnNovaTabela);
        toolBar.addSeparator();
        toolBar.add(btnInserir);
        toolBar.addSeparator();
        toolBar.add(btnAddColuna);
        toolBar.addSeparator();
        toolBar.add(btnAtualizar);

        modeloTabelaDados = new DefaultTableModel();
        tabelaDados = new JTable(modeloTabelaDados);
        tabelaDados.setRowHeight(25);
        tabelaDados.getTableHeader().setReorderingAllowed(false);

        // Estilizando a Tabela
        tabelaDados.setBackground(fundoMedio);
        tabelaDados.setForeground(textoClaro);
        tabelaDados.setGridColor(fundoEscuro);
        tabelaDados.setSelectionBackground(laranjaGamer);
        tabelaDados.setSelectionForeground(Color.BLACK);

        // Estilizando o Cabeçalho da Tabela
        tabelaDados.getTableHeader().setBackground(laranjaGamer);
        tabelaDados.getTableHeader().setForeground(Color.BLACK);
        tabelaDados.getTableHeader().setFont(tabelaDados.getFont().deriveFont(java.awt.Font.BOLD));
        DefaultTableCellRenderer centralizado = new DefaultTableCellRenderer();
        centralizado.setHorizontalAlignment(SwingConstants.CENTER);
        tabelaDados.setDefaultRenderer(Object.class, centralizado);

        JScrollPane scrollTabelaDados = new JScrollPane(tabelaDados);
        scrollTabelaDados.setBorder(BorderFactory.createEmptyBorder());
        scrollTabelaDados.getViewport().setBackground(fundoEscuro);

        painelCentral.add(toolBar, BorderLayout.NORTH);
        painelCentral.add(scrollTabelaDados, BorderLayout.CENTER);

        // ==========================================
        // 3. BARRA DE STATUS (Rodapé)
        // ==========================================
        lblStatus = new JLabel(" Pronto. Selecione ou crie um banco de dados.");
        lblStatus.setOpaque(true);
        lblStatus.setBackground(fundoEscuro);
        lblStatus.setForeground(laranjaGamer);
        lblStatus.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, laranjaGamer));

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, painelEsquerdo, painelCentral);
        splitPane.setDividerLocation(220);
        splitPane.setBorder(null);
        splitPane.setDividerSize(3);
        splitPane.setBackground(fundoEscuro);

        add(splitPane, BorderLayout.CENTER);
        add(lblStatus, BorderLayout.SOUTH);

        // Fundo geral do JFrame
        getContentPane().setBackground(fundoEscuro);
    }

    // Método auxiliar para criar os botões da ToolBar estilizados
    private JButton criarBotaoToolBar(String texto, Color corTexto) {
        JButton btn = new JButton(texto);
        btn.setForeground(corTexto);
        btn.setBackground(new Color(40, 40, 40));
        btn.setFocusPainted(false);
        return btn;
    }

    // --- MÉTODOS DE LÓGICA (Mantidos iguais ao original) ---

    private void carregarBancos() {
        cbBancos.removeAllItems();
        File raiz = new File(pastaRaiz);
        File[] pastas = raiz.listFiles(File::isDirectory);
        if (pastas != null) {
            for (File p : pastas) {
                cbBancos.addItem(p.getName());
            }
        }
    }

    private void selecionarBanco() {
        String selecionado = (String) cbBancos.getSelectedItem();
        if (selecionado != null) {
            bancoAtual = new File(pastaRaiz, selecionado);
            lblStatus.setText(" Banco selecionado: " + selecionado);
            carregarTabelas();
        }
    }

    private void carregarTabelas() {
        modeloTabelas.clear();
        modeloTabelaDados.setRowCount(0);
        modeloTabelaDados.setColumnCount(0);

        if (bancoAtual != null && bancoAtual.exists()) {
            File[] arquivos = bancoAtual.listFiles((d, nome) -> nome.endsWith(".txt"));
            if (arquivos != null) {
                for (File f : arquivos) {
                    modeloTabelas.addElement(f.getName());
                }
            }
        }
    }

    private void criarNovoBanco() {
        String nome = JOptionPane.showInputDialog(this, "Nome do novo banco de dados:");
        if (nome != null && !nome.trim().isEmpty()) {
            File novaPasta = new File(pastaRaiz, nome);
            if (novaPasta.mkdir()) {
                carregarBancos();
                cbBancos.setSelectedItem(nome);
            }
        }
    }

    private void criarNovaTabela() {
        if (bancoAtual == null) {
            JOptionPane.showMessageDialog(this, "Selecione um banco primeiro!");
            return;
        }

        String nome = JOptionPane.showInputDialog(this, "Nome da tabela:");
        if (nome == null || nome.trim().isEmpty())
            return;

        String colunas = JOptionPane.showInputDialog(this,
                "Defina as colunas da tabela (ex: nome;funcao):");

        if (colunas == null)
            return;

        String estruturaFinal;
        if (colunas.trim().isEmpty()) {
            estruturaFinal = "id";
        } else {
            if (colunas.toLowerCase().startsWith("id;")) {
                estruturaFinal = colunas;
            } else {
                estruturaFinal = "id;" + colunas;
            }
        }

        File arquivo = new File(bancoAtual, nome + ".txt");
        try (FileWriter fw = new FileWriter(arquivo)) {
            fw.write(estruturaFinal + "\n");
            lblStatus.setText(" Tabela " + nome + " criada com a estrutura: " + estruturaFinal);
            carregarTabelas();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage());
        }
    }

    private void carregarDadosDaTabela(String nomeArquivo) {
        File arquivo = new File(bancoAtual, nomeArquivo);
        modeloTabelaDados.setRowCount(0);
        modeloTabelaDados.setColumnCount(0);

        try (BufferedReader br = new BufferedReader(new FileReader(arquivo))) {
            String cabecalho = br.readLine();
            if (cabecalho != null) {
                String[] colunas = cabecalho.split(";");
                for (String col : colunas) {
                    modeloTabelaDados.addColumn(col);
                }

                String linha;
                int count = 0;
                while ((linha = br.readLine()) != null) {
                    if (!linha.trim().isEmpty()) {
                        String[] dados = linha.split(";", -1);
                        modeloTabelaDados.addRow(dados);
                        count++;
                    }
                }
                lblStatus.setText(" Tabela carregada: " + count + " registro(s) retornado(s).");
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Erro ao ler dados: " + e.getMessage());
        }
    }

    private void inserirRegistroDinamicamente() {
        String tabelaSelecionada = listaTabelas.getSelectedValue();
        if (bancoAtual == null || tabelaSelecionada == null) {
            JOptionPane.showMessageDialog(this, "Selecione uma tabela na lista à esquerda primeiro.");
            return;
        }

        int qtdColunas = modeloTabelaDados.getColumnCount();
        if (qtdColunas == 0)
            return;

        JPanel painelForm = new JPanel(new GridLayout(qtdColunas, 2, 5, 5));
        JTextField[] camposTexto = new JTextField[qtdColunas];

        for (int i = 0; i < qtdColunas; i++) {
            String nomeDaColuna = modeloTabelaDados.getColumnName(i);
            painelForm.add(new JLabel(nomeDaColuna + ":"));

            if (i == 0 && nomeDaColuna.equalsIgnoreCase("id")) {
                int proximoId = modeloTabelaDados.getRowCount() + 1;
                camposTexto[i] = new JTextField(String.valueOf(proximoId));
                camposTexto[i].setEditable(false);
                camposTexto[i].setBackground(new Color(220, 220, 220));
            } else {
                camposTexto[i] = new JTextField();

                // === ADICIONE ESTA LINHA PARA O ADVANCE FOCUS ===
                // Quando o Enter for pressionado, o foco é transferido para o próximo
                // componente
                camposTexto[i].addActionListener(e -> ((JTextField) e.getSource()).transferFocus());
            }
            painelForm.add(camposTexto[i]);
        }

        int resultado = JOptionPane.showConfirmDialog(this, painelForm, "Inserir Novo Registro",
                JOptionPane.OK_CANCEL_OPTION);

        if (resultado == JOptionPane.OK_OPTION) {
            StringBuilder novoRegistro = new StringBuilder();
            Vector<String> linhaParaGrid = new Vector<>();

            for (int i = 0; i < qtdColunas; i++) {
                String valor = camposTexto[i].getText().trim();
                novoRegistro.append(valor);
                linhaParaGrid.add(valor);

                if (i < qtdColunas - 1) {
                    novoRegistro.append(";");
                }
            }

            File arquivo = new File(bancoAtual, tabelaSelecionada);
            try (FileWriter fw = new FileWriter(arquivo, true)) {
                fw.write(novoRegistro.toString() + "\n");
                modeloTabelaDados.addRow(linhaParaGrid);
                lblStatus
                        .setText(" 1 registro inserido com sucesso na tabela " + tabelaSelecionada.replace(".txt", ""));
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Erro ao salvar: " + e.getMessage());
            }
        }
    }

    private void adicionarNovaColuna() {
        String tabelaSelecionada = listaTabelas.getSelectedValue();
        if (bancoAtual == null || tabelaSelecionada == null) {
            JOptionPane.showMessageDialog(this, "Selecione uma tabela primeiro para adicionar uma coluna.");
            return;
        }

        String novaColuna = JOptionPane.showInputDialog(this, "Digite o nome da nova coluna:");
        if (novaColuna == null || novaColuna.trim().isEmpty())
            return;

        File arquivo = new File(bancoAtual, tabelaSelecionada);
        try {
            List<String> linhasAtualizadas = new ArrayList<>();
            BufferedReader br = new BufferedReader(new FileReader(arquivo));
            String linha;
            boolean primeiraLinha = true;

            while ((linha = br.readLine()) != null) {
                if (primeiraLinha) {
                    linhasAtualizadas.add(linha + ";" + novaColuna);
                    primeiraLinha = false;
                } else {
                    linhasAtualizadas.add(linha + ";");
                }
            }
            br.close();

            BufferedWriter bw = new BufferedWriter(new FileWriter(arquivo));
            for (String l : linhasAtualizadas) {
                bw.write(l);
                bw.newLine();
            }
            bw.close();

            carregarDadosDaTabela(tabelaSelecionada);
            lblStatus.setText(
                    " Coluna '" + novaColuna + "' adicionada à tabela " + tabelaSelecionada.replace(".txt", ""));

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Erro ao adicionar coluna: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Gerenc_Arc().setVisible(true));
    }
}
