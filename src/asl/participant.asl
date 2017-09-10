products([
		["Banana",1], 
		["Apple",2], 
		["Pinapple",2.5]
		]).
margin(2).
!start.

//Just say hello
+!start : true <- 
	.print("hello, I am here!").
	
//Submit proposal plan, sends to the initiator my product offers
+!sendProposals[source(X)] : true <-
	?products(List);
    ?margin(Z);
	for (.member(Item,List)){
		.random(Y);
		.nth(0,Item,Name);
		.nth(1,Item,Price);
		tp_cnp.formatCurrency(Y*Z+Price,PPrice);
		.print("Product: ",Name," base price $",Price, " sale price $",PPrice);
		.send(X,tell,newProposal(Name,PPrice));
	}.

+accepted(Product,Price) : true <-
	.print("Accepted: ",Product,"  $",Price).

+rejected(Product,Price) :true <-
	.print("Rejected: ",Product,"  $",Price).
