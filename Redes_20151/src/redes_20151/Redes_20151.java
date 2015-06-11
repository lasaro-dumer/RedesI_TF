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
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
        try {
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

            app.readFile(args[0]);
            app.executeConfig(args[1]);
            app.printNetworkElements();
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            System.out.println("STACK: ");
            e.printStackTrace(System.out);
        }
    }

    //PRIMARY FUNCTION
    private List<NetworkElement> readFile(String arquivo) throws FileNotFoundException, IOException, Exception {
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
            linha = linha.toLowerCase();

            if (linha.equals("#network")) {
                tipo = TipoEntrada.Network;
            } else if (linha.equals("#router")) {
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

    //PRIMARY FUNCTION
    private void executeConfig(String redeCIDR) throws Exception {
        List<Network> redes = getRedes();
        List<Router> routers = gerRouters();

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
                        if (((Router) networkElement).getName().equals(connection)) {
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
        String[] mascarasRedes = generateNetworkMasks(binarRedeIP, CIDRValue, redes.size(), ref);
        subNetsCIDR = CIDRValue + ref.get();

        for (int i = 0; i < mascarasRedes.length; i++) {
            Network net = redes.get(i);
            net.setNetworkMask(mascarasRedes[i], subNetsCIDR);
            if (DEBUG) {
                System.out.println("rede[" + (i + 1) + "]\t\t=" + mascarasRedes[i]);
            }
        }

        generateNetworkMasks(binarRedeIP, CIDRValue, redes);

        // TODO Until this line, what is OK: {Networks IP,Mask, and range} {Plugs between networks and routers (not tested)}
        // TODO what yet need to be done: Router table
        for (Router router : routers) {
            router.fillRouterTable(redes);
        }
    }

    private void printNetworkElements() {
        // TODO A print like that should go to the file, maybe work with redirecting a stream ?
        List<Network> redes = getRedes();
        List<Router> routers = gerRouters();
        System.out.println("#NETWORK");
        for (Network rede : redes) {
            System.out.println(rede.toString());
        }

        System.out.println("#ROUTER");
        for (Router router : routers) {
            System.out.println(router.toString());
        }

        // TODO Print the router table, if its implemented        
        System.out.println("#ROUTERTABLE");
        for (Router router : routers) {
            System.out.print(router.routerTableToString());
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

    private String[] generateNetworkMasks(String ipRede, int CIDRValue, int qtdRedes, AtomicReference<Integer> subNetsCIDR) {
        /* TODO That is fine, but not fully tested, the optimizaiton algorithm was not implemented here, only the basic
         UPDATE: What's necessary here is do the sort the host quantity demanded per network and do the disbuition of the mask and subnetworks of each network
           
         PRIMARY FUNCTION
         */

        String[] ret = new String[qtdRedes];
        String zeros;
        zeros = "00000000000000000000000000000000";

        Integer maxBits = 0;

        for (int i = qtdRedes - 1; i >= 0; i--) {
            String redeBin = Integer.toBinaryString(i);
            ret[i] = redeBin;
            if (i == qtdRedes - 1) {
                maxBits = redeBin.length();
                subNetsCIDR.set(maxBits);
            }
            redeBin = (String.format("%" + maxBits + "s", redeBin).replace(' ', '0'));
            if (DEBUG) {
                System.out.println("redeBin=" + redeBin + ", int=" + binaryMath.bitArrayToInt(redeBin.toCharArray()));
            }
            ret[i] = (ipRede.substring(0, CIDRValue) + redeBin + zeros).substring(0, 32);
        }

        return ret;
    }

    private void generateNetworkMasks(String binarRedeIP, int CIDRValue, List<Network> redes) {
        if (DEBUG) {
            for (int i = 0; i < redes.size(); i++) {
                Network rede = redes.get(i);
                //System.out.println(rede.getName() + ":" + rede.getNumNodes());
            }
        }

        redes.sort((Network net1, Network net2) -> {
            if (net1.getNumNodes() < net2.getNumNodes()) {
                //inverted order  for sort
                return 1;
            } else if (net1.getNumNodes() == net2.getNumNodes()) {
                return 0;
            }
            return -1;
        });

        //net.setNetworkMask(mascarasRedes[i], subNetsCIDR);
        List<BitsRange> ranges = new ArrayList<>();
        for (int i = 32; i >= 1; i--) {
            Integer bits =i;
            String mask = (String.format("%" + i + "s", "1").replace(' ', '1'));
            ranges.add(new BitsRange(bits,mask,0));
        }
        for (int i = 0; i < redes.size(); i++) {
            Network rede = redes.get(i);
            for (BitsRange range : ranges) {
                if(range.min<= rede.getNumNodes() && range.max>=rede.getNumNodes()){
                    range.count++;
                    break;
                }
            }
        }

        if (DEBUG) {
            for (BitsRange range : ranges) {
                if(range.count>0){
                    System.out.println(range);
                }
            }
            for (int i = 0; i < redes.size(); i++) {
                Network rede = redes.get(i);
                //System.out.println(rede.getName() + ":" + rede.getNumNodes());
            }
        }
    }

    private static class BitsRange {
        public final Integer bits;
        public final String mask;
        public int count;
        public final int max;
        public final int min;
        public final int CIDR;

        private BitsRange(Integer bits, String mask, int count) {
            this.bits = bits;
            this.CIDR = 32 - this.bits;
            this.mask = mask;
            this.count = count;
            this.max = binaryMath.bitArrayToInt(mask.toCharArray())-1;
            this.min = binaryMath.bitArrayToInt(mask.substring(1).toCharArray());
        }

        @Override
        public String toString() {
            return "bits:"+bits+"|CIDR:"+CIDR+"|mask:"+mask+"|max:"+max+"|min:"+min+"|count:"+count;
        }
    }

}
