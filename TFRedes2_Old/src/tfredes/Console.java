/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package tfredes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 *
 * @author 11100087
 */
public class Console {
    
    
    private ArrayList<Node> nodos;
    
    private String tipo="";
    
    private static final String broadcast = "FF:FF:FF:FF:FF:FF";
    
    
    private boolean finalReplySent = false;
    private int ttl = 8;
    
    public Console(){
        
        nodos = new ArrayList<>();
        
    }
    
    public boolean leArquivo(String arquivo) {
        boolean leu = false;
        try {
            File f = new File(arquivo);
            FileReader fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);
            StringTokenizer st;
            
            String linha = br.readLine();
            
            
            
            while(linha != null) {
                boolean mudou = checaTipo(linha);
            
                if(mudou){
                    linha = br.readLine();
                    if(linha==null) return true;
                }
                
                st = new StringTokenizer(linha,",");
                if("node".equals(tipo)){
                    String nome = st.nextToken();
                    String mac = st.nextToken();
                    String ip = st.nextToken();
                    String gateway = st.nextToken();
                    nodos.add(new Maquina(nome, mac, ip, gateway));
                }
                
                if("switch".equals(tipo)){
                    String nome = st.nextToken();
                    int num_ports = Integer.parseInt(st.nextToken());
                    nodos.add(new Switch(nome, num_ports));
                }
                
                if("router".equals(tipo)){
                    
                    String name = st.nextToken();
                    int num_ports = Integer.parseInt(st.nextToken());
                    Router router = new Router(name, num_ports);
                    nodos.add(router);
                    while(st.hasMoreTokens()){
                        String mac = st.nextToken();
                        String ip = st.nextToken();
                        router.addPort(mac, ip);
                    }
                }
                
                if("routerTable".equals(tipo)){
                    String name = st.nextToken();
                    String dest = st.nextToken();
                    String nexthop = st.nextToken();
                    int port = Integer.parseInt(st.nextToken());
                    Router router = getRouterWithName(name);
                    router.getRouterTables().add(new RouterTable(dest, nexthop, port));
                }
                
                if("link".equals(tipo)){
                    String src = st.nextToken();
                    int src_port = Integer.parseInt(st.nextToken());
                    String dest = st.nextToken();
                    int dest_port = Integer.parseInt(st.nextToken());
                    Node n = getNodeWithName(src);
                    n.getLinks().add(new Link(src, src_port, dest, dest_port));
                    Switch s = getSwitchWithName(dest);
                    if(s!=null) s.addLink(new Link(dest, dest_port, src, src_port));
                    
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
    
    public Router getRouterWithName(String name){
        Router r=null;
        for(Node n: nodos){
            if(n.getNome().equals(name)){
                r = (Router)n;
                break;
            }
        }
        return r;
    }
    
    public Node getNodeWithName(String name){
        Node node = null;
        for(Node n:nodos){
            if(n.getNome().equals(name)){
                node = n;
                break;
            }
        }
        return node;
    }
    
    
    public boolean checaTipo(String linha){
        switch(linha){
                case "#NODE":
//                    System.out.println("NODE");
                    tipo = "node";
                    return true;

                case "#SWITCH":
//                    System.out.println("SWITCH");
                    tipo = "switch";
                    return true;

                case "#ROUTER":
//                    System.out.println("ROUTER");
                    tipo = "router";
                    return true;

                case "#ROUTERTABLE":
//                    System.out.println("ROUTERTABLE");
                    tipo = "routerTable";
                    return true;

                case "#LINK":
//                    System.out.println("LINK");
                    tipo = "link";
                    return true;

                default:
                    return false;
        }
    }
    
    public void ping (String orig, String dest){     
        
        Node nodoOrigem, nodoDestino;
        
        nodoOrigem = getNodeWithName(orig);
        nodoDestino = getNodeWithName(dest);
        
        if(nodoOrigem.getTipo().equals(Tipo.MAQUINA)){
            if(!((Maquina)nodoOrigem).existsInArpTable(nodoDestino)){
                if(nodoDestino.getTipo().equals(Tipo.MAQUINA)){
                    if(sendArpRequest(nodoOrigem, ((Maquina)nodoDestino).getIp())){
                        sendEcho(nodoOrigem, nodoDestino);
                    }
                }else{
                    if(sendArpRequest(nodoOrigem, ((Router)nodoDestino).getIpAtEth(((Maquina)nodoOrigem).getEthFromRouter((Router)nodoDestino)))){
                        sendEcho(nodoOrigem, nodoDestino);
                    }
                }
            }
        }
        
    }
    
    public ArrayList<Maquina> getMaquinas(){
        ArrayList<Maquina> lista = new ArrayList<>();
        for(Node n: nodos){
            if(n.getTipo().equals(Tipo.MAQUINA)){
                lista.add((Maquina)n);
            }
        }
        return lista;
    }
    
    public ArrayList<Switch> getSwitches(){
        ArrayList<Switch> lista = new ArrayList<>();
        for(Node n: nodos){
            if(n.getTipo().equals(Tipo.SWITCH)){
                lista.add((Switch)n);
            }
        }
        return lista;
    }
    
    public ArrayList<Router> getRouters(){
        ArrayList<Router> lista = new ArrayList<>();
        for(Node n: nodos){
            if(n.getTipo().equals(Tipo.ROUTER)){
                lista.add((Router)n);
            }
        }
        return lista;
    }
    
    public boolean sendArpRequest(Node orig, String ipD){
       String packet, ipDestO, ipDestD;
       Node dest = getNodeByIp(ipD);
       
       if(orig.getTipo().equals(Tipo.MAQUINA)){
            Maquina origem = (Maquina)orig;
            
            ipDestO = origem.getIp();
            ipDestO = ipDestO.substring(0, 9) + "0";
            ipDestD = ipD.substring(0, 9) + "0";
       
            //Mesma rede?
            if( ipDestO.equals(ipDestD)){
                packet = origem.getMac()+ "->" + broadcast + "|" + "ARP_REQUEST" +  "," + ipD +"?";
                System.out.println(packet);
                //Popula ARP Table do nodo destino
                if(dest.getTipo().equals(Tipo.MAQUINA)){
                    ((Maquina)dest).getArpTable().add(new MacIP(origem.getMac(), origem.getIp()));
                }
                ArrayList<Node> mesmaRede;
                mesmaRede = getNetworkNodes(origem.getNome());
                
                for(Node n: mesmaRede){
                    if(n.getTipo().equals(Tipo.MAQUINA)){
                        Maquina m = (Maquina)n;
                        if(m.getIp().equals(ipD)){
                            sendArpReply(dest, orig);
                            break;
                        }
                    }else if(n.getTipo().equals(Tipo.ROUTER)){
                        Router r = (Router)n;
                        if(r.getIpAtEth(origem.getEthFromRouter((Router)r)).equals(ipD)){
                            sendArpReply(dest, orig);
                            break;
                        }
                    }
                }

                  return true;
            //Outra rede
            }else {
                return false;
            }
       }
       
        //verifica se nodo origem e destino estão na mesma rede
       
       //Se está na mesma rede, faz broadcast buscando nodo destino
       
       //Não pertence a mesma rede, terá que buscar conexões em comum nos Links
       /*else{
           
       }*/
       
       
       return false;
    }
    
    public void sendEcho(Node orig, Node dest){
        String packet, ipDestO, ipDestD = "";
        
        if(orig.getTipo().equals(Tipo.MAQUINA)){
            Maquina origem = (Maquina)orig;
            
            
            ipDestO = origem.getIp();
            ipDestO = ipDestO.substring(0, 9) + "0";
            
            if(dest.getTipo().equals(Tipo.MAQUINA)){
                ipDestD = ((Maquina)dest).getIp().substring(0, 9) + "0";
            }else if(dest.getTipo().equals(Tipo.ROUTER)){
                ipDestD = ((Router)dest).getIpAtEth(origem.getEthFromRouter((Router)dest)).substring(0, 9) + "0";
            }
            if( ipDestO.equals(ipDestD)){
                
                String sMac = origem.getMac();
                String sIp = origem.getIp();
                String dMac = "";
                String dIp = "";
                
                if(dest.getTipo().equals(Tipo.MAQUINA)){
                    dMac = ((Maquina)dest).getMac();
                    dIp = ((Maquina)dest).getIp();
                }else if(dest.getTipo().equals(Tipo.ROUTER)){
                    dMac = ((Router)dest).getMacAtEth(origem.getEthFromRouter((Router)dest));
                    dIp = ((Router)dest).getIpAtEth(origem.getEthFromRouter((Router)dest));
                }
                //inicia montagem echo request
               int ttlRq = ttl;
               packet = sMac+"->"+dMac+"|"+sIp+"->"+dIp+","+"TTL="+ttlRq+"|ICMP_ECHOREQUEST";
               System.out.println(packet);
               //inicia montagem echo reply
               int ttlRp = ttl;
               packet = dMac+"->"+sMac+"|"+dIp+"->"+sIp+","+"TTL="+ttlRp+"|ICMP_ECHOREPLY";
               System.out.println(packet);
               finalReplySent = true;
            }
       }
    }
    
    public boolean sendArpReply(Node source, Node dest){
        String packet="";
        
        Maquina destino;
        
        
        if(source.getTipo().equals(Tipo.MAQUINA)){
            Maquina m = (Maquina)source;
            if(dest.getTipo().equals(Tipo.MAQUINA)){
                packet = m.getMac()+"->"+((Maquina)dest).getMac()+"|"+"ARP_REPLY"+","+m.getIp()+"="+m.getMac();
            }else if(dest.getTipo().equals(Tipo.ROUTER)){
                packet = m.getMac()+"->"+((Router) dest).getMacAtEth(((Maquina)m).getEthFromRouter((Router)dest))+"|"+"ARP_REPLY"+","+m.getIp()+"="+m.getMac();
            }
        }
            
         
        System.out.println(packet);
        return true;
    }
    
    public ArrayList<Node> getNetworkNodes(String name){
        ArrayList<Node> aux = new ArrayList<>();
        
        Node nodo = getNodeWithName(name);
            
        for(Link l : nodo.getLinks()){
            aux.add(getNodeWithName(l.getDest()));
        }
        
        if(aux.size()==1){
            if(aux.get(0).getTipo().equals(Tipo.SWITCH)){
                aux = getNetworkNodes(aux.get(0).getNome());
            }   
        }
        return aux;
    }
    
    
    public Node getNodeByIp(String nodeIp){
       Node r = null;
        for (Node n: nodos){
            if(n.getTipo().equals(Tipo.MAQUINA)){
                if(((Maquina)n).getIp().equals(nodeIp)){
                    r = n;
                    break;
                }
            }
        }
        return r;
    }
    
    public Switch getSwitchWithName(String name){
        Switch s=null;
        for(Switch sAux: getSwitches()){
            if(sAux.getNome().equals(name)){
                s = sAux;
            }
        }   
        return s;
    }
    
    public static void main(String []args){
        Console cons = new Console();
        
        
//        for (String arg : args) {
//            System.out.println(arg);
//        }
        
        
        cons.leArquivo(args[0]);
        
        if(args[1].equals("ping")){
            cons.ping(args[2], args[3]);
            
        }else if(args[2].equals("tracert")){
        //tracert
//            System.out.println("EXECUTA TRACERT");
        }
        else return;
    }
}
