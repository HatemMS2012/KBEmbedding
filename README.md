# A Java Implenetation of TransE 

TransE [Boredes et al., 2013] is an algorithm for embedding knowledge bases facts. Given a fact as a triple (h,r,t), where h corresponds to the head entity, r is the relation and t is the tail entity, the model learns embedding for each element in the same space. 
The main idea is to represent each element of the fact as vector and learn those vectors that satisfy: h+r = t



[Boredes et al., 2013] Bordes, A.; Usunier, N.; Garcia-Duran, A.; Weston, J.; and Yakhnenko, O. 2013. Translating embeddings for modeling multi-relational data. In Proceedings of NIPS , 2787–2795.
