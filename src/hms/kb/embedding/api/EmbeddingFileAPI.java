//package hms.kb.embedding.api;
//
//import java.io.BufferedReader;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.util.Arrays;
//
//import org.apache.commons.math3.linear.ArrayRealVector;
//import org.apache.commons.math3.linear.RealVector;
//
//import hms.kb.embedding.TransEInitializer;
//import hms.kb.embedding.util.EmbeddingUtil;
//
//
//public class EmbeddingFileAPI extends EmbeddingAPI{
//
//	
//	private static String entityEmbedingFile  ="output/wikidata/Event/entity2vec_ep990.bern";
//	private static String relatiomEmbedingFile  ="output/wikidata/Event/relation2vec_ep990.bern";
//
//	
//	public EmbeddingFileAPI(String entityFile, String relationFile) {
//		super(entityFile, relationFile);
//	}
//
//	
//
//	/**
//	 * Get the embedding of a given entity
//	 * @param entityQuery
//	 * @param embeddingFile
//	 * @return
//	 * @throws IOException
//	 */
//	public double[] getEmbeddings(String entityQuery, String embeddingFile, boolean useID) {
//		
//		double[] v = new double[100];
//		
//		FileInputStream fstream;
//		try {
//			fstream = new FileInputStream(embeddingFile);
//			BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
//			String strLine;
//
//			while ((strLine = br.readLine()) != null) {
//				String entity = strLine.split("\t")[0].trim();
//				String entityRealID = entity.split("#")[0];
//				String entityLabel = entity.split("#")[1];
//				
//				if(useID){
//					if(entityRealID.equals(entityQuery)){
//						
//						String strArr[] = strLine.split("\t");
//						
//						for (int i = 1; i < strArr.length; i++) {
//							v[i-1] = Double.valueOf(strArr[i]);
//						}
//						br.close();
//						return v;
//					}
//				}
//				else{
//					if(entityLabel.equals(entityQuery)){
//						
//						String strArr[] = strLine.split("\t");
//						
//						for (int i = 1; i < strArr.length; i++) {
//							v[i-1] = Double.valueOf(strArr[i]);
//						}
//						br.close();
//						return v;
//					}
//				}
//			
//				
//			}
//			br.close();
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (NumberFormatException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		return null;
//		
//	}
//	
//	
//	
//	
//	
//	
//	public static void main(String[] args) throws IOException {
//
//		EmbeddingFileAPI e = new EmbeddingFileAPI("C:/Devlopement/workspace_hanish/WikidataSampleGenerator/output/sample/entities_Events.txt",
//				"C:/Devlopement/workspace_hanish/WikidataSampleGenerator/output/sample/relations_Events.txt");
//		
////		String tail = e.predictTail("George_Washington","sex_or_gender",false);
////		System.out.println(tail);
////		
//		//Q5389#Olympic_Games	Q40970#International_Olympic_Committee	P664#organizer
////		Q200794#Eurovision_Song_Contest_1961	Q166400#European_Broadcasting_Union	P664#organizer
//
//		String rel = e.predictRelation("Q5389","Q166400",false);
//		System.out.println(rel);
//	}
//
//	@Override
//	public double[] getEmbeddingsByLabel(String entityLabel) {
//		
//		if(entityLabel.contains("#")){
//			entityLabel = entityLabel.split("#")[1];
//
//		}
//		
//		double[] v = getEmbeddings(entityLabel,entityEmbedingFile,false);
//		if(v == null){
//			v = getEmbeddings(entityLabel, relatiomEmbedingFile,false);
//		}
//		return v;
//	}
//	@Override
//	public double[] getEmbedding(String entityID) {
//		
//		if(entityID.contains("#")){
//			entityID = entityID.split("#")[1];
//
//		}
//		double[] v = getEmbeddings(entityID,entityEmbedingFile,true);
//		if(v == null){
//			v = getEmbeddings(entityID, relatiomEmbedingFile,true);
//		}
//		return v;
//	}
//	
//}
//
