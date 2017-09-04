products(["Banana", "Apple", "Pineapple"]).
!start.

+!start : products(List) <-
	.length(List,ListLength);
	-+listSize(0);
	while(listSize(Count) & Count < ListLength)
	{        
		.nth(Count,List,Item);
		.print("Product(",Count,"): ",Item);
		-+listSize(Count+1);
    }.
