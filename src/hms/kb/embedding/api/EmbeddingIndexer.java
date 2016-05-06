package hms.kb.embedding.api;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.common.SolrInputDocument;


public class EmbeddingIndexer {

	private static SolrClient solr = new HttpSolrClient("http://localhost:8983/solr/TransE/");

	public static void index(String file) throws IOException, SolrServerException {

		FileInputStream fstream = new FileInputStream(file);
		BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

		String tripleLine;

		int counter = 0;

		while ((tripleLine = br.readLine()) != null) {

			String arr[] = tripleLine.split("\t");

			String entityID = arr[0];
			

			String[] v = new String[100];

			for (int i = 1; i < arr.length; i++) {
				v[i - 1] = arr[i];
			}

			SolrInputDocument document = new SolrInputDocument();

			document.addField("id", entityID);
			document.addField("label", entityID);
			document.addField("embedding", Arrays.toString(v));
			org.apache.solr.client.solrj.response.UpdateResponse response = solr.add(document);

			solr.commit();
			System.out.println(response);
			System.out.println(counter + " entities were indexed");
			counter++;

		}

		br.close();

	}
	
	public static void main(String[] args) throws IOException, SolrServerException {
		index("output/wikidata/embeddings/relation2vec_ep1000.bern");
		index("output/wikidata/embeddings/entity2vec_ep1000.bern");
		
	}

}
