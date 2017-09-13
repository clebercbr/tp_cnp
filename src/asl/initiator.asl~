proposals([]).//proposals(_).
desiredProducts(["Banana","Apple"]).
!askForProposals.

//Start broadcasting to participants send their offers
@r1 +!askForProposals : true <- 
	?desiredProducts(LDP);
	for (.member(Item,LDP)){
		.print("Please, send your proposals for '",Item,"'.");
		.broadcast(achieve,sendProposals(Item));	
		.wait(3000);
		-+checkProposals(Item);
		?bestOffer(SSupplier,PProduct,PPrice,QtOffers);
		?proposals(ListProposals);
		.print("Proposals received: ",ListProposals,". Best offer: ",PProduct," $",PPrice);
		for (.member(Proposal,ListProposals))
		{
			.nth(0,Proposal,XSupplier);
			.nth(1,Proposal,XProduct);
			.nth(2,Proposal,XPrice);
			if ((QtOffers >= 3) & (SSupplier = XSupplier) & (PProduct = XProduct)) {
				.send(XSupplier,tell,accepted(XProduct,XPrice));
			} else { 
				.send(XSupplier,tell,rejected(XProduct,XPrice));
			}
			?proposals(CLP);
			.delete([XSupplier,XProduct,XPrice],CLP,Result);
			-+proposals(Result);
		}
	}.
	
//A proposal was received
@p1 +newProposal(Product,Price)[source(Supplier)] : true <-
	.print("Proposal received from ",Supplier,": product ''",Product ,"'' price $", Price);
	?proposals(ListProposals);
	.concat(ListProposals,[[Supplier,Product,Price]],NewLP);
	-+proposals(NewLP);
	.print("List: ",NewLP).

//Check if already have 3 proposals for one accept and other rejects  
@p2 +checkProposals(ProductName) : true <-
    ?proposals(List);
	.length(List,LLenght);
	-+listSize(0);
	-+bestOffer("","","",0);
	while(listSize(Sz) & Sz < LLenght)
	{        
		.nth(Sz,List,Item);
		.nth(0,Item,Supplier);
		.nth(1,Item,Product);
		.nth(2,Item,Price);
		.print("Checking proposal from ",Supplier,": product ''",Product ,"'' price $", Price);
		if (Product = ProductName){
			?bestOffer(X,W,Y,Z);
			if (Z > 0) {
				-+bestOffer(X,W,Y,Z+1);
				if (Y > Price)
				{
					-+bestOffer(Supplier,Product,Price,Z+1);
				};
			} else {
				-+bestOffer(Supplier,Product,Price,Z+1);
			};
		};
		-+listSize(Sz+1);
    }.	
	
//Failure event since initiator uses broadcasting
@r2 +!sendProposals: true.
