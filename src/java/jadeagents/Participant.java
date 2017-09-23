package jadeagents;

import jade.core.Agent;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

import java.util.*;

public class Participant extends Agent {
	private static final long serialVersionUID = 1L;
	// The catalogue of fruits for sale (maps the fruit to its price)
	private Hashtable<String, Float> catalogue;
	private float margin = (float) 2.0;

	// Put agent initializations here
	protected void setup() {
		// Create the catalogue
		catalogue = new Hashtable<String, Float>();
		updateCatalogue("Banana",1.0);
		updateCatalogue("Apple",2.0);
		updateCatalogue("Pineapple", 2.5);

		// Register the fruit-selling service in the yellow pages
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("fruit-selling");
		sd.setName("JADE-fruit-trading");
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}

		// Add the behaviour serving queries from buyer agents
		addBehaviour(new CFPServer());

		// Add the behaviour serving purchase orders from buyer agents
		addBehaviour(new AcceptProposalServer());
	}

	// Put agent clean-up operations here
	protected void takeDown() {
		// Deregister from the yellow pages
		try {
			DFService.deregister(this);
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}
	}

	/*
     This is invoked by the GUI when the user adds a new fruit for sale
	 */
	public void updateCatalogue(final String title, final double d) {
		addBehaviour(new OneShotBehaviour() {
			private static final long serialVersionUID = 1L;

			public void action() {
				catalogue.put(title, new Float(d));
			}
		} );
	}

	/*
	   Inner class OfferRequestsServer.
	   This is the behaviour used by Fruit-seller agents to serve incoming requests 
	   for offer from buyer agents.
	   If the requested fruit is in the local catalogue the seller agent replies 
	   with a PROPOSE message specifying the price. Otherwise a REFUSE message is
	   sent back.
	 */
	private class CFPServer extends CyclicBehaviour {
		private static final long serialVersionUID = 1L;

		public void action() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.CFP);
			ACLMessage msg = myAgent.receive(mt);
			if (msg != null) {
				// CFP Message received. Process it
				String title = msg.getContent();
				ACLMessage reply = msg.createReply();

				Float price = (Float) catalogue.get(title);
				if (price != null) {
					// The requested fruit is available for sale. Reply with the price
					reply.setPerformative(ACLMessage.PROPOSE);
					reply.setContent(String.format("%.2f", margin*Math.random()+price.floatValue()));
				}
				else {
					// The requested fruit is NOT available for sale.
					reply.setPerformative(ACLMessage.REFUSE);
					reply.setContent("not-available");
				}
				myAgent.send(reply);
			}
			else {
				block();
			}
		}
	}

	/*
	   Initiator is saying he accepted my proposal, so inform DONE
	 */
	private class AcceptProposalServer extends CyclicBehaviour {

		private static final long serialVersionUID = 1L;

		public void action() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL);
			ACLMessage msg = myAgent.receive(mt);
			if (msg != null) {
				// ACCEPT_PROPOSAL Message received. Process it
				ACLMessage reply = msg.createReply();

				reply.setPerformative(ACLMessage.INFORM);
				myAgent.send(reply);
			}
			else {
				block();
			}
		}
	}
}
