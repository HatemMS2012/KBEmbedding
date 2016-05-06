package hms.kb.embedding.util;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

public class EmbeddingUtil {

	/**
	 * Calculate cosine similarity
	 * @param v1
	 * @param v2
	 * @return
	 */
	public static double cosine(double[] v1, double[] v2){
		
		RealVector vec1 = new ArrayRealVector(v1);
		RealVector vec2 = new ArrayRealVector(v2);
		return vec1.cosine(vec2);
	}
	
	/**
	 * Calculate dot product
	 * @param v1
	 * @param v2
	 * @return
	 */
	public static double dot(double[] v1, double[] v2){
		
		RealVector vec1 = new ArrayRealVector(v1);
		RealVector vec2 = new ArrayRealVector(v2);
		return vec1.dotProduct(vec2);
	}
	/**
	 * Combine embedding vectors
	 * @param v1
	 * @param v2
	 * @param add true for using addition otherwise use subtruction
	 * @return
	 */
	public static double[] combineEmbeddingVectors(double[] v1, double[] v2, boolean add){
		
		if(v1.length != v2.length){
			throw new RuntimeException("Embedding vectors must have the same length");
		}
		
		int op = 1;
		if(!add){
			op = -1;
		}
		double[] res = new double[v1.length];
		
		
		for (int i = 0; i < res.length; i++) {
			
			res[i] = v1[i]+op*v2[i];
		}
		
		return res;
	}
}
