products([
		["Banana",1.123], 
		["Apple",2.66666], 
		["Pinapple",2.5555]
		]).
!start.
!test.

+!start : products(List) <-
	.length(List,LLenght);
	-+listSize(0);
	while(listSize(Sz) & Sz < LLenght)
	{        
		.nth(Sz,List,Item);
		.nth(0,Item,Name);
		.nth(1,Item,Price);
		tp_cnp.formatCurrency(Price,PPrice);
		.print("Product(",Sz,"): ",Name," $",PPrice);
		-+listSize(Sz+1);
    }.

+!test : true <-
	tp_cnp.formatCurrency(3.66667,Price);
	.print("$",Price).