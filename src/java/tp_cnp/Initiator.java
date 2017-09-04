package tp_cnp;

import jason.architecture.AgArch;
import jason.asSemantics.ActionExec;
import jason.asSyntax.StringTerm;

public class Initiator extends AgArch{

	@Override
    public void act(ActionExec action) {
		StringTerm term = null;
        if (action.getActionTerm().getFunctor().startsWith("format2decimals")) {
            //term = (StringTerm)String.format("%.2f", Float.valueOf(action.getActionTerm().getTerm(0).toString()));
            action.getActionTerm().setTerm(0, term);
            action.setResult(true);
        } else {
        	 super.act(action); // send the action to the environment to be performed.
        }
	}    	
}
