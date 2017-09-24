
//Stop mas when all initiatores are done
+done(X) : .count(done(_),Y) & expectedDones(Z) & Y == Z <-
	.stopMAS.

//Failure event since initiator uses broadcasting
@r2 +!cfp(RequestedItem): true.
