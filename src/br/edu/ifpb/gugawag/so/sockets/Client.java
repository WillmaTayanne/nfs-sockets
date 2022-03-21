package br.edu.ifpb.gugawag.so.sockets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Locale;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) throws IOException {
        System.out.println("== Cliente ==");

        // configurando o socket
        Socket socket = new Socket("127.0.0.1", 7001);
        // pegando uma referência do canal de saída do socket. Ao escrever nesse canal, está se enviando dados para o
        // servidor
        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
        // pegando uma referência do canal de entrada do socket. Ao ler deste canal, está se recebendo os dados
        // enviados pelo servidor
        DataInputStream dis = new DataInputStream(socket.getInputStream());

        // laço infinito do cliente
        while (true) {
            System.out.println("Digite uma das opcoes abaixo: readdir | rename | create | remove");
            Scanner teclado = new Scanner(System.in);
            // escrevendo para o servidor
            String mensagem = teclado.nextLine();
            dos.writeUTF(mensagem);

            String mensagemLida = dis.readUTF();

            if(mensagemLida != null) {
                if(mensagem.trim().toLowerCase(Locale.ROOT).matches("rename")) {
                    System.out.println(mensagemLida);
                    System.out.println("Digite o número do arquivo que você deseja renomear ou aperte 'X' para sair: ");
                    Scanner nomeArquivo = new Scanner(System.in);
                    String numeroArquivo = nomeArquivo.nextLine();
                    dos.writeUTF(numeroArquivo);
                    String mensagemLidaNumeroArquivo = dis.readUTF();
                    if (numeroArquivo != null && !numeroArquivo.trim().equals("") && mensagemLidaNumeroArquivo.equals("")) {
                        String mensagemLidaValida = dis.readUTF();
                        if (!numeroArquivo.trim().toUpperCase(Locale.ROOT).equals("X") &&
                                numeroArquivo.matches("-?\\d+(\\.\\d+)?") && mensagemLidaValida.equals("")) {
                            System.out.println("Digite o novo nome deste arquivo: ");
                            Scanner scannerNomeArquivoNovo = new Scanner(System.in);
                            String nomeArquivoNovo = scannerNomeArquivoNovo.nextLine();
                            dos.writeUTF(nomeArquivoNovo);

                            String mensagemLidaFinal = dis.readUTF();
                            System.out.println(mensagemLidaFinal);
                        } else {
                            System.out.println(mensagemLidaValida);
                        }
                    } else {
                        String mensagemLidaFinal = dis.readUTF();
                        System.out.println(mensagemLidaFinal);
                    }
                } else if(mensagem.trim().toLowerCase(Locale.ROOT).matches("readdir")) {
                    System.out.println(mensagemLida);
                } else if(mensagem.trim().toLowerCase(Locale.ROOT).matches("remove")) {
                    System.out.println(mensagemLida);
                    System.out.println("Digite o número do arquivo que você deseja remover ou aperte 'X' para sair: ");
                    Scanner nomeArquivo = new Scanner(System.in);
                    String numeroArquivo = nomeArquivo.nextLine();
                    dos.writeUTF(numeroArquivo);
                    String mensagemLidaNumeroArquivo = dis.readUTF();
                    if (numeroArquivo != null && !numeroArquivo.trim().equals("") && mensagemLidaNumeroArquivo.equals("")) {
                        String mensagemLidaValida = dis.readUTF();
                        if (!numeroArquivo.trim().toUpperCase(Locale.ROOT).equals("X") &&
                                numeroArquivo.matches("-?\\d+(\\.\\d+)?") && mensagemLidaValida.equals("")) {
                            String mensagemLidaFinal = dis.readUTF();
                            System.out.println(mensagemLidaFinal);
                        } else {
                            System.out.println(mensagemLidaValida);
                        }
                    } else {
                        String mensagemLidaFinal = dis.readUTF();
                        System.out.println(mensagemLidaFinal);
                    }
                } else if(mensagem.trim().toLowerCase(Locale.ROOT).matches("create")) {
                    System.out.println("Digite o nome do novo arquivo a ser criado: ");
                    Scanner scannerNomeArquivoNovo = new Scanner(System.in);
                    String nomeArquivoNovo = scannerNomeArquivoNovo.nextLine();
                    dos.writeUTF(nomeArquivoNovo);

                    String mensagemLidaFinal = dis.readUTF();
                    System.out.println(mensagemLidaFinal);
                } else {
                    // lendo o que o servidor enviou
                    System.out.println("Servidor falou: " + mensagemLida);
                }
            }
        }
        /*
         * Observe o while acima. Perceba que primeiro se escreve para o servidor (linha 27), depois se lê do canal de
         * entrada (linha 30), vindo do servidor. Agora observe o código while do Servidor2. Lá, primeiro se lê,
         * depois se escreve. De outra forma, haveria um deadlock.
         */
    }
}
