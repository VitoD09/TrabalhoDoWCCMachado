import java.util.Scanner;
import java.io.File;
import java.io.IOException;

public class Gerenc_Arc {
    public static void main(String[] args) {
        Scanner leitor = new Scanner(System.in);
        System.out.print("""
            =============================== 
                Gerenciador de Arquivos
            ===============================
             1. Criar um novo arquivo
             2. Editar um arquivo existente
             3. Deletar um arquivo
             4. Ler um arquivo
             5. Sair
            ===============================
            escolha uma opção:
                """);
        int escolha = leitor.nextInt();
        leitor.nextLine();
       
       
        if(escolha == 1){
            File arquivo = nomeArquivo();
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
        else if(escolha == 3){
            deleteArquivo();
        }
        else if(escolha == 4){
            lerArquivo();
        }else if(escolha == 5){
            System.out.println("Pograma finalizado");
        }
        else {
            System.out.println("Opção inválida. Por favor, escolha uma opção entre 1 e 4.");
        }

        leitor.close();
    }
    public static void EditarArquivo() {
        File arquivo = nomeArquivo();

        if(arquivo.exists()){
            Scanner Escrever = new Scanner(System.in);
            System.out.println("Digite o conteúdo a ser adicionado ao arquivo: ");
            String conteudo = Escrever.nextLine();
            try {
                java.io.FileWriter escritor = new java.io.FileWriter(arquivo, true);
                escritor.write(conteudo + System.lineSeparator());
                escritor.close();
                System.out.println("Conteúdo adicionado ao arquivo com sucesso.");
            } 
             catch (IOException e) {
                 System.out.println("Ocorreu um erro ao escrever no arquivo: " + e.getMessage());

            }
            Escrever.close();   
         } else {
                System.out.println("O arquivo não existe.");
                return;
            }     
    }  
    public static void deleteArquivo(){
        File arquivo = nomeArquivo();
        if(arquivo.exists()){
            arquivo.delete();
            if(arquivo.exists() == false){
                System.out.println("Arquivo deletado com sucesso: " + arquivo.getName());
            } else {
                System.out.println("Falha ao deletar o arquivo.");
            }
        }
    }
    public static void lerArquivo(){
        File arquivo = nomeArquivo();

        try{
            Scanner leitor = new Scanner(arquivo);
            while(leitor.hasNextLine()){
                if(leitor.hasNextLine()){
                    String linha = leitor.nextLine();
                    System.out.println(linha);
                } else {
                    break;
                }
            }
                leitor.close();
        } catch (IOException e) {
            System.out.println("Ocorreu um erro ao ler o arquivo: ");
        }
        
        
    }
    public static  File nomeArquivo(){
        System.out.print("Digite o nome do arquivo: ");
        Scanner leitor = new Scanner(System.in);
        String nomeArquivo = leitor.nextLine();

        while(nomeArquivo.isEmpty()){
            System.out.print("Nome do arquivo não pode ser vazio. Digite novamente: ");
            nomeArquivo = leitor.nextLine();
        }
        if (!nomeArquivo.endsWith(".txt")) {
             nomeArquivo = nomeArquivo + ".txt";
        }
        File arquivo = new File(nomeArquivo);
        leitor.close();
        return arquivo;
    }
    
}
