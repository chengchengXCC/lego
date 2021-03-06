package transformers;

import soot.tagkit.*;
import java.io.*;
import java.util.*;

public class SecurityLabel implements Tag{

	int value;
	Vector<Integer> labels;
	
	public SecurityLabel(int value){
		this.value = value;
		this.labels = new Vector<Integer>();
	}
	
	public SecurityLabel(int value, Vector<Integer> labels){
		this.value = value;
		this.labels = new Vector<Integer>();
		for(Integer label : labels){
			this.labels.add(label);
		}
	}
	
	public String getName(){
		return "mySoot.SecurityLabel";
	}
	
	public byte[] getValue(){
		ByteArrayOutputStream baos = new ByteArrayOutputStream(4);
		DataOutputStream dos = new DataOutputStream(baos);
		try{
			dos.writeInt(value);
			dos.flush();
		}catch(IOException e){
			System.err.println(e);
			throw new RuntimeException(e);
		}
		return baos.toByteArray();
	}
	
	public Vector<Integer> getLabels(){
		return this.labels;
	}
		
	public void addLabel(int label){
		if(!labels.contains(new Integer(label))){
			labels.add(new Integer(label));
		}
	}
	
	public void addLabels(Vector<Integer> labels){
		for(Integer label : labels){
			if(!this.labels.contains(label)){
				this.labels.add(label);
			}
		}
	}
	
	public boolean isLabeled(int label){
		if(labels.contains(new Integer(label))){
			return true;
		}
		return false;
	}
	
	public void add(SecurityLabel another){
		Vector<Integer> anotherLabels = another.getLabels();
		this.addLabels(anotherLabels);
	}
	
	public void subtract(SecurityLabel another){
		Vector<Integer> anotherLabels = another.getLabels();
		for(Integer label : anotherLabels){
			if(this.getLabels().contains(label)){
				this.getLabels().remove(label);
			}
		}
	}

}
