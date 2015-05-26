/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package redes_20151;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 *
 * @author lasaro
 */
public class Redes_20151 {

    private List<NetworkElement> networkElements;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {// TODO code application logic here
            System.out.println("Hello world");
            if (args.length < 2) {
                throw new IllegalArgumentException("Numero de argumentos menor que 2.");
            }

            for (int i = 0; i < args.length; i++) {
                System.out.println("args[" + i + "] = " + args[i]);
            }

            Redes_20151 app = new Redes_20151();

            app.lerArquivo(args[0]);
            app.executarConfig(args[1]);
            app.printElementosRede();
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            System.out.println("STACK: ");
            e.printStackTrace(System.out);
        }
    }

    private List<NetworkElement> lerArquivo(String arquivo) throws FileNotFoundException, IOException, Exception {
        this.networkElements = new ArrayList<>();

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
        
        TipoEntrada tipo = TipoEntrada.Network;
        
        while (linha != null) {

            if (linha.equals("#NETWORK")) {
                tipo = TipoEntrada.Network;
            } else if (linha.equals("#ROUTER")) {
                tipo = TipoEntrada.Router;
            } else {
                st = new StringTokenizer(linha, ",");

                switch (tipo) {
                    case Network:
                        String net_name = st.nextToken();
                        int num_nodes = Integer.parseInt(st.nextToken().trim());
                        Network n = new Network(net_name, num_nodes);
                        this.networkElements.add(n);
                        break;
                    case Router:
                        String router_name = st.nextToken();
                        int num_ports = Integer.parseInt(st.nextToken().trim());
                        List<String> connections = new ArrayList<>();
                        while (st.hasMoreElements()) {
                            String connected = st.nextToken();
                            connections.add(connected);
                        }
                        Router r = new Router(router_name, num_ports, connections);
                        this.networkElements.add(r);
                        break;
                    default:
                        throw new Exception("Erro ao ler o arquivo.");
                }
            }
            linha = br.readLine();
        }
        br.close();

        return this.networkElements;
    }

    private void executarConfig(String redeCIDR) throws UnknownHostException {
        List<Network> redes = getRedes();
        List<Router> routers = gerRouters();

        //ajustar connected dos routers
        for (Router router : routers) {
            List<String> connections = router.getConnections();
            int netInterface = 0;
            for (String connection : connections) {
                for (NetworkElement networkElement : networkElements) {
                    if (networkElement instanceof Network) {
                        if (((Network) networkElement).getName().equals(connection)) {
                            router.plug(netInterface, ((Network) networkElement));
                        }
                    } else if (networkElement instanceof Router) {
                        if (((Router) networkElement).getRouter_name().equals(connection)) {
                            redes.add(router.plug(netInterface, ((Router) networkElement)));
                        }
                    }
                }
                netInterface++;
            }
        }

        String ipRede = redeCIDR.substring(0, redeCIDR.lastIndexOf("/"));
        int maxHosts = (int) Math.pow(2, (32 - Integer.parseInt(redeCIDR.substring(redeCIDR.lastIndexOf("/") + 1))));
        System.out.println("maxHosts=" + maxHosts);

        InetAddress ip = InetAddress.getByName(ipRede);
        byte[] bytesRedeBase = ip.getAddress();
        for (byte b : bytesRedeBase) {
            System.out.print((b & 0xFF) + ".");
        }
        System.out.println();
        /*
         bytesRedeBase[3] += 1;
        
         System.out.println(bytesRedeBase[3] & 0xFF);
         */
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void printElementosRede() {
        for (NetworkElement networkElement : networkElements) {
            System.out.println(networkElement.toString());
        }
    }

    private List<Network> getRedes() {
        List<Network> redes = new ArrayList<>();

        for (NetworkElement networkElement : networkElements) {
            if (networkElement instanceof Network) {
                redes.add((Network) networkElement);
            }
        }

        return redes;
    }

    private List<Router> gerRouters() {
        List<Router> routers = new ArrayList<>();

        for (NetworkElement networkElement : networkElements) {
            if (networkElement instanceof Router) {
                routers.add((Router) networkElement);
            }
        }

        return routers;
    }

}
