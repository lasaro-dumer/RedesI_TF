/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package redes_20151;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 *
 * @author lasaro
 */
class Network extends NetworkElement {

    private String net_name;
    private int num_nodes;
    private String net_address;
    private int netCIDR;
    private String nextAvailableIP;
    private Map<String, Router> connections;
    private Stack<Router> IPwaitingList;
    
    Network(String net_name, int num_nodes) {
        this.net_name = net_name;
        this.num_nodes = num_nodes;
        this.connections = new HashMap<>();
        this.net_address = null;
        this.netCIDR = -1;
        this.IPwaitingList = new Stack<>();
    }

    /**
     * @return the net_name
     */
    public String getName() {
        return net_name;
    }

    /**
     * @return the num_nodes
     */
    public int getNumNodes() {
        return num_nodes;
    }

    @Override
    public String toString() {
        String ret = "";
        ret = net_name
                + (net_address != null ? ", " + binaryIPtoStringIPv4(net_address) : "")
                + (netCIDR > -1 ? "/" + netCIDR + ", " + IP_range(net_address, netCIDR) : "");//;
        return ret;
    }

    private String IP_range(String net_address, int netCIDR) {
        int availableBits = 32 - netCIDR;

        String minIpAddress = (String.format("%" + (availableBits) + "s", "1").replace(' ', '0'));
        String maxIpAddress = (String.format("%" + (availableBits) + "s", "0").replace(' ', '1'));

        String minIp = net_address.substring(0, netCIDR) + minIpAddress;
        String maxIp = net_address.substring(0, netCIDR) + maxIpAddress;
        return binaryIPtoStringIPv4(minIp) + "-" + binaryIPtoStringIPv4(maxIp);
    }

    void setNetMask(String subNetAddress, int subNetsCIDR) {
        net_address = subNetAddress;
        netCIDR = subNetsCIDR;
        int availableBits = 32 - netCIDR;

        String minIpAddress = (String.format("%" + (availableBits) + "s", "1").replace(' ', '0'));
        nextAvailableIP = net_address.substring(0, netCIDR) + minIpAddress;
        
        while (!IPwaitingList.empty()) {
            Router router = IPwaitingList.pop();
            this.connect(router);
        }
    }

    @Override
    String getNetMask() {
        return (net_address != null ? binaryIPtoStringIPv4(net_address) : "") + (netCIDR > -1 ? "/" + netCIDR : "");
    }

    public String connect(Router router) {
        String ip = getNewIP();
        connections.put(ip, router);
        return ip;
    }

    private String getNewIP() {
        String ret = nextAvailableIP;
        nextAvailableIP = binaryMath.addBinary(nextAvailableIP, "1");
        return ret;
    }

    public boolean hasConnectedRouters() {
        return !connections.isEmpty();
    }

    public TableLine route(Network destination,Router source, Integer sourceNetInterface) {
        TableLine route = null;
        for (Map.Entry<String, Router> connection : connections.entrySet()) {
            String ip = connection.getKey();
            Router router = connection.getValue();
            if(!router.getName().equals(source.getName())){
                if(router.knowsRouteTo(destination)){
                    route = new TableLine(source, destination, NetworkElement.binaryIPtoStringIPv4(ip), sourceNetInterface);
                }
            }
        }
        
        return route;
    }

    void putIPwaitingList(Router router) {
        IPwaitingList.push(router);
    }
}
