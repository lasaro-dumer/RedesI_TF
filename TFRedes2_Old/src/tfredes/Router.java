/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tfredes;

import java.util.ArrayList;

/**
 *
 * @author Joao
 */
public class Router extends Node{
    
    private int num_ports;
    private ArrayList<MacIP> ethList;
    private ArrayList<RouterTable> routerTables;
    
    public Router(String nome, int num_ports) {
        super(nome, Tipo.ROUTER);
        this.num_ports = num_ports;
        this.ethList = new ArrayList<>();
    }
    
    public int getNum_ports() {
        return num_ports;
    }
    
    public void addPort(String mac, String ip){
        ethList.add(new MacIP(mac, ip));
    }

    public String getMacAtEth(int eth){
        return ethList.get(eth).getMAC();
    }
    
    public String getIpAtEth(int eth){
        return ethList.get(eth).getIP();
    }
    
    public ArrayList<MacIP> getEthList(){
        return ethList;
    }
    
    public ArrayList<RouterTable> getRouterTables(){
        return routerTables;
    }
    
}
