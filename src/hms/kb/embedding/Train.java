package hms.kb.embedding;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Vector;

public class Train {

	
	public static String version ;
	public Map<String, Map<Integer, Integer>> ok = new HashMap<String, Map<Integer, Integer>>();
	int n; //Number of embedding dimensions
	int method; 
	double res;// loss function value
	double count, count1;// loss function gradient
	double rate; //learning rate
	double margin; //the margin for the energy function
	double belta;
	Vector<Integer> fb_h = new Vector<>(); //Embedding of a head element
	Vector<Integer> fb_l = new Vector<>(); //Embedding of a tail element
	Vector<Integer> fb_r = new Vector<>(); //Embedding of a relation element
	Vector<Vector<Integer>> feature;
	
	Vector<Vector<Double>> relation_vec = new Vector<>();
	Vector<Vector<Double>> entity_vec = new Vector<>();
	Vector<Vector<Double>> relation_tmp = new Vector<>();
	Vector<Vector<Double>> entity_tmp = new Vector<>();

	
	private static final String outputDir = "output/wikidata/embeddings/"; //Where to store the final embeddings
	
	
	private static NumberFormat nf = NumberFormat.getNumberInstance(Locale.ENGLISH);
	private static DecimalFormat df = (DecimalFormat)nf;
	static{
		df.setMaximumFractionDigits(6);
		Locale.setDefault(Locale.ENGLISH);

	}
	
	
	
	public void run(int n_in, double rate_in, double margin_in, int method_in) throws FileNotFoundException {
		n = n_in;
		rate = rate_in;
		margin = margin_in;
		method = method_in;

		// initialize the relation and entity embedding matrices and create a temp copy for each
		relation_vec = new Vector<>(); // relation_num
		entity_vec = new Vector<>();
		relation_tmp = new Vector<>();
		entity_tmp = new Vector<>();

		initRelationMatrix();
		
		initEntityMatrix();

		bfgs();
	}

	/**
	 * Initialize the entity matrix
	 */
	private void initEntityMatrix() {
		
		//1- First initialized the relation matrix B_k,n where k: number of relations and n: the embedding dimension

		for (int i = 0; i < TransEInitializer.entity_num; i++) {
			
			Vector<Double> v = new Vector<>();
			for (int j = 0; j < n; j++) {
				v.add(0.0);
			}
			entity_vec.add(v);
			
		}
		//2- Initialize the embedding vector of each relation
		for (int i = 0; i < TransEInitializer.entity_num; i++) {
			for (int ii = 0; ii < n; ii++){
				entity_vec.get(i).set(ii, TransEInitializer.randn(0, 1.0 / n, -6 / Math.sqrt(n), 6 / Math.sqrt(n)));
			}

			TransEInitializer.norm(entity_vec.get(i));
		}
	}

	/**
	 * Initialize the embedding vectors of relations and entities as described in the TransE paper
	 */
	private void initRelationMatrix() {
		//1- First initialized the relation matrix A_m,n : m: number of relations and n: the embedding dimension
		for (int i = 0; i < TransEInitializer.relation_num; i++) {
			Vector<Double> v = new Vector<>();
			for (int j = 0; j < n; j++) {
				v.add(0.0);
			}
			relation_vec.add(v);
		}
		//2- Initialize the embedding vector of each relation
		for (int i = 0; i < TransEInitializer.relation_num; i++) {
			for (int ii = 0; ii < n; ii++){
				relation_vec.get(i).set(ii, TransEInitializer.randn(0, 1.0 / n, -6 / Math.sqrt(n), 6 / Math.sqrt(n)));
			    //relation_vec.get(i).set(ii,0.1*ii+0.1);
			}

		}
	}

	/**
	 * Add training instance
	 * @param x
	 * @param y
	 * @param z
	 */
	public void add(int x, int y, int z) {
		fb_h.add(x);
		fb_r.add(z);
		fb_l.add(y);
		
		Map<Integer, Integer> m = new HashMap<Integer, Integer>();
		m.put(y, 1);
		ok.put(x+"-"+z, m);

	}


	public void bfgs() throws FileNotFoundException
	{
	        res=0;
	        int nbatches=100;
	        int nepoch = 1001;
	        int batchsize = fb_h.size()/nbatches;
	            for (int epoch=0; epoch<nepoch; epoch++)
	            {

	            	res=0;
	             	for (int batch = 0; batch<nbatches; batch++)
	             	{
	             		relation_tmp=relation_vec;
	            		entity_tmp = entity_vec;
	             		for (int k=0; k<batchsize; k++)
	             		{
							int i= TransEInitializer.rand_max(fb_h.size());
							int j= TransEInitializer.rand_max(TransEInitializer.entity_num);
							
							double pr = 1000*TransEInitializer.right_num.get(fb_r.get(i))/(TransEInitializer.right_num.get(fb_r.get(i))+TransEInitializer.left_num.get(fb_r.get(i)));
							
							if (method ==0)
	                            pr = 500;
							if (TransEInitializer.rand()%1000<pr)
							{

								while (ok.get(fb_h.get(i)+"-"+fb_r.get(i)).get(j) !=null) //Problem here with reference TODO
									j= TransEInitializer.rand_max(TransEInitializer.entity_num);
								
								train_kb(fb_h.get(i),fb_l.get(i),fb_r.get(i),fb_h.get(i),j,fb_r.get(i));
							}
							else
							{

								if(ok.get(j+"-"+fb_r.get(i))!=null){
								
									while (ok.get(j+"-"+fb_r.get(i)) != null && ok.get(j+"-"+fb_r.get(i)).get(fb_l.get(i)) !=null)
									
										j=TransEInitializer.rand_max(TransEInitializer.entity_num);
								}
								
								train_kb(fb_h.get(i),fb_l.get(i),fb_r.get(i),j,fb_l.get(i),fb_r.get(i));
							}

							TransEInitializer.norm(relation_tmp.get(fb_r.get(i))); 
							TransEInitializer.norm(entity_tmp.get(fb_h.get(i)));
							TransEInitializer.norm(entity_tmp.get(fb_l.get(i)));
							TransEInitializer.norm(entity_tmp.get(j));
	             		}
			            relation_vec = relation_tmp;
			            entity_vec = entity_tmp;
	             	}
	              
	             	
	             	System.out.println("epoch:" + epoch + " " + res);
	             	
	             	//Print the results to a file
	             	if(epoch % 10 == 0){
		             	PrintWriter f2 = new PrintWriter(outputDir+"relation2vec_ep"+epoch + "." + version);
		             	PrintWriter f3 = new PrintWriter(outputDir+"entity2vec_ep"+epoch + "." + version);
		                
		                //write relation vector to file
		                for (int i=0; i<TransEInitializer.relation_num; i++)
		                {
		                	f2.print(TransEInitializer.id2relation.get(i) + "\t" );
		                    
		                	for (int ii=0; ii<n; ii++){
		                        f2.print(df.format(relation_vec.get(i).get(ii))+"\t");
		                	}
		                	f2.println();
		                    
		                }
		                for (int i=0; i<TransEInitializer.entity_num; i++)
		                {
		                	f3.print(TransEInitializer.id2entity.get(i) + "\t" ); 
		                    for (int ii=0; ii<n; ii++){
		                    	f3.print(df.format(entity_vec.get(i).get(ii))+"\t");
		                    }
		                    f3.println(); 
		                }
		                f2.close();
		                f3.close();
	             	}
	            }
	    }
	

		/**
		 * Calculate the distance d(h+l,t)
		 * @param e1
		 * @param e2
		 * @param rel
		 * @return
		 */
	    double calc_sum(int e1,int e2,int rel)
	    {
	        double sum=0;
	        if (TransEInitializer.L1_flag)
	        	//L1 distance d1(e1+rel,e2) = Sum_i |e2_i - (e1_i + rel_i)| = Sum_i |e2_i - e1_i - rel_i)|
	        	for (int ii=0; ii<n; ii++)
	            	sum+=Math.abs(entity_vec.get(e2).get(ii)-entity_vec.get(e1).get(ii)-relation_vec.get(rel).get(ii));
	        else
	        	for (int ii=0; ii<n; ii++)
	            	sum+=TransEInitializer.sqr(entity_vec.get(e2).get(ii)-entity_vec.get(e1).get(ii)-relation_vec.get(rel).get(ii));
	        return sum;
	    }
	    
	    /**
	     * Calculate the gradient and update the embedding vectors
	     * @param e1_a
	     * @param e2_a
	     * @param rel_a
	     * @param e1_b
	     * @param e2_b
	     * @param rel_b
	     */
	    void gradient(int e1_a,int e2_a,int rel_a,int e1_b,int e2_b,int rel_b)
	    {
	        for (int ii=0; ii<n; ii++)
	        {
				//The gradient of ||x||^2_2 = 2x (gadient of L2)
	            double x = 2*(entity_vec.get(e2_a).get(ii)-entity_vec.get(e1_a).get(ii)-relation_vec.get(rel_a).get(ii));
	            //The gradient of ||x||^1_1 = +/- 1 (gadient of L2) 
	            //[L1L1] This norm is not differentiable with respect to a coordinate where that coordinate is zero.
				// Elsewhere, the partial derivatives are just constants, 񵼅 depending on the quadrant
				if (TransEInitializer.L1_flag)
	            	if (x>0)
	            		x=1;
	            	else
	            		x=-1;
	            double relation_a_Update = relation_tmp.get(rel_a).get(ii)-(-1*rate*x);
				relation_tmp.get(rel_a).set(ii,relation_a_Update);
				
	            double e1_a_Update = entity_tmp.get(e1_a).get(ii)-(-1*rate*x);
				entity_tmp.get(e1_a).set(ii,e1_a_Update);
				
	            entity_tmp.get(e2_a).set(ii, entity_tmp.get(e2_a).get(ii)+(-1*rate*x));
	            
	            x = 2*(entity_vec.get(e2_b).get(ii)-entity_vec.get(e1_b).get(ii)-relation_vec.get(rel_b).get(ii));
	            
	            if (TransEInitializer.L1_flag)
	            	if (x>0)
	            		x=1;
	            	else
	            		x=-1;
	            relation_tmp.get(rel_b).set(ii, relation_tmp.get(rel_b).get(ii)-rate*x);
	            entity_tmp.get(e1_b).set(ii,entity_tmp.get(e1_b).get(ii)-rate*x);
	            entity_tmp.get(e2_b).set(ii,entity_tmp.get(e2_b).get(ii) + rate*x);
	        }
	    }
	    
	    /**
	     * Train the model using a positive and a negative triple
	     * @param e1_a
	     * @param e2_a
	     * @param rel_a
	     * @param e1_b
	     * @param e2_b
	     * @param rel_b
	     */
	    void train_kb(int e1_a,int e2_a,int rel_a,int e1_b,int e2_b,int rel_b)
	    {
	        double sum1 = calc_sum(e1_a,e2_a,rel_a); //calculate the distance of the positive triple d1(e1+rel,e2)
	        double sum2 = calc_sum(e1_b,e2_b,rel_b); //calculate the distance of the negative triple d1(e'1+rel,e'2)
	        
			if (sum1+margin>sum2)
	        {
	        	res+=margin+sum1-sum2; // gamma + d1(e1+rel,e2) -  d1(e'1+rel,e'2)
	        	gradient( e1_a, e2_a, rel_a, e1_b, e2_b, rel_b); //update the vectors based on the gradient
	        }
	    }
}
