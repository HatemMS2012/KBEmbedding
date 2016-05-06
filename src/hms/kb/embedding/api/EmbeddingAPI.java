package hms.kb.embedding.api;

import java.io.IOException;

import hms.kb.embedding.TransEInitializer;
import hms.kb.embedding.util.EmbeddingUtil;

abstract public class EmbeddingAPI {


	public abstract double[] getEmbedding(String entityID);


	public EmbeddingAPI(String entityFile,String relationFile ) {
	
		try {
			TransEInitializer.prepareEntityIndex(entityFile);
			TransEInitializer.prepareRelationIndex(relationFile);
			System.out.println("Number of relations: " + TransEInitializer.relation2id.size());
			System.out.println("Number of entity: " + TransEInitializer.entity2id.size());
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	/**
	 * Predict the tail given the head and the relation
	 * 
	 * @param head
	 * @param relation
	 * @return
	 */
	public String predictTail(String head, String relation) {

		String finalTail = null;

		double tempSim = 0;

		double[] v1;
		double[] relV;
		
		v1 = getEmbedding(head);
		relV = getEmbedding(relation);

		

		double[] hPlusR = EmbeddingUtil.combineEmbeddingVectors(v1, relV, true);

		for (String tail : TransEInitializer.entity2id.keySet()) {

			double[] v2;
			
			v2 = getEmbedding(tail);
			
			double similarity = EmbeddingUtil.cosine(hPlusR, v2);

			if (similarity > tempSim) {

				finalTail = tail;
				tempSim = similarity;

			}
		}

		return finalTail;
	}

	public String predictRelation(String head, String tail) {

		String finalRelation = null;

		double tempSim = 0;

		double[] v1;
		double[] v2;

		
		v1 = getEmbedding(head);
		v2 = getEmbedding(tail);

		
		double[] tailMinusHead = EmbeddingUtil.combineEmbeddingVectors(v2, v1, false);

		for (String relation : TransEInitializer.relation2id.keySet()) {
			
			
			double[] relVec;
			
			relVec = getEmbedding(relation);
			
			double similarity = EmbeddingUtil.cosine(tailMinusHead, relVec);

			if (similarity > tempSim) {

				finalRelation = relation;
				tempSim = similarity;
				System.out.println(relation);

			}
		}

		return finalRelation;
	}
}
