package resources;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Date;
import java.text.SimpleDateFormat;

import jason.asSemantics.*;
import jason.asSyntax.*;

public class writeOutputFile extends DefaultInternalAction {

	private static final long serialVersionUID = 1L;

	@Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
    	
		try (FileWriter fw = new FileWriter("output.csv", true);
				BufferedWriter bw = new BufferedWriter(fw);
				PrintWriter out = new PrintWriter(bw)) 
		{
			String timeStamp = new SimpleDateFormat("yyyy.MM.dd HH.mm.ss").format(new Date());
			out.print(timeStamp+" "+args[0].toString()+"\n");
			return true;
		} catch (IOException e) {
			return false;
		}
    }
}

