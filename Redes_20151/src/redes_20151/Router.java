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
        ret = getRouter_name()+ ", " + num_ports;//  + ", "+ IP0 + ", " + net_mask0;
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

    public void plug(int netInterface, Network network) {
        if(!(netInterface > num_ports - 1)){
            pluged.put(netInterface, network);
        }else{
            throw new IllegalArgumentException("Numero da porta inválido{"+netInterface +"}. Máximo:{"+num_ports +"}");
        }
    }

    public Network plug(int netInterface, Router router) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
