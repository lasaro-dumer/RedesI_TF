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
class TableLine {

    private Router sourceRouter;
    private Network destination;
    private final String nextHopName;
    private Integer netInterface;
    private String nextHopIp;

    public TableLine(Router sourceRouter, Network destination, String nextHopIp, String nextHopName, Integer netInterface) {
        this.sourceRouter = sourceRouter;
        this.destination = destination;
        this.nextHopIp = nextHopIp;
        this.nextHopName = nextHopName;
        this.netInterface = netInterface;
    }

    /**
     * @return the destination
     */
    public Network getDestination() {
        return destination;
    }

    /**
     * @return the nextHopIp
     */
    public String getNextHopIp() {
        return nextHopIp;
    }

    /**
     * @return the netInterface
     */
    public Integer getNetInterface() {
        return netInterface;
    }

    /**
     * @return the sourceRouter
     */
    public Router getSourceRouter() {
        return sourceRouter;
    }

    @Override
    public String toString() {
        String ret = "";
        //<router_name>, <net_dest>, <net_mask>, <nexthop>, <port>
        ret += sourceRouter.getName() + ", " + destination.getNetMask() + ", " + nextHopIp + (nextHopName != null && !nextHopName.isEmpty() ? " (" + nextHopName + ")" : "") + ", " + netInterface;
        return ret;
    }

}
