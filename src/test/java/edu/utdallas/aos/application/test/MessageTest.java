package edu.utdallas.aos.application.test;

import org.junit.Test;

import com.google.gson.Gson;

import edu.utdallas.aos.message.DoneMessage;
import edu.utdallas.aos.message.Message;
import edu.utdallas.aos.message.ReadMessage;
import edu.utdallas.aos.message.SuccessMessage;
import edu.utdallas.aos.message.WriteMessage;

public class MessageTest {
	
	@Test
	public void testConstructors(){
		Message success = new SuccessMessage();
		Message done 	= new DoneMessage();
		Message read	= new ReadMessage();
		Message write	= new WriteMessage();
		
		System.out.println(success.getType());
		System.out.println(done.getType());
		System.out.println(read.getType());
		System.out.println(write.getType());
		
		Gson gson = new Gson();
		
		read.setVN(1);
		read.setClock("{\"1\":0,\"0\":0,\"2\":0}");
		read.setFileName("1.txt");
		read.setContent("file content goes here.");
		read.setRU(5);
		System.out.println(gson.toJson(read));
	}	

}
