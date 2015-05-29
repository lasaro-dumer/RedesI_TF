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
    private int netCIDR;

    Network(String net_name, int num_nodes) {
        this.net_name = net_name;
        this.num_nodes = num_nodes;
        net_address = null;
        netCIDR = -1;
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
    }

    @Override
    String getNetMask() {
        return (net_address != null ? binaryIPtoStringIPv4(net_address) : "") + (netCIDR > -1 ? "/" + netCIDR : "");
    }
}
