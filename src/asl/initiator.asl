desiredProducts(["Banana","Apple","Guava","Pineapple"]).
!askForProposals.

//Start broadcasting to participants send their offers
@r1 +!askForProposals : true <- 
	.abolish(propose(_,_));
	.abolish(refuse(_));
	?desiredProducts(LDP);
	for (.member(Item,LDP)) {
		.broadcast(achieve,cfp(Item));
		.print("Asked for: '",Item,"'.");
	}.
	
//An expected proposal was received
@np1 +propose(Product,Price)[source(Supplier)] : desiredProducts(LDP) & .sublist([Product],LDP) <-
	.print("Received (",Supplier,"): ''",Product ,"'' $", Price);
	!deliberate(Product).

@d1[atomic] +!deliberate(Product) : true <-
	?expectedResponses(N);
	.count(propose(Product,_)[source(_)],X);
	if (X == N) {
		.findall([V,P,S], propose(P,V)[source(S)] & P == Product, ListProposals);
		.sort(ListProposals,SortedProposals);
		.length(SortedProposals,L);
		-+listSize(0);
		while(listSize(Sz) & Sz < L) {
			.nth(Sz,SortedProposals,Proposal);
			.nth(0,Proposal,XPrice);
			.nth(1,Proposal,XProduct);
			.nth(2,Proposal,XSupplier);
			if (Sz == 0) { //The first is the mininum price		
				.send(XSupplier,tell,acceptProposal(XProduct,XPrice));
				.print("acceptProposal (",XSupplier,"): ''",XProduct ,"'' $", XPrice);
			} else { 
				.send(XSupplier,tell,rejectProposal(XProduct,XPrice));
				.print("rejectProposal (",XSupplier,"): ''",XProduct ,"'' $", XPrice);
			}
			-propose(XProduct,XPrice)[source(XSupplier)];
			-+listSize(Sz+1);
		}	
	}.
	
//An refuse for proposal was received
@nop3 +refuse(Product)[source(Supplier)] : true <-
	.print("Refuse (",Supplier,"): ''",Product ,"''");
	!deliberate(Product).
	
//A participant informed the service is done
+informDone(Product,Price) : .count(informDone(_,_),X) & X == 3 <-
	.my_name(Me);
	.send(c,tell,done(Me)).

//Failure event since initiator uses broadcasting
@r2 +!cfp(RequestedItem): true.
