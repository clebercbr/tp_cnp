!start.

+!start : true <- 
	.print("hello, I am here! Please, send your proposals.");
	.broadcast(achieve,sendProposals).
	
+newProposal(X) : true <-
	.print("Proposal ",X ," received!").
	
//Failure event since initiator uses broadcasting
+!sendProposals: true.
