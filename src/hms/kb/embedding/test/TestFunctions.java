package hms.kb.embedding.test;

import java.util.Vector;

import hms.kb.embedding.Train;
import hms.kb.embedding.TransEInitializer;

public class TestFunctions {

	
	public static void main(String[] args) {
		
		
	
		
//		Vector<Double> a = new Vector<>();
//		a.add(0.3);
//		a.add(0.7);
//		a.add(0.1);
//		a.add(0.9);
//		
//		System.out.println(a);
//		trainer.norm(a);
//		
//		System.out.println(a);
//		
		System.out.println(TransEInitializer.rand_max(1));
		System.out.println(TransEInitializer.rand_max(10));
		
		Vector<Vector<Double>> v = new Vector<>();
		Vector<Double> vv = new Vector<>();
		vv.add(0.5);
		vv.add(0.8);
		vv.add(0.1);
		vv.add(0.99);
		v.add(vv);
		System.out.println(v);
		System.out.println(vv);
		TransEInitializer.norm(v.get(0));
		
		System.out.println(vv);
		System.out.println(v);
	}
}
