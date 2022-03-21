package br.edu.ifpb.gugawag.so.sockets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class Server {

    private static String HOME = System.getProperty("user.home");

    public static void main(String[] args) throws IOException {
        System.out.println("== Servidor ==");

        // Configurando o socket
        ServerSocket serverSocket = new ServerSocket(7001);
        Socket socket = serverSocket.accept();

        // pegando uma referência do canal de saída do socket. Ao escrever nesse canal, está se enviando dados para o
        // servidor
        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
        // pegando uma referência do canal de entrada do socket. Ao ler deste canal, está se recebendo os dados
        // enviados pelo servidor
        DataInputStream dis = new DataInputStream(socket.getInputStream());

        // laço infinito do servidor
        while (true) {
            System.out.println("Cliente: " + socket.getInetAddress());

            String mensagem = dis.readUTF();
            System.out.println(mensagem);
            String resposta = "";

            if( mensagem != null && !mensagem.trim().equals("") ) {
                File home = new File(HOME);

                switch (mensagem.trim().toLowerCase(Locale.ROOT)) {
                    case "readdir": {
                        resposta = listFilesOfFolder(home);
                        break;
                    }
                    case "rename": {
                        List<String> listFiles = listFilesOfFolderRename(home);
                        for(int i = 0; i < listFiles.size(); i++)
                            resposta += "[" + (i+1) + "] - " + listFiles.get(i) + "\n";

                        dos.writeUTF(resposta.substring(0, resposta.length() - 3));
                        String indexFileChange = dis.readUTF();
                        if(indexFileChange != null && !indexFileChange.trim().equals("")) {
                            dos.writeUTF("");

                            if( !indexFileChange.toUpperCase(Locale.ROOT).equals("X") &&
                                    indexFileChange.matches("-?\\d+(\\.\\d+)?") &&
                                    Integer.parseInt(indexFileChange) <= listFiles.size() ) {

                                dos.writeUTF("");
                                File arquivoAntigo = new File(HOME + "\\" + listFiles.get(Integer.parseInt(indexFileChange)-1));
                                String nomeArquivoNovo = dis.readUTF();
                                File arquivoNovo = new File(HOME + "\\" + nomeArquivoNovo);
                                if( !arquivoNovo.exists() ) {
                                    arquivoAntigo.renameTo(arquivoNovo);
                                    resposta = "Nome de Arquivo alterado com sucesso!";
                                } else
                                    resposta = "Nome novo do arquivo já existente nesta página, tente novamente";
                            } else {
                                resposta = "Erro ao selecionar arquivo, tente novamente";
                            }
                        } else {
                            resposta = "Opção invalidá, tente novamente";
                        }
                        break;
                    }
                    case "remove": {
                        List<String> listFiles = listFilesOfFolderRename(home);
                        for(int i = 0; i < listFiles.size(); i++)
                            resposta += "[" + (i+1) + "] - " + listFiles.get(i) + "\n";

                        dos.writeUTF(resposta.substring(0, resposta.length() - 3));
                        String indexFileChange = dis.readUTF();
                        if(indexFileChange != null && !indexFileChange.trim().equals("")) {
                            dos.writeUTF("");

                            if( !indexFileChange.toUpperCase(Locale.ROOT).equals("X") &&
                                    indexFileChange.matches("-?\\d+(\\.\\d+)?") &&
                                    Integer.parseInt(indexFileChange) <= listFiles.size() ) {

                                dos.writeUTF("");
                                File arquivoAntigo = new File(HOME + "\\" + listFiles.get(Integer.parseInt(indexFileChange)-1));
                                arquivoAntigo.delete();
                                resposta = "Arquivo removido com sucesso!";
                            } else {
                                resposta = "Erro ao selecionar arquivo, tente novamente";
                            }
                        } else {
                            resposta = "Opção invalidá, tente novamente";
                        }
                        break;
                    }
                    case "create": {
                        dos.writeUTF("");
                        String nomeArquivoNovo = dis.readUTF();
                        File arquivoNovo = new File(HOME + "\\" + nomeArquivoNovo);
                        if( !arquivoNovo.exists() ) {
                            arquivoNovo.createNewFile();
                            resposta = "Arquivo criado com sucesso!";
                        } else
                            resposta = "Nome de novo arquivo já existente nesta página, tente novamente";
                        break;
                    }
                    default:
                        resposta = "Comando Invalido";
                }

                dos.writeUTF(resposta);
            } else {
                dos.writeUTF("Li sua mensagem: " + mensagem);
            }

        }
        /*
         * Observe o while acima. Perceba que primeiro se lê a mensagem vinda do cliente (linha 29, depois se escreve
         * (linha 32) no canal de saída do socket. Isso ocorre da forma inversa do que ocorre no while do Cliente2,
         * pois, de outra forma, daria deadlock (se ambos quiserem ler da entrada ao mesmo tempo, por exemplo,
         * ninguém evoluiria, já que todos estariam aguardando.
         */
    }

    public static String listFilesOfFolder(final File folder) {
        StringBuilder resposta = new StringBuilder();
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory())
                resposta.append("<DIR> " + fileEntry + "\n");
            else
                resposta.append("      " + fileEntry.getName() + "\n");
        }
        return resposta.substring(0, resposta.length() - 3);
    }

    public static List<String> listFilesOfFolderRename(final File folder) {
        List<String> resposta = new ArrayList<>();
        for (final File fileEntry : folder.listFiles()) {
            if (!fileEntry.isDirectory()) resposta.add(fileEntry.getName());
        }
        return resposta;
    }
}
