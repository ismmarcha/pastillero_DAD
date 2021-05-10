package es.us.lsi.dad.Utils;

import io.vertx.core.json.JsonObject;

public class Utils {
	
	 public boolean checkJson ( String data ) {
		try {
			new JsonObject(data);
			return true;
			
		}catch(Exception e ) {
			return false;
		}
	}
}
