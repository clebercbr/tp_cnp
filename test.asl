products([
		["Banana",1.123], 
		["Apple",2.66666], 
		["Pinapple",2.5555]
		]).
!start.

+!start : products(List) <-
	.length(List,LLenght);
	-+listSize(0);
	while(listSize(Sz) & Sz < LLenght)
	{        
		.nth(Sz,List,Item);
		.nth(0,Item,Name);
		.nth(1,Item,Price);
		.print("Product(",Sz,"): ",Name," $",Price);
		-+listSize(Sz+1);
    };
	for (.member(Item,List)){
		.formatCurrency(Price,PPrice);
		//iactions.formatCurrency(Price,PPrice);
		//iactions.formatCurrency(Price,PPrice);
		.print("Product: ",Name," base price $",Price, " calculating offer...",PPrice);
	}.

