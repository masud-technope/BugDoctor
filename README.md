# BugDoctor: Supporting Source Code Search with Context-Aware, Semantics-Driven Query Reformulation


**Mohammad Masudur Rahman**, PhD Candidate

Advised by: **Chanchal K. Roy**, PhD

Department of Computer Science, University of Saskatchewan, Canada


**Abstract**: Software bugs and failures cost trillions of dollars every year, and could even lead to massive fatalities (e.g., Therac-25). During maintenance, software developers fix numerous bugs and implement hundreds of new features by making necessary changes to the existing software code. Once an issue report (e.g., bug report, change request) is assigned to a developer, she chooses a few important keywords from the report as a search query, and then attempts to find out the exact locations in the software code that need to be either repaired or enhanced. As a part of this maintenance, developers also often select ad hoc queries on the fly, and attempt to locate the reusable code from the Internet that could assist them either in bug fixing or in feature implementation. Unfortunately, even the experienced developers often fail to construct the right search queries. Even if the developers come up with a few ad hoc queries, most of them require frequent modifications which cost significant development time and efforts. Thus, construction of an appropriate query for localizing the software bugs, programming concepts or even the reusable code is a major challenge. In this thesis, we overcome this query construction challenge with six studies, and develop a novel, effective code search solution (BugDoctor) that assists the developers in localizing the software code of interest (e.g., bugs, concepts and reusable code) during software maintenance. In particular, we reformulate a given search query (1) by designing novel keyword selection algorithms (e.g., CodeRank) that outperform the traditional alternatives (e.g., TF-IDF), (2) by leveraging the bug report quality paradigm and source document structures which were previously overlooked and (3) by exploiting the crowd knowledge and word semantics derived from Stack Overflow Q\&A site, which were previously untapped. Our experiment using 5000+ search queries (bug reports, change requests, and ad hoc queries) suggests that our proposed approach can improve the given queries significantly through automated query reformulations. Comparison with 10+ existing studies on bug localization, concept location and Internet-scale code search suggests that our approach can outperform the state-of-the-art approaches with a significant margin. 

Components of BugDoctor
--------------------------------
- [**STRICT**](https://github.com/masud-technope/STRICT-Replication-Package): Search Query Reformulation for Concept Location using Graph-Based Term Weighting
- [**ACER**](https://github.com/masud-technope/ACER-Replication-Package-ASE2017): Search Query Reformulation for Concept Location using CodeRank and Source Document Structures
- [**BLIZZARD**](https://github.com/masud-technope/BLIZZARD-Replication-Package-ESEC-FSE2018): Search Query Reformulation for Bug Localization using Report Quality Dynamics & Graph-Based Term Weighting
- [**BLADER**](https://github.com/masud-technope/BLADER-ICSE2019-Replication-Package): Search Query Reformulation for Bug Localization using Word Semantics & Clustering Tendency Analysis
- [**RACK**](https://github.com/masud-technope/RACK-Replication-Package): Search Query Reformulation for Internet-scale Code Search using Crowdsourced Knowledge
- [**NLP2API**](https://github.com/masud-technope/NLP2API-Replication-Package): Search Query Reformulation for Internet-scale Code Search using Word Semantics


Please cite our work as
------------------------------------------
```
@INPROCEEDINGS{icse2019masud,
	author={Rahman, M. M.},
	booktitle={Proc. ICSE-C},
	title={Supporting Code Search with Context-Aware, Analytics-Driven, Effective Query Reformulation},
	year={2019},
	pages={226--229}
}
```
**Download this paper** [<img src="http://homepage.usask.ca/~masud.rahman/img/pdf.png"
     alt="PDF" heigh="16px" width="16px" />](http://homepage.usask.ca/~masud.rahman/papers/masud-ICSE2019-pp.pdf)


Something not working as expected?
------------------------------------------------------------------------
Contact: **Masud Rahman** (masud.rahman@usask.ca)

OR

Create an issue from [here](https://github.com/masud-technope/BugDoctor/issues/new)





