/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package redes_20151;

import java.util.List;

/**
 *
 * @author lasaro
 */
class Router {

    private String router_name;
    private int num_ports;
    private List<String> connections;

    Router(String router_name, int num_ports, List<String> connections) {
        this.router_name = router_name;
        this.num_ports = num_ports;
        this.connections = connections;
    }
    
}
