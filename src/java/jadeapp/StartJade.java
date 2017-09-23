package jadeapp;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;


public class StartJade {

    ContainerController cc;
    
    public static void main(String[] args) throws Exception {
        StartJade s = new StartJade();
        s.startContainer();
        s.createAgents();         
    }

    void startContainer() {
        //Runtime rt= Runtime.instance();
        ProfileImpl p = new ProfileImpl();
        p.setParameter(Profile.MAIN_HOST, "localhost");
        p.setParameter(Profile.GUI, "true");
        
        cc = Runtime.instance().createMainContainer(p);
    }
    
    void createAgents() throws Exception {
        for (int i=1; i<2; i++) {
            Object args[] = new Object[] {"Banana", "Apple", "Guava", "Pineapple"};
            AgentController ac = cc.createNewAgent("Buyer"+i, "fruits.FruitBuyerAgent", args);
            ac.start();
        }
        for (int i=1; i<2; i++) {
            AgentController ac = cc.createNewAgent("Seller"+i, "fruits.FruitSellerAgent", new Object[] { i });
            ac.start();
        }
    }
}
