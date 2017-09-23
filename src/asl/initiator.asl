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
		.my_name(Me);
		.concat("[",Me,"] Asked for: ",Item,C)
		resources.writeOutputFile(C);
	}.
	
//An expected proposal was received
@np1 +propose(Product,Price)[source(Supplier)] : desiredProducts(LDP) & .sublist([Product],LDP) <-
	.my_name(Me);
	.concat("[",Me,"] Received(",Supplier,"): '",Product,"' $",Price,CC)
	resources.writeOutputFile(CC);
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
			.my_name(Me);
			if (Sz == 0) { //The first is the mininum price		
				.send(XSupplier,tell,acceptProposal(XProduct,XPrice));
				.print("acceptProposal (",XSupplier,"): ''",XProduct ,"'' $", XPrice);
				.concat("[",Me,"] acceptProposal(",XSupplier,"): ",XProduct," $",XPrice,CC)
				resources.writeOutputFile(CC);
			} else { 
				.send(XSupplier,tell,rejectProposal(XProduct,XPrice));
				.print("rejectProposal (",XSupplier,"): ''",XProduct ,"'' $", XPrice);
				.concat("[",Me,"] rejectProposal(",XSupplier,"): ",XProduct," $",XPrice,CC)
				resources.writeOutputFile(CC);
			}
			-propose(XProduct,XPrice)[source(XSupplier)];
			-+listSize(Sz+1);
		}	
	}.
	
//An refuse for proposal was received
@nop3 +refuse(Product)[source(Supplier)] : true <-
	.print("Refuse (",Supplier,"): ''",Product ,"''");
	.my_name(Me);
	.concat("[",Me,"] Refuse(",Supplier,"): '",Product,"'",CC);
	resources.writeOutputFile(CC);
	!deliberate(Product).
	
//A participant informed the service is done
+informDone(Product,Price) : true.

//Failure event since initiator uses broadcasting
@r2 +!cfp(RequestedItem): true.
