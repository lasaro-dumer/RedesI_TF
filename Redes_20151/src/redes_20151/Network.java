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
class Network extends NetworkElement{

    private String net_name;
    private int num_nodes;
    
    Network(String net_name, int num_nodes) {
        this.net_name = net_name;
        this.num_nodes = num_nodes;
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
        ret = net_name ;//+ ", " + net_address + ", "+ net_mask + ", " + IP_range;
        return ret;
    }
}
