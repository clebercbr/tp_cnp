package jadeagents;

import java.util.ArrayList;
import java.util.Iterator;

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
			addBehaviour(new OneShotBehaviour() {
				private static final long serialVersionUID = 1L;

				@Override
				public void action() {
					// Update the list of seller agents
					DFAgentDescription template = new DFAgentDescription();
					ServiceDescription sd = new ServiceDescription();
					sd.setType("fruit-selling");
					template.addServices(sd);
					try {
						DFAgentDescription[] result = DFService.search(myAgent, template);
						System.out.print("["+getAID().getLocalName()+"] Participants founded: ");
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
		private MessageTemplate mt; // The template to receive replies
		private int cfpState = 0;
		private String targetProduct;
		ArrayList<Proposal> proposalList = new ArrayList<Proposal>();
		
		public RequestPerformer(String targetFruit) {
			this.targetProduct = targetFruit;
		}

		public void action() {
			
			if (cfpState == 0) {
				// Send the cfp to all sellers
				ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
				for (int i = 0; i < participantAgents.length; ++i) 
					cfp.addReceiver(participantAgents[i]);
				
				cfp.setContent(targetProduct);
				// Each behaviour must be waiting for a different conversation subject
				cfp.setConversationId(targetProduct);
				cfp.setReplyWith("cfp" + System.currentTimeMillis()); // Unique value
				myAgent.send(cfp);
				// Prepare the template to get proposals
				mt = MessageTemplate.and(MessageTemplate.MatchConversationId(targetProduct),
						MessageTemplate.MatchInReplyTo(cfp.getReplyWith()));
				System.out.println("["+getAID().getLocalName()+"] Asked for: " + targetProduct);
				cfpState = 1;
				
			} else {
				// Receive all proposals/refusals from seller agents
				ACLMessage reply = myAgent.receive(mt);
				
				if (reply != null) {
					if (reply.getPerformative() == ACLMessage.PROPOSE) {
						// This is an offer
						String[] values = reply.getContent().split(", ");

						if (values[0].equals(targetProduct)) {
							proposalList.add(new Proposal(reply.getSender(), values[0], Float.parseFloat(values[1])));

							System.out.println("[" + getAID().getLocalName() + "] Received ("
									+ reply.getSender().getLocalName() + ") '" + values[0] + "' $ " + Float.parseFloat(values[1]));
						}

					} else if (reply.getPerformative() == ACLMessage.REFUSE) {
						
						if (reply.getContent().equals(targetProduct)) {
							proposalList.add(new Proposal(reply.getSender(), reply.getContent(), -1));

							System.out.println("[" + getAID().getLocalName() + "] Refuse ("
									+ reply.getSender().getLocalName() + ") '" + reply.getContent());
						}

					} else {
						System.out.println("[" + getAID().getLocalName() + "] Unexpected message :("
								+ reply.getSender().getLocalName() + ") " + reply.getContent());
						
					}
					
					int count = 0, bestProposal = -1;
					float bestPrice = -1; // The best offered price

					for (int i = 0; i < proposalList.size(); i++) {
						if (proposalList.get(i).product.equals(targetProduct)) {
							if (bestProposal == -1 || (bestPrice == -1 && proposalList.get(i).price != -1)
									|| proposalList.get(i).price < bestPrice) {
								// This is the best offer at present
								bestPrice = proposalList.get(i).price;
								bestProposal = i;
							}
							count++;
						}
					}
					// We received all replies for this product 
					if (count == participantAgents.length) {
						for (int i = 0; i < proposalList.size(); i++) {
							if (proposalList.get(i).product.equals(targetProduct)) {
								// If is the best offer and a valid offer: ACCEPT
								if (i == bestProposal && proposalList.get(i).price != -1) {
									ACLMessage order = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
									order.addReceiver(proposalList.get(i).source);
									order.setContent(proposalList.get(i).product);
									order.setConversationId("fruit-trade");
									order.setReplyWith("order" + System.currentTimeMillis());
									myAgent.send(order);
									
									System.out.println("[" + getAID().getLocalName() + "] acceptProposal ("
											+ proposalList.get(i).source.getLocalName() + ") '"
											+ proposalList.get(i).product + "' $ " + proposalList.get(i).price);

									// Prepare the template to get the purchase order reply - but it is not being used!!!
									mt = MessageTemplate.and(MessageTemplate.MatchConversationId("fruit-trade"),
											MessageTemplate.MatchInReplyTo(order.getReplyWith()));
								} else {
									ACLMessage order = new ACLMessage(ACLMessage.REJECT_PROPOSAL);
									order.addReceiver(proposalList.get(i).source);
									order.setContent(proposalList.get(i).product);
									order.setConversationId("fruit-trade");
									order.setReplyWith("order" + System.currentTimeMillis());
									myAgent.send(order);
									
									System.out.println("[" + getAID().getLocalName() + "] rejectProposal ("
											+ proposalList.get(i).source.getLocalName() + ") '"
											+ proposalList.get(i).product + "' $ " + proposalList.get(i).price);
								}
							}
						}

						// Remove proposal for current product
						Iterator<Proposal> proposalIterator = proposalList.iterator();
						while (proposalIterator.hasNext())
							if (proposalIterator.next().product.equals(targetProduct)) proposalIterator.remove();
						
						// If all participants sent proposal for targetProduct and it was already replied
						cfpState = 2;
					}

				} else {
					block();
				}
			}
		}
		
		@Override
		public boolean done() {
			return (cfpState == 2);
		}
	}
	
	private class Proposal {
		public AID source;
		public String product;
		public float price;
		
		public Proposal(AID src, String prd, float prc) {
			this.source = src;
			this.product = prd;
			this.price = prc;
		}
		
	}
}

