import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;

public class Gerenc_Arc {
    public static void main(String[] args) {
        int escolha = 0;
        // CORREÇÃO: Scanner principal aberto apenas UMA vez, fora do loop
        Scanner leitor = new Scanner(System.in); 

        while (escolha != 6) { 
            System.out.print("""
                    ===============================
                        Gerenciador de Arquivos
                    ===============================
                     1. Criar um novo arquivo
                     2. Adicionar registro (Tabela)
                     3. Editar um registro (Por ID)
                     4. Deletar um arquivo
                     5. Ler um arquivo
                     6. Sair
                    ===============================
                    escolha uma opção:
                        """);
            escolha = leitor.nextInt();
            leitor.nextLine(); // Limpa o buffer do enter

            if (escolha == 1) {
                criarArquivo();
            } else if (escolha == 2) {
                adicionarRegistro(); // Antigo EditarArquivo (agora focado em tabela)
            } else if (escolha == 3) {
                editarRegistroReal(); // Nova lógica do "caderno"
            } else if (escolha == 4) {
                deleteArquivo();
            } else if (escolha == 5) {
                lerArquivo(); // Atualizado com split
            } else if (escolha == 6) {
                System.out.println("Programa finalizado");
            } else {
                System.out.println("Opção inválida. Por favor, escolha uma opção entre 1 e 6.");
            }
        }
        // CORREÇÃO: Fechamos o leitor só quando o loop acaba de vez
        leitor.close(); 
    }

    // MODIFICADO: Pede os dados separados e salva no padrão id;nome;idade
    public static void adicionarRegistro() {
        File arquivo = nomeArquivo();

        if (arquivo.exists()) {
            Scanner escrever = new Scanner(System.in);
            
            System.out.print("Digite o ID (número): ");
            String id = escrever.nextLine();
            
            System.out.print("Digite o Nome: ");
            String nome = escrever.nextLine();
            
            System.out.print("Digite a Idade (número): ");
            String idade = escrever.nextLine();

            // Força o padrão da tabela
            String conteudo = id + ";" + nome + ";" + idade;

            try {
                // true = adiciona ao final sem apagar o resto
                FileWriter escritor = new FileWriter(arquivo, true); 
                escritor.write(conteudo + System.lineSeparator());
                escritor.close();
                System.out.println("Registro adicionado à tabela com sucesso.");
            } catch (IOException e) {
                System.out.println("Ocorreu um erro ao escrever no arquivo: " + e.getMessage());
            }
        } else {
            System.out.println("O arquivo não existe.");
        }
    }

    // NOVO: A lógica de achar a linha pelo ID, trocar e reescrever
    public static void editarRegistroReal() {
        File arquivo = nomeArquivo();

        if (!arquivo.exists()) {
            System.out.println("O arquivo não existe.");
            return;
        }

        Scanner teclado = new Scanner(System.in);
        System.out.print("Digite o ID do registro que deseja editar: ");
        String idProcurado = teclado.nextLine();

        List<String> linhasEmMemoria = new ArrayList<>();
        boolean idEncontrado = false;

        try {
            // 1. Lê tudo e modifica apenas a linha certa
            Scanner leitorArquivo = new Scanner(arquivo);
            while (leitorArquivo.hasNextLine()) {
                String linha = leitorArquivo.nextLine();
                String[] partes = linha.split(";");

                if (partes.length > 0 && partes[0].equals(idProcurado)) {
                    idEncontrado = true;
                    System.out.print("Digite o NOVO Nome: ");
                    String novoNome = teclado.nextLine();
                    System.out.print("Digite a NOVA Idade: ");
                    String novaIdade = teclado.nextLine();
                    
                    // Monta a linha atualizada e joga na memória
                    linhasEmMemoria.add(idProcurado + ";" + novoNome + ";" + novaIdade);
                } else {
                    // Mantém a linha original na memória
                    linhasEmMemoria.add(linha);
                }
            }
            leitorArquivo.close();

            if (!idEncontrado) {
                System.out.println("ID não encontrado.");
                return;
            }

            // 2. Reescreve o arquivo inteiro (FileWriter sem o 'true')
            FileWriter escritor = new FileWriter(arquivo);
            for (String linhaAtualizada : linhasEmMemoria) {
                escritor.write(linhaAtualizada + System.lineSeparator());
            }
            escritor.close();
            System.out.println("Registro atualizado com sucesso!");

        } catch (IOException e) {
            System.out.println("Erro ao editar: " + e.getMessage());
        }
    }

    public static void criarArquivo() {
        File arquivo = nomeArquivo();
        try {
            if (arquivo.createNewFile()) {
                System.out.println("Arquivo criado com sucesso: " + arquivo.getName());
            } else {
                System.out.println("O arquivo já existe.");
            }
        } catch (IOException e) {
            System.out.println("Ocorreu um erro ao criar o arquivo.");
        }
    }

    public static void deleteArquivo() {
        File arquivo = nomeArquivo();

        if (arquivo.exists()) {
            System.out.println("Tem certeza que deseja deletar o arquivo " + arquivo.getName() + "? (S/N)");
            Scanner leitor = new Scanner(System.in);
            String resposta = leitor.nextLine();

            if (resposta.equalsIgnoreCase("S")) {
                arquivo.delete();
                System.out.println("Arquivo deletado com sucesso: " + arquivo.getName());
            } else {
                System.out.println("Falha ao deletar o arquivo ou operação cancelada.");
            }
        } else {
            System.out.println("O arquivo não existe.");
        }
    }

    // MODIFICADO: Usa split(";") para exibir bonitinho
    public static void lerArquivo() {
        File arquivo = nomeArquivo();

        if (!arquivo.exists()) {
            System.out.println("O arquivo não existe.");
            return;
        }

        try {
            Scanner leitor = new Scanner(arquivo);
            System.out.println("\n--- Tabela de Registros ---");
            while (leitor.hasNextLine()) {
                String linha = leitor.nextLine();
                String[] partes = linha.split(";");

                if (partes.length == 3) {
                    System.out.println("ID: " + partes[0]);
                    System.out.println("Nome: " + partes[1]);
                    System.out.println("Idade: " + partes[2]);
                    System.out.println("---------------------------");
                } else {
                    System.out.println("Linha inválida: " + linha);
                }
            }
            leitor.close();
        } catch (IOException e) {
            System.out.println("Ocorreu um erro ao ler o arquivo.");
        }
    }

    public static File nomeArquivo() {
        System.out.print("Digite o nome do arquivo: ");
        Scanner leitor = new Scanner(System.in);
        String nomeArquivo = leitor.nextLine();

        while (nomeArquivo.trim().isEmpty()) {
            System.out.print("Nome do arquivo não pode ser vazio. Digite novamente: ");
            nomeArquivo = leitor.nextLine();
        }
        if (!nomeArquivo.endsWith(".txt")) {
            nomeArquivo = nomeArquivo + ".txt";
        }
        return new File(nomeArquivo);
    }
}