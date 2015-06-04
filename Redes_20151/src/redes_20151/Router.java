/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package redes_20151;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author lasaro
 */
class Router extends NetworkElement {

    private String router_name;
    private int num_ports;
    private List<String> connections;
    private Map<Integer, NetworkElement> pluged;
    private List<TableLine> routerTable;

    Router(String router_name, int num_ports, List<String> connections) {
        this.router_name = router_name;
        this.num_ports = num_ports;
        this.connections = connections;
        this.pluged = new HashMap<>();
        this.routerTable = new ArrayList<>();
    }

    @Override
    public String toString() {
        String ret = "";
        /*
         #ROUTER
         <router_name>, <num_ports>, <IP0>, <net_mask0>, <IP1> , <net_mask1>, …, <IPN> , <net_maskN>
         */
        ret = getName() + ", " + num_ports;//  + ", "+ IP0 + ", " + net_mask0;
        for (int i = 0; i < num_ports; i++) {
            if (pluged.containsKey(i)) {
                ret += ", ";// + i + "=>";
                ret += pluged.get(i).getNetMask();
            }
        }
        return ret;
    }

    /**
     * @return the router_name
     */
    public String getName() {
        return router_name;
    }

    /**
     * @return the num_ports
     */
    public int getNum_ports() {
        return num_ports;
    }

    /**
     * @return the connections
     */
    public List<String> getConnections() {
        return connections;
    }

    public void plug(Network network) throws Exception {
        int netInterface = getAvailableInterface();
        if (netInterface > -1) {
            plug(netInterface, network);
        } else {
            throw new Exception("Nenhuma interface disponivel");
        }
    }

    public Network plug(Router router) throws Exception {
        int netInterface = getAvailableInterface();
        if (netInterface > -1) {
            return plug(netInterface, router);
        } else {
            throw new Exception("Nenhuma interface disponivel");
        }
    }

    private void plug(int netInterface, Network network) {
        pluged.put(netInterface, network);
        if (!network.getNetMask().isEmpty()) {
            network.connect(this);
        }else{
            network.putIPwaitingList(this);
        }
    }

    private Network plug(int netInterface, Router router) throws Exception {
        Network ret = new Network(this.getName() + "_" + router.getName(), 2);
        this.plug(ret);
        router.plug(ret);
        return ret;
    }

    private int getAvailableInterface() {
        for (int i = 0; i < num_ports; i++) {
            if (!pluged.containsKey(i)) {
                return i;
            }
        }

        return -1;
    }

    void fillRouterTable(List<Network> redes) {
        for (Network destination : redes) {
            for (Map.Entry<Integer, NetworkElement> entry : pluged.entrySet()) {
                Integer netInterface = entry.getKey();
                NetworkElement network = entry.getValue();
                TableLine route = null;
                if (network.getName().equals(destination.getName())) {
                    route = new TableLine(this, destination, "0.0.0.0", netInterface);
                } else if (((Network) network).hasConnectedRouters()) {
                    route = ((Network) network).route(destination,this, netInterface);
                }
                if (route != null) {
                    routerTable.add(route);
                    break;
                }
            }
        }
    }

    public String routerTableToString() {
        StringBuilder sb = new StringBuilder();

        for (TableLine tableLine : routerTable) {
            sb.append(tableLine.toString()).append("\n");
        }

        return sb.toString();
    }

    boolean knowsRouteTo(Network destination) {
        for (Map.Entry<Integer, NetworkElement> entry : pluged.entrySet()) {
            Integer netInterface = entry.getKey();
            NetworkElement networkElement = entry.getValue();
            if(networkElement instanceof Network){
                if(networkElement.getName().equals(destination.getName())){
                    return true;
                }
            }
        }
        
        if (!routerTable.isEmpty()) {
            for (TableLine tableLine : routerTable) {
                if(tableLine.getDestination().getName().equals(destination.getName())){
                    return true;
                }
            }
        }
        
        return false;
    }
}
