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
        p.setParameter(Profile.GUI, "false");
        
        cc = Runtime.instance().createMainContainer(p);
    }
    
    void createAgents() throws Exception {
    	//Participants must be created firstly since they must be waiting for a cfp
        for (int i=1; i<=2; i++) {
            AgentController ac = cc.createNewAgent("p"+i, "jadeagents.Participant", new Object[] { i });
            ac.start();
        }
        Thread.sleep(2000);
        for (int i=1; i<=2; i++) {
            Object args[] = new Object[] {"Banana", "Apple", "Guava", "Pineapple"};
            AgentController ac = cc.createNewAgent("i"+i, "jadeagents.Initiator", args);
            ac.start();
        }
    }
}
