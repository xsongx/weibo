package catcher;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;


public class NameEncodeJSRunner {
	private ScriptEngineManager manager = new ScriptEngineManager();
	private ScriptEngine engine = manager.getEngineByName("JavaScript");
	Invocable inv=null;
	public NameEncodeJSRunner(){
		try {
			engine.eval(new FileReader(new File("js/suencode.js")));
		} catch (FileNotFoundException e) {
		} catch (ScriptException e) {
		}
		inv = (Invocable) engine;
	}
	public String executeFunction(String username){
		String value=null;
		try {
			value=String.valueOf(inv.invokeFunction("suencode",username));
		} catch (NoSuchMethodException e) {
		} catch (ScriptException e) {
		}
		return value;
	}
}
