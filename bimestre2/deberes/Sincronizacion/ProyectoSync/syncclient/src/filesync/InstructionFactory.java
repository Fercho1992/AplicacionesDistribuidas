package filesync;

/**
 * @author aaron
 * @date 7th April 2013
 */
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/*
 * Use the InstructionFactory to convert a JSON String to an appropriate
 * Instruction class.
 */
public class InstructionFactory {

    private static final JSONParser parser = new JSONParser();

//	InstructionFactory(){
//		
//	}
    // returns null on any problems
    Instruction FromJSON(String jst) {
        JSONObject obj;
        try {
            obj = (JSONObject) parser.parse(jst);
        } catch (ParseException e) {
            // alert the user
            e.printStackTrace();
            return null;
        }
        if (obj != null) {
            Instruction inst = null;
            if (obj.get("Tipo").equals("StartUpdate")) {
                inst = new StartUpdateInstruction();
            } else if (obj.get("Tipo").equals("EndUpdate")) {
                inst = new EndUpdateInstruction();
            } else if (obj.get("Tipo").equals("CopyBlock")) {
                inst = new CopyBlockInstruction();
            } else if (obj.get("Tipo").equals("NewBlock")) {
                inst = new NewBlockInstruction();
            }
            inst.FromJSON(jst);
            return inst;
        }
        return null;
    }
}
