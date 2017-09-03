!start.

+!start : true <- 
	.print("hello, I am here!").
	
+!sendProposals[source(X)]: true <-
	.send(X,tell,newProposal(1)).
