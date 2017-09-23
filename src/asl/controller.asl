data([]).

+!addData(Type)[source(X)] : true <-
	?data(ListData);
	.concat(ListData,[[X,Type]],NewLD);
	-+data(NewLD).
	
@p1[atomic] +!finish[source(X)] : true <-
	?data(ListData);
	-+countA(0);
	-+countR(0);
	-+countU(0);
	-+countN(0);
	for (.member(Data,ListData)) {
		.nth(0,Data,SSender);
		.nth(1,Data,TType);
		if (SSender == X & TType == "Accepted"  ) { ?countA(Y1); -+countA(Y1+1); }
		if (SSender == X & TType == "Rejected"  ) { ?countR(Y2); -+countR(Y2+1); }
		if (SSender == X & TType == "Unwanted"  ) { ?countU(Y3); -+countU(Y3+1); }
		if (SSender == X & TType == "NoProposal") { ?countN(Y4); -+countN(Y4+1); }
	};
	?countA(T1);
	.concat("Stat [",X,"] Accepted ",T1," times",CC1)
	tp_cnp.writeOutputFile(CC1);
	?countR(T2);
	.concat("Stat [",X,"] Rejected ",T2," times",CC2)
	tp_cnp.writeOutputFile(CC2);
	?countU(T3);
	.concat("Stat [",X,"] Unwanted ",T3," times",CC3)
	tp_cnp.writeOutputFile(CC3);
	?countN(T4);
	.concat("Stat [",X,"] NoProposal ",T4," times",CC4)
	tp_cnp.writeOutputFile(CC4).
	
//Failure event since initiator uses broadcasting
@r1 +!sendProposals(RequestedItem): true.


