// Agent sample_agent in project tp_cnp



/* Initial beliefs and rules */

/* Initial goals */

!start.

/* Plans */

+!start : true <- 
	.print("hello world.");
	.broadcast(achieve,sendProposals).
	
+newProposal(X) : true <-
	.print("Proposal ",X ," received!").
