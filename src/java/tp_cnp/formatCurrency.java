// Internal action code for project tp_cnp.mas2j

package tp_cnp;

import jason.asSemantics.*;
import jason.asSyntax.*;

public class formatCurrency extends DefaultInternalAction {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
        // execute the internal action
    	
    	StringTerm result = new StringTermImpl(String.format("%.2f", Float.valueOf(args[0].toString())));
    	un.unifies(result, args[1]); 

        return true;
    }
}

