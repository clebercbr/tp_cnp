products([
		["Banana",1], 
		["Apple",2], 
		["Pinapple",2.5]
		]).
margin(2).
	
//Submit proposal plan, sends to the initiator my product offers
+!sendProposals(RequestedItem)[source(X)] : true <-
	?products(List);
    ?margin(Z);
	+replied(_);
	for (.member(Item,List)) {
		.nth(0,Item,Name);
		.nth(1,Item,Price);
		if (RequestedItem = Name) {
			if (.random(W) & W >= 0.1) {//This is just a random decision to send or not a proposal
				.random(Y);
				tp_cnp.formatCurrency(Y*Z+Price,PPrice);
				.print("Product: ",Name," base price $",Price, " sales price $",PPrice);
				.send(X,tell,newProposal(Name,PPrice));
				-+replied(X);
			} else {
				.send(X,tell,noProposal(Name));
				-+replied(X);
			}
		}
	}
	?replied(A);
	if (A \== X) {
		.send(X,tell,noProposal(RequestedItem));
	}
	-replied(_).		


+accepted(Product,Price)[source(X)] : true <-
	.print("Accepted by ",X,": ",Product,"  $",Price).

+rejected(Product,Price)[source(X)] : true <-
	.print("Rejected by ",X,": ",Product,"  $",Price).
