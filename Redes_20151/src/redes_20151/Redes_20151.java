/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package redes_20151;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 *
 * @author lasaro
 */
public class Redes_20151 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        System.out.println("Hello world");

        lerArquivo("");
    }

    private static boolean lerArquivo(String arquivo) {
        boolean leu = false;

        try {
            File f = new File(arquivo);
            FileReader fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);
            StringTokenizer st;

            String linha = br.readLine();
            /*
             #NETWORK
             <net_name>, <num_nodes>
             #ROUTER
             <router_name>, <num_ports>, <(net|router)_name0>, <(net|router)_name1>, …, <(net|router)_nameN>
             */
            while (linha != null) {
                TipoEntrada tipo = TipoEntrada.Network;
                
                if (linha.equals("#NETWORK")) {
                    tipo = TipoEntrada.Network;
                } else if (linha.equals("#ROUTER")) {
                    tipo = TipoEntrada.Router;
                } else {
                    st = new StringTokenizer(linha, ",");

                    switch (tipo) {
                        case Network:
                            String net_name = st.nextToken();
                            int num_nodes = Integer.parseInt(st.nextToken());
                            Network n = new Network(net_name,num_nodes);
                            
                            break;
                        case Router:
                            String router_name = st.nextToken();
                            int num_ports = Integer.parseInt(st.nextToken());
                            List<String> connections = new ArrayList<>();
                            while (st.hasMoreElements()) {
                                String connected = st.nextToken();
                                connections.add(connected);
                            }
                            Router r = new Router(router_name,num_ports,connections);
                            
                            break;
                        default:
                            throw new Exception("Erro ao ler o arquivo.");
                    }
                }
                linha = br.readLine();
            }
            br.close();
            leu = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return leu;
    }

}
