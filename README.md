# A Java Implenetation of TransE 

TransE [Boredes et al., 2013] is an algorithm for creating low dimensional vector representation, i.e., embeddings for knowledge bases facts. 
The algorithm is based proposes what is called the translation model to generate the embedding. 
Briefly, the translation model is based on the following formula: Given a triple (h,r,t), where h is the head of the fact, r the relation and t is the tail, the following must hold: **h** + **r** = **t** 


We provide a training data from [Wikidata](https://www.wikidata.org/) in the data folder. It contains three files:

* [entities_full.txt](data/entities_full.txt): includes a list of Wikidata entity IDs
* [relations_full.txt](data/relations_full.txt) :  includes a list of Wikidata properties (relations) IDs
* [train_full.txt](data/train_full.txt): includes a list of Wikidata triples, where the first column corresponds to h, the second to t and the third to r.

This implementation is based on the C++ implmentation which is available [here](https://github.com/Mrlyk423/Relation_Extraction).
## References

[Boredes et al., 2013] Bordes, A.; Usunier, N.; Garcia-Duran, A.; Weston, J.; and Yakhnenko, O. 2013. Translating embeddings for modeling multi-relational data. In Proceedings of NIPS , 2787–2795.
