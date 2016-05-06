package hms.kb.embedding;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Vector;

public class TransEInitializer {

	private static Random random = new Random();
	public static int relation_num; //Total number of relations
	public static int entity_num;   //Total number of entities

	public static Map<String, Integer> relation2id = new HashMap<>(); //A map of relations and the corresponding IDs
	public static Map<String, Integer> entity2id = new HashMap<>(); //A map of entities and the corresponding IDs
	public static Map<Integer, String> id2entity = new HashMap<>(); //A map of IDs and the corresponding entities
	public static Map<Integer, String> id2relation = new HashMap<>(); //A map of IDs and the corresponding relations

	public static Map<Integer, Map<Integer, Integer>> left_entity = new HashMap<>();
	public static Map<Integer, Map<Integer, Integer>> right_entity = new HashMap<>();

	public static Map<Integer, Double> left_num = new HashMap<>();
	public static Map<Integer, Double> right_num = new HashMap<>();

	public static boolean L1_flag = true; //Flag for using L1 as distance measure

	public static Train train = new Train(); //Instance of the Train class

	
	/**
	 * Generate random number between min and max
	 * @param min
	 * @param max
	 * @return
	 */
	public static double rand(double min, double max) {

		return min + (max - min) * Math.random();

	}

	/**
	 * Get a value for an input x from normal distribution
	 * @param x  
	 * @param miu
	 * @param sigma
	 * @return
	 */
	public static double normal(double x, double miu, double sigma) {
		return 1.0 / Math.sqrt(2 * Math.PI) / sigma * Math.exp(-1 * (x - miu) * (x - miu) / (2 * sigma * sigma));
	}

	/**
	 * Create random value from normal distribution
	 * @param miu
	 * @param sigma
	 * @param min
	 * @param max
	 * @return
	 */
	public static double randn(double miu, double sigma, double min, double max) {
		double x, y, dScope;
		do {
			x = rand(min, max);
			y = normal(x, miu, sigma);
			dScope = rand(0.0, normal(miu, miu, sigma));
		} while (dScope > y);
		return x;
	}

	/**
	 * Calculate the norm of a vector
	 * @param a
	 * @return
	 */
	public static double vec_len(Vector<Double> a) {
		double res = 0;
		for (int i = 0; i < a.size(); i++)
			res += a.get(i) * a.get(i);
		res = Math.sqrt(res);
		return res;
	}

	/**
	 * Calculate the square of a given number
	 * @param x
	 * @return
	 */
	public static double sqr(double x) {
		return x * x;
	}


	/**
	 * Normalized a vector if its length above 1
	 * @param a
	 * @return
	 */
	public static Vector<Double> norm(Vector<Double> a) {
		double x = TransEInitializer.vec_len(a);
		if (x > 1){
			
			for (int ii = 0; ii < a.size(); ii++)
				a.set(ii, a.get(ii) / x);
		

		}

//		if(x > 1)
//			return true;
//		return false;
		return a;
	}

	public static int rand(){
		return random.nextInt();
	}
	
	/**
	 * Generate an integer positive random number in the range [0,x]
	 * @param x
	 * @return
	 */
	public static int rand_max(int x) {
		int res = (rand() * rand()) % x;
		while (res < 0)
			res += x;
		return res;
	}
	/**
	 * Read the training data and initialize index maps
	 * @param entity2idFile
	 * @param relation2idFile
	 * @param trainingFile
	 * @throws IOException
	 */
	public static void prepare(String entity2idFile, String relation2idFile, String trainingFile) throws IOException {
		System.out.println("prepare");

		
		prepareEntityIndex(entity2idFile);

		
		prepareRelationIndex(relation2idFile);

		
		FileInputStream fstream;
		BufferedReader br;
		String strLine;
		
		fstream = new FileInputStream(trainingFile);
		br = new BufferedReader(new InputStreamReader(fstream));
		while ((strLine = br.readLine()) != null) {

			String[] strArr = strLine.split("\t");
			String s1 = strArr[0]; // the head
			String s2 = strArr[1]; // the tail
			String s3 = strArr[2]; // the relation

			if (entity2id.get(s1) == null) {
				System.out.println("miss entity: " + s1);
			}
			if (entity2id.get(s2) == null) {
				System.out.println("miss entity: " + s2);
			}
			if (relation2id.get(s3) == null) {
				relation2id.put(s3, relation_num);
				relation_num++;
			}

			
			if(left_entity.get(relation2id.get(s3))== null){
				Map<Integer, Integer> temp = new HashMap<>();
				temp.put(entity2id.get(s1),1);
				left_entity.put(relation2id.get(s3),temp);
			}
			else{
				Integer val = left_entity.get(relation2id.get(s3)).get(entity2id.get(s1));
				if(val!=null){
					left_entity.get(relation2id.get(s3)).put(entity2id.get(s1),val + 1);
				}
				else{
					left_entity.get(relation2id.get(s3)).put(entity2id.get(s1),1);
				}
			}
			
			
			if(right_entity.get(relation2id.get(s3))== null){
				Map<Integer, Integer> temp = new HashMap<>();
				temp.put(entity2id.get(s2),1);
				right_entity.put(relation2id.get(s3),temp);
			}
			else{
				Integer val = right_entity.get(relation2id.get(s3)).get(entity2id.get(s2));
				if(val!=null){
					right_entity.get(relation2id.get(s3)).put(entity2id.get(s2),val + 1);
				}
				else{
					right_entity.get(relation2id.get(s3)).put(entity2id.get(s2), 1);
				}
			}
			

			train.add(entity2id.get(s1), entity2id.get(s2), relation2id.get(s3));

		}

		for (int i = 0; i < relation_num; i++) {
			double sum1 = 0, sum2 = 0;
			
			if(left_entity.get(i)!=null){
				for (Entry<Integer, Integer> e : left_entity.get(i).entrySet()) {
					sum1++;
					sum2 += e.getValue();

				}
				left_num.put(i, sum2 / sum1);
			}
			else{
				System.out.println("left entity " + i);
			}
		}

		for (int i = 0; i < relation_num; i++) {
			
			if(right_entity.get(i) !=null){
				double sum1 = 0, sum2 = 0;
				for (Entry<Integer, Integer> e : right_entity.get(i).entrySet()) {
					sum1++;
					sum2 += e.getValue();

				}

				right_num.put(i, sum2 / sum1);
			}
			else{
				System.out.println("right entity : " + i);
			}
			
		}
		System.out.println("relation_num= " + relation_num);
		System.out.println("entity_num= " + entity_num);
		br.close();
		
		System.out.println(train);
	}

	/**
	 * Create index maps for relations
	 * @param relation2idFile
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void prepareRelationIndex(String relation2idFile) throws FileNotFoundException, IOException {
		FileInputStream fstream;
		BufferedReader br;
		String strLine;
		// Read the relation file and create the two indices
		fstream = new FileInputStream(relation2idFile);
		br = new BufferedReader(new InputStreamReader(fstream));
		int y = 0;
		while ((strLine = br.readLine()) != null) {
			String relationLabel = strLine.split("\t")[0];

			relation2id.put(relationLabel, y);
			id2relation.put(y, relationLabel);
			relation_num++;
			y++;

		}

		br.close();
	}

	/**
	 * Create index maps for entities
	 * @param entity2idFile
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void prepareEntityIndex(String entity2idFile) throws FileNotFoundException, IOException {
		// Read the entity file and create the two indices
		FileInputStream fstream;
		fstream = new FileInputStream(entity2idFile);
		BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
		String strLine;

		int x = 0;
		while ((strLine = br.readLine()) != null) {
			String entityLabel = strLine.split("\t")[0];

			entity2id.put(entityLabel, x);
			id2entity.put(x, entityLabel);
			entity_num++;
			x++;

		}
		br.close();
	}

	public static void main(String[] args) throws IOException {
		
		//prepare("data/FB15k/entity2id.txt", "data/FB15k/relation2id.txt", "data/FB15k/train.txt");
		
		prepare("data/entities_full.txt",
				"data/relations_full.txt",
				"data/train_full.txt");
		
		
		
		int method = 1;
		if (method==1)
	        Train.version = "bern";
	    else
	        Train.version = "unif";
	    int n = 100;
	    double rate = 0.001;
	    double margin = 1;
	    
		train.run(n, rate, margin, method);
	}

}
