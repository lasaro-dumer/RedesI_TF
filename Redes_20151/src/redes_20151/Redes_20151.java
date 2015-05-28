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
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.atomic.AtomicReference;

/**
 *
 * @author lasaro
 */
public class Redes_20151 {

    private static boolean DEBUG;

    private List<NetworkElement> networkElements;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {// TODO code application logic here
            if (args.length < 2) {
                throw new IllegalArgumentException("Numero de argumentos menor que 2.");
            }

            if (args.length >= 3) {
                DEBUG = (args[2].toLowerCase().equals("true"));
            }

            if (DEBUG) {
                for (int i = 0; i < args.length; i++) {
                    System.out.println("args[" + i + "] = " + args[i]);
                }
            }

            Redes_20151 app = new Redes_20151();

            app.lerArquivo(args[0]);
            app.executarConfig(args[1]);
            if (DEBUG) {
                app.printElementosRede();
            }
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
                        String net_name = st.nextToken().trim();
                        int num_nodes = Integer.parseInt(st.nextToken().trim());
                        Network n = new Network(net_name, num_nodes);
                        this.networkElements.add(n);
                        break;
                    case Router:
                        String router_name = st.nextToken().trim();
                        int num_ports = Integer.parseInt(st.nextToken().trim());
                        List<String> connections = new ArrayList<>();
                        while (st.hasMoreElements()) {
                            String connected = st.nextToken().trim();
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

    private void executarConfig(String redeCIDR) throws Exception {
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
                            router.plug(((Network) networkElement));
                        }
                    } else if (networkElement instanceof Router) {
                        if (((Router) networkElement).getRouter_name().equals(connection)) {
                            redes.add(router.plug(((Router) networkElement)));
                        }
                    }
                }
                netInterface++;
            }
        }

        int CIDRValue = Integer.parseInt(redeCIDR.substring(redeCIDR.lastIndexOf("/") + 1));
        int maxHosts = (int) Math.pow(2, (32 - CIDRValue));
        if (DEBUG) {
            System.out.println("maxHosts=" + maxHosts);
        }

        byte[] bytesRedeBase = (InetAddress.getByName(redeCIDR.substring(0, redeCIDR.lastIndexOf("/")))).getAddress();
        String binarRedeIP = "";

        for (byte b : bytesRedeBase) {
            String s1 = String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
            binarRedeIP += s1;
        }
        if (DEBUG) {
            System.out.println("\t\t 12345678901234567890123456789012");
            System.out.println("binarRedeIP\t=" + binarRedeIP);
        }

        Integer subNetsCIDR = CIDRValue;
        AtomicReference<Integer> ref = new AtomicReference<Integer>(subNetsCIDR);
        String[] mascarasRedes = gerarMascarasRedes(binarRedeIP, CIDRValue, redes.size(), ref);
        subNetsCIDR = CIDRValue + ref.get();

        for (int i = 0; i < mascarasRedes.length; i++) {
            Network net = redes.get(i);
            net.setNetMask(mascarasRedes[i], subNetsCIDR);
            net.setMaxHosts(maxHosts/redes.size());
            if (DEBUG) {
                System.out.println("rede[" + (i + 1) + "]\t\t=" + mascarasRedes[i]);
            }
        }
    }

    private void printElementosRede() {
        List<Network> redes = getRedes();
        List<Router> routers = gerRouters();
        System.out.println("#NETWORKS");
        for (Network rede : redes) {
            System.out.println(rede.toString());
        }

        System.out.println("#ROUTERS");
        for (Router router : routers) {
            System.out.println(router.toString());
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

    private String[] gerarMascarasRedes(String ipRede, int CIDRValue, int qtdRedes, AtomicReference<Integer> subNetsCIDR) {
        String[] ret = new String[qtdRedes];
        String zeros;
        zeros = "00000000000000000000000000000000";
        //zeros = "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";

        Integer maxBits = 0;

        for (int i = qtdRedes - 1; i >= 0; i--) {
            String redeBin = Integer.toBinaryString(i);
            ret[i] = redeBin;
            if (i == qtdRedes - 1) {
                maxBits = redeBin.length();
                subNetsCIDR.set(maxBits);
            }
            redeBin = (String.format("%" + maxBits + "s", redeBin).replace(' ', '0'));
            ret[i] = (ipRede.substring(0, CIDRValue) + redeBin + zeros).substring(0, 32);
        }

        return ret;
    }

}
