/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package redes_20151;

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

    Router(String router_name, int num_ports, List<String> connections) {
        this.router_name = router_name;
        this.num_ports = num_ports;
        this.connections = connections;
        this.pluged = new HashMap<>();
    }

    @Override
    public String toString() {
        String ret = "";
        /*
         #ROUTER
         <router_name>, <num_ports>, <IP0>, <net_mask0>, <IP1> , <net_mask1>, …, <IPN> , <net_maskN>
         */
        ret = getRouter_name() + ", " + num_ports;//  + ", "+ IP0 + ", " + net_mask0;
        for (int i = 0; i < num_ports; i++) {
            if (pluged.containsKey(i)) {
                ret += ", " + i + pluged.get(i).getNetMask();
            }
        }
        return ret;
    }

    /**
     * @return the router_name
     */
    public String getRouter_name() {
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
    }

    private Network plug(int netInterface, Router router) throws Exception {
        Network ret = new Network(this.getRouter_name() + "_" + router.getRouter_name(), 2);
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
}
