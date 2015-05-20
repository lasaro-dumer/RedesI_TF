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
public class Maquina extends Node{

    private String mac;
    private String ip;
    private String gateway;
    private ArrayList<MacIP> arpTable;
    
    public Maquina(String nome, String mac, String ip, String gateway) {
        super(nome, Tipo.MAQUINA);
        this.mac = mac;
        this.ip = ip;
        this.gateway = gateway;
        this.arpTable = new ArrayList<>();
    }

    public String getMac() {
        return mac;
    }

    public String getIp() {
        return ip;
    }

    public String getGateway() {
        return gateway;
    }
    
    public ArrayList<MacIP> getArpTable() {
        return arpTable;
    }
    
    public boolean existsInArpTable(Node n){
        
        if(n.getTipo().equals(Tipo.MAQUINA)){
            for (int i = 0; i < arpTable.size(); i++){
                if (arpTable.get(i).getMAC() == ((Maquina)n).getMac() && 
                    arpTable.get(i).getIP() == ((Maquina)n).getIp()){
                    return true;
                }
            }
        }else if(n.getTipo().equals(Tipo.ROUTER)){
            for (int i = 0; i < arpTable.size(); i++){
                if (arpTable.get(i).getMAC() == ((Router)n).getMacAtEth(getEthFromRouter(((Router)n))) && 
                    arpTable.get(i).getIP() == ((Router)n).getIpAtEth(getEthFromRouter(((Router)n)))){
                    return true;
                }
            }
        }
        return false;
    }
    
    public int getEthFromRouter(Router r){
        for(int i=0;i<r.getEthList().size();i++){
            MacIP aux = r.getEthList().get(i);
            if(aux.getIP().equals(this.ip) && aux.getMAC().equals(this.mac)){
                return i;
            }
        }
        return -1;
    }
    
}
