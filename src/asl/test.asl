proposals([
		[part2,"Banana",1.123], 
		[part3,"Apple",1.56666], 
		[part1,"Banana",1.223], 
		[part1,"Apple",1.66666], 
		[part3,"Apple",2.36666], 
		[part3,"Banana",2.123], 
		[part2,"Pineapple",2.5555]
		]).
!test.

+!test : true <-
    ?proposals(List);
	.length(List,LLenght);
	-+listSize(0);
	-+bestOffer("","",0);
	while(listSize(Sz) & Sz < LLenght)
	{        
		.nth(Sz,List,Item);
		.nth(0,Item,Supplier);
		.nth(1,Item,ProductName);
		.nth(2,Item,Price);
		if (ProductName = "Pineapple"){
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
	
/*

		if (ProductName = "Pinapple"){
			if (bestOffer(X,Y,Z)) {
				if (Y > Price)
				{
					-+bestOffer(Supplier,Price,Qt+1);
					-+svshsbasma(X,Y);
					?listTs2(LN);
					.concat(LN,[[Supplier,Price]],K);
					-+listTs2(K);
				};
			} else {
				-+bestOffer(Supplier,Price);
				?listTst(LN);
				.concat(LN,[[Supplier,Price]],K);
				-+listTst(K);
			};
		};
		-+listSize(Sz+1);

*/