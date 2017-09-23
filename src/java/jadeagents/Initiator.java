package jadeagents;

import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public class Initiator extends Agent {
	private static final long serialVersionUID = 1L;
	// The list of known seller agents
	private AID[] participantAgents;
	
	@Override
	// Put agent initializations here
	protected void setup() {
		// Printout a welcome message
		System.out.println("["+getAID().getLocalName()+"] Hello! Buyer-agent " + getAID().getName() + " is ready.");

		// Get the fruit to buy as a start-up argument
		final Object[] args = getArguments();
		if (args != null && args.length > 0) {
			// Add a TickerBehaviour that schedules a request to seller agents every minute
			addBehaviour(new TickerBehaviour(this, 6000) {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onTick() {
					// Update the list of seller agents
					DFAgentDescription template = new DFAgentDescription();
					ServiceDescription sd = new ServiceDescription();
					sd.setType("fruit-selling");
					template.addServices(sd);
					try {
						DFAgentDescription[] result = DFService.search(myAgent, template);
						System.out.print("["+getAID().getLocalName()+"] Found the following seller agents: ");
						participantAgents = new AID[result.length];
						for (int i = 0; i < result.length; ++i) {
							participantAgents[i] = result[i].getName();
							System.out.print(participantAgents[i].getLocalName()+" ");
						}
						System.out.print("\n");
					} catch (FIPAException fe) {
						fe.printStackTrace();
					}

					// Perform the request
					myAgent.addBehaviour(new RequestPerformer("Banana"));
					myAgent.addBehaviour(new RequestPerformer("Apple"));
					myAgent.addBehaviour(new RequestPerformer("Guava"));
					myAgent.addBehaviour(new RequestPerformer("Pineapple"));
				}
			});
		} else {
			// Make the agent terminate
			System.out.println("No target fruit specified");
			doDelete();
		}
	}

	// Put agent clean-up operations here
	protected void takeDown() {
		// Printout a dismissal message
		System.out.println("["+getAID().getLocalName()+"] Buyer-agent " + getAID().getName() + " terminating.");
	}

	/*
	 * Inner class RequestPerformer. This is the behaviour used by Fruit-buyer
	 * agents to request seller agents the target Fruit.
	 */
	private class RequestPerformer extends Behaviour {
		private static final long serialVersionUID = 1L;
		private AID bestSeller; // The agent who provides the best offer
		private float bestPrice; // The best offered price
		private int repliesCnt = 0; // The counter of replies from seller agents
		private MessageTemplate mt; // The template to receive replies
		private int step = 0;
		private String targetFruit;
		
		public RequestPerformer(String targetFruit) {
			this.targetFruit = targetFruit;
		}

		public void action() {
			switch (step) {
			case 0:
				// Send the cfp to all sellers
				ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
				for (int i = 0; i < participantAgents.length; ++i) {
					cfp.addReceiver(participantAgents[i]);
				}
				cfp.setContent(targetFruit);
				cfp.setConversationId("fruit-trade");
				cfp.setReplyWith("cfp" + System.currentTimeMillis()); // Unique value
				myAgent.send(cfp);
				// Prepare the template to get proposals
				mt = MessageTemplate.and(MessageTemplate.MatchConversationId("fruit-trade"),
						MessageTemplate.MatchInReplyTo(cfp.getReplyWith()));
				System.out.println("["+getAID().getLocalName()+"] Asked for: " + targetFruit);
				step = 1;
				break;
			case 1:
				// Receive all proposals/refusals from seller agents
				ACLMessage reply = myAgent.receive(mt);
				if (reply != null) {
					// Reply received
					if (reply.getPerformative() == ACLMessage.PROPOSE) {
						// This is an offer
						float price = Float.parseFloat(reply.getContent());

						System.out.println("[" + getAID().getLocalName() + "] Received ("
								+ reply.getSender().getLocalName() + ") '" + targetFruit + "' $ " + price);

						if (bestSeller == null || price < bestPrice) {
							// This is the best offer at present
							bestPrice = price;
							bestSeller = reply.getSender();
						}
					} else if (reply.getPerformative() == ACLMessage.REFUSE) {
						
						System.out.println("[" + getAID().getLocalName() + "] Refuse ("
								+ reply.getSender().getLocalName() + ") '" + targetFruit);

					}
					repliesCnt++;
					if (repliesCnt >= participantAgents.length) {
						// We received all replies
						step = 2;
					}
				} else {
					block();
				}
				break;
			case 2:
				// Send the purchase order to the seller that provided the best offer
				ACLMessage order = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
				order.addReceiver(bestSeller);
				order.setContent(targetFruit);
				order.setConversationId("fruit-trade");
				order.setReplyWith("order" + System.currentTimeMillis());
				myAgent.send(order);
				System.out.println("[" + getAID().getLocalName() + "] acceptProposal ("
						+ bestSeller.getLocalName() + ") '" + targetFruit + "' $ " + bestPrice);
				// Prepare the template to get the purchase order reply
				mt = MessageTemplate.and(MessageTemplate.MatchConversationId("fruit-trade"),
						MessageTemplate.MatchInReplyTo(order.getReplyWith()));
				step = 3;
				break;
			case 3:
				// Receive the purchase order reply
				step = 4;
				break;
			}
		}

		public boolean done() {
			return ((step == 2 && bestSeller == null) || step == 4);
		}
	}
}
