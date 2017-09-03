// Agent participant in project tp_cnp

/* Initial beliefs and rules */

/* Initial goals */

!start.

/* Plans */

+!start : true <- 
	.print("hello world.").
	
+!sendProposals : true <-
	.send(initiator,tell,newProposal(1)).
