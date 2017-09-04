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
	.length(List,LLenght);
    ?margin(Z);
	-+listSize(0);
	while(listSize(Sz) & Sz < LLenght)
	{        
		.random(Y);
		.nth(Sz,List,Item);
		.nth(0,Item,Name);
		.nth(1,Item,Price);
		.print("Product(",Sz,"): ",Name," base price $",Price, " calculating offer...");
		//.string.format("%.2f", Price); .format2decimals(Price,X)
		.send(X,tell,newProposal(Name,Y*Z+Price));
		-+listSize(Sz+1);
		.wait(1000);
    }.

