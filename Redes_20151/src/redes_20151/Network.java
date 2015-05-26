/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package redes_20151;

/**
 *
 * @author lasaro
 */
class Network extends NetworkElement {

    private String net_name;
    private int num_nodes;
    private String net_address;
    private String net_mask;

    Network(String net_name, int num_nodes) {
        this.net_name = net_name;
        this.num_nodes = num_nodes;
        net_address = null;
        net_mask = null;
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
        //<net_name>, <net_address>, <net_mask>, <IP_range>
        ret = net_name
                + (net_address != null ? ", " + net_address : "")
                + (net_mask != null ? ", " + net_mask + ", " + IP_range(net_address, net_mask) : "");//;
        return ret;
    }

    private String IP_range(String net_address, String net_mask) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
