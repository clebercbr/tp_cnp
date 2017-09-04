proposals([]).//proposals(_).
!start.

//Start broadcasting to participants send their offers
+!start : true <- 
	.print("Please, send your groceries products proposals.");
	.broadcast(achieve,sendProposals).
	
//A proposal was received
+newProposal(X,Y)[source(Z)] : true <-
	.print("Proposal received from ",Z,", product ",X ," price $", Y);
	?proposals(LN);
	.print("List: ",LN);
	.concat(LN,[Z,X,Y],K);
	-+proposals(K).
	
//Failure event since initiator uses broadcasting
+!sendProposals: true.
