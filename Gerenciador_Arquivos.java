import java.util.Scanner;
import java.io.File;
import java.io.IOException;

public class Gerenc_Arc {
    public static void main(String[] args) {
        Scanner leitor = new Scanner(System.in);
        System.out.println("""
            =============================== 
                Gerenciador de Arquivos
            ===============================
            1. Criar um novo arquivo
            2. Editar um arquivo existente

            escolha uma opção:
                """);
        int escolha = leitor.nextInt();
        String nomeArquivo = leitor.nextLine();
        File arquivo = new File(nomeArquivo + ".txt");
        
        if(escolha == 1){
            try {
            if(arquivo.createNewFile()){
                System.out.println("Arquivo criado com sucesso: " + arquivo.getName());
            } else {
                System.out.println("O arquivo já existe.");
            }
        }catch (IOException e) {
            System.out.println("Ocorreu um erro ao criar o arquivo: " + e.getMessage());
        }
    }     else if(escolha == 2){
            EditarArquivo();
        }

        leitor.close();
    }
    public static void EditarArquivo() {
        System.out.print("Digite o nome do arquivo a ser editado (sem extensão): ");
        Scanner leitor = new Scanner(System.in);
        String nomeArquivo = leitor.nextLine();

        if(leitor.hasNextLine()){
            Scanner Escrever = new Scanner(System.in);
            System.out.println("Digite o conteúdo a ser adicionado ao arquivo: ");
            String conteudo = Escrever.nextLine();
            try {
                File arquivo = new File(nomeArquivo + ".txt");
                if(arquivo.exists()){
                java.io.FileWriter escritor = new java.io.FileWriter(arquivo, true);
                escritor.write(conteudo + System.lineSeparator());
                escritor.close();
                System.out.println("Conteúdo adicionado ao arquivo com sucesso.");
            } else {
                System.out.println("O arquivo não existe.");
            }
             } catch (IOException e) {
                 System.out.println("Ocorreu um erro ao escrever no arquivo: " + e.getMessage());
            }

            } else {
                System.out.println("O arquivo não existe.");
                return;
            }
        }
       
    }
