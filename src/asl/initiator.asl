proposals([]).//proposals(_).
newLP([]).
!start.

//Start broadcasting to participants send their offers
+!start : true <- 
	.print("Please, send your groceries products proposals.");
	.broadcast(achieve,sendProposals).
	
//A proposal was received
@p1[atomic] +newProposal(Product,Price)[source(Supplier)] : true <-
	.print("Proposal received from ",Supplier,": product ''",Product ,"'' price $", Price);
	?proposals(ListProposals);
	.concat(ListProposals,[[Supplier,Product,Price]],NewLP);
	-+proposals(NewLP);
	.print("List: ",NewLP);
	-+checkProposals(Product);
	?bestOffer(SSupplier,PPrice,QtOffers);
	if (QtOffers >= 3) {
		if (SSupplier = Supplier) {
			.send(Supplier,tell,accepted(Product,Price));
		} else {
			.send(Supplier,tell,rejected(Product,Price));
		}
	}.

//Check if already have 3 proposals for one accept and other rejects  
+checkProposals(ProductName) : true <-
    ?proposals(List);
	.length(List,LLenght);
	-+listSize(0);
	-+bestOffer("","",0);
	while(listSize(Sz) & Sz < LLenght)
	{        
		.nth(Sz,List,Item);
		.nth(0,Item,Supplier);
		.nth(1,Item,Product);
		.nth(2,Item,Price);
		.print("Checking proposal from ",Supplier,": product ''",Product ,"'' price $", Price);
		if (Product = ProductName){
			?bestOffer(X,Y,Z);
			if (Z > 0) {
				-+bestOffer(X,Y,Z+1);
				if (Y > Price)
				{
					-+bestOffer(Supplier,Price,Z+1);
				};
			} else {
				-+bestOffer(Supplier,Price,Z+1);
			};
		};
		-+listSize(Sz+1);
    }.	
	
//Failure event since initiator uses broadcasting
+!sendProposals: true.
