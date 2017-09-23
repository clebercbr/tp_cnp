products([["Banana",1],["Apple",2],["Pineapple",2.5]]).
margin(2).
	
//Submit proposal plan, sends to the initiator my product offers
@sp +!cfp(Product)[source(X)] : true <-
	?products(List);
    ?margin(Z);
	if (.sublist([[Product,_]],List)){
		for (.member(Item,List)) {
			.nth(0,Item,Name);
			.nth(1,Item,Price);
			if (Product == Name) {
				.random(Y);
				tp_cnp.formatCurrency(Y*Z+Price,PPrice);
				.send(X,tell,propose(Name,PPrice));
			}
		}
	} else {
		.send(X,tell,refuse(Product));
	}.		

+acceptProposal(Product,Price)[source(X)] : true <-
	.send(X,tell,informDone(Product,Price)).

+rejectProposal(Product,Price)[source(X)] : true.
