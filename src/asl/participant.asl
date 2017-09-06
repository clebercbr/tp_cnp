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
+!sendProposals[source(X)] : products(List) <-
    ?margin(Z);
	for (.member(Item,List)){
		.random(Y);
		.nth(0,Item,Name);
		.nth(1,Item,Price);
		.print("Product: ",Name," base price $",Price, " calculating offer...",PPrice);
		.send(X,tell,newProposal(Name,Y*Z+Price));
	}.

