package hms.kb.embedding.api;

import java.io.IOException;
import java.util.Arrays;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;


public class EmbeddingSolrAPI extends EmbeddingAPI{
	
	


	private static SolrClient solr = new HttpSolrClient("http://localhost:8983/solr/TransE/");

	
	public EmbeddingSolrAPI(String entityFile, String relationFile) {
		super(entityFile, relationFile);
	}

	
	
	/**
	 * Getting the embedding of a given entity given its ID
	 * @param entityID
	 * @return
	 */
	public double[] getEmbedding(String entityID){
		String queryText = "id:" +  entityID ;
		return getEmbeddingUsingSolrQuery(queryText);
	}
	

	
	/**
	 * Get the embedding given a search query
	 * @param queryText
	 * @return
	 */
	private double[] getEmbeddingUsingSolrQuery(String queryText){
		double v[] = null;
		SolrQuery query = new SolrQuery();
		query.setQuery(queryText);

		query.add("rows", String.valueOf(1));
		
		QueryResponse response;
		try {
			response = solr.query(query);

			SolrDocumentList results = response.getResults();

			for (int i = 0; i < results.size(); ++i) {
				v = new double[100] ;
				SolrDocument res = results.get(i);

				String embedding = ((String) res.getFieldValue("embedding")).trim();
				
				String strArr[] = embedding.replace("[", "").replace("]", "").split(",");
				
				for (int j = 0; j < strArr.length; j++) {
					v[j] = Double.valueOf(strArr[j]);

				}
				

			}
		} catch (SolrServerException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return v;
	}
	
	
	public static void main(String[] args) {
		EmbeddingSolrAPI e = new EmbeddingSolrAPI("data/entities_full.txt", "data/relations_full.txt");

//		System.out.println("Predict Tail: ");
//		System.out.println(e.predictTail("Q2117579", "P69"));
//		System.out.println();
		System.out.println("Predict relation: ");
		System.out.println(e.predictRelation("Q2117579", "Q126004"));

	}

}
