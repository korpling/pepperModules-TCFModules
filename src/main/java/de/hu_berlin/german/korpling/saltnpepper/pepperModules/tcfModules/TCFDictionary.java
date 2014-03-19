package de.hu_berlin.german.korpling.saltnpepper.pepperModules.tcfModules;

public interface TCFDictionary{
	/** constant processing instruction **/
	public static final String TCF_PI = "xml-model href=\"http://de.clarin.eu/images/weblicht-tutorials/resources/tcf-04/schemas/latest/d-spin_0_4.rnc\" type=\"application/relax-ng-compact-syntax\"";
	/** constant to address the xml-namespace prefix'tc'. **/
	public static final String NS_TC= "tc";
	/** constant to address the xml-namespace 'http://www.dspin.de/data/textcorpus'. **/
	public static final String NS_VALUE_TC= "http://www.dspin.de/data/textcorpus";
	/** constant to address the xml-namespace prefix'ed'. **/
	public static final String NS_ED= "ed";
	/** constant to address the xml-namespace 'http://www.dspin.de/data/extdata'. **/
	public static final String NS_VALUE_ED= "http://www.dspin.de/data/extdata";
	/** constant to address the xml-namespace prefix'xsi'. **/
	public static final String NS_XSI= "xsi";
	/** constant to address the xml-namespace 'http://www.w3.org/2001/XMLSchema-instance'. **/
	public static final String NS_VALUE_XSI= "http://www.w3.org/2001/XMLSchema-instance";
	/** constant to address the xml-namespace prefix'md'. **/
	public static final String NS_MD= "md";
	/** constant to address the xml-namespace 'http://www.dspin.de/data/metadata'. **/
	public static final String NS_VALUE_MD= "http://www.dspin.de/data/metadata";
	/** constant to address the xml-namespace prefix'lx'. **/
	public static final String NS_LX= "lx";
	/** constant to address the xml-namespace 'http://www.dspin.de/data/lexicon'. **/
	public static final String NS_VALUE_LX= "http://www.dspin.de/data/lexicon";
	/** constant to address the xml-namespace prefix'wl'. **/
	public static final String NS_WL= "wl";
	/** constant to address the xml-namespace 'http://www.dspin.de/data'. **/
	public static final String NS_VALUE_WL= "http://www.dspin.de/data";

	/** constant to address the xml-element 'tc:tokens'. **/
	public static final String TAG_TC_TOKENS= "tokens";
	/** constant to address the xml-element 'PID'. **/
	public static final String TAG_PID= "PID";
	/** constant to address the xml-element 'ResourceRelationList'. **/
	public static final String TAG_RESOURCERELATIONLIST= "ResourceRelationList";
	/** constant to address the xml-element 'JournalFileProxyList'. **/
	public static final String TAG_JOURNALFILEPROXYLIST= "JournalFileProxyList";
	/** constant to address the xml-element 'wl:D-Spin'. **/
	public static final String TAG_WL_D_SPIN= "D-Spin";
	/** constant to address the xml-element 'ResourceName'. **/
	public static final String TAG_RESOURCENAME= "ResourceName";
	/** constant to address the xml-element 'Descriptions'. **/
	public static final String TAG_DESCRIPTIONS= "Descriptions";
	/** constant to address the xml-element 'tc:parse'. **/
	public static final String TAG_TC_PARSE= "parse";
	/** constant to address the xml-element 'tc:parsing'. **/
	public static final String TAG_TC_PARSING= "parsing";
	/** constant to adress the xml-element 'tc:depparsing'. **/
	public static final String TAG_TC_DEPPARSING= "depparsing";
	/** constant to adress the xml-element 'tc:dependency'. **/
	public static final String TAG_TC_DEPENDENCY= "dependency";	
	/** constant to address the xml-element 'GeneralInfo'. **/
	public static final String TAG_GENERALINFO= "GeneralInfo";
	/** constant to address the xml-element 'CMD'. **/
	public static final String TAG_CMD= "CMD";
	/** constant to address the xml-element 'ResourceClass'. **/
	public static final String TAG_RESOURCECLASS= "ResourceClass";
	/** constant to address the xml-element 'Header'. **/
	public static final String TAG_HEADER= "Header";
	/** constant to address the xml-element 'tc:constituent'. **/
	public static final String TAG_TC_CONSTITUENT= "constituent";
	/** constant to address the xml-element 'tc:sentences'. **/
	public static final String TAG_TC_SENTENCES= "sentences";
	/** constant to address the xml-element 'Parameter'. **/
	public static final String TAG_PARAMETER= "Parameter";
	/** constant to address the xml-element 'md:MetaData'. **/
	public static final String TAG_MD_METADATA= "MetaData";
	/** constant to address the xml-element 'Description'. **/
	public static final String TAG_DESCRIPTION= "Description";
	/** constant to address the xml-element 'tc:TextCorpus'. **/
	public static final String TAG_TC_TEXTCORPUS= "TextCorpus";
	/** constant to address the xml-element 'tc:lemma'. **/
	public static final String TAG_TC_LEMMA= "lemma";
	/** constant to address the xml-element 'ToolInChain'. **/
	public static final String TAG_TOOLINCHAIN= "ToolInChain";
	/** constant to address the xml-element 'tc:text'. **/
	public static final String TAG_TC_TEXT= "text";
	/** constant to address the xml-element 'ResourceProxyList'. **/
	public static final String TAG_RESOURCEPROXYLIST= "ResourceProxyList";
	/** constant to address the xml-element 'tc:token'. **/
	public static final String TAG_TC_TOKEN= "token";
	/** constant to address the xml-element 'Components'. **/
	public static final String TAG_COMPONENTS= "Components";
	/** constant to address the xml-element 'WebServiceToolChain'. **/
	public static final String TAG_WEBSERVICETOOLCHAIN= "WebServiceToolChain";
	/** constant to address the xml-element 'tc:lemmas'. **/
	public static final String TAG_TC_LEMMAS= "lemmas";
	/** constant to address the xml-element 'tc:sentence'. **/
	public static final String TAG_TC_SENTENCE= "sentence";
	/** constant to address the xml-element 'md:Services'. **/
	public static final String TAG_MD_SERVICES= "Services";
	/** constant to address the xml-element 'Toolchain'. **/
	public static final String TAG_TOOLCHAIN= "Toolchain";
	/** constant to address the xml-element 'tc:tag'. **/
	public static final String TAG_TC_TAG= "tag";
	/** constant to address the xml-element 'tc:POStags'. **/
	public static final String TAG_TC_POSTAGS= "POStags";
	/** constant to address the xml-element 'Resources'. **/
	public static final String TAG_RESOURCES= "Resources";
	/** constant to address the xml-element 'morphology'. **/
	public static final String TAG_TC_MORPHOLOGY = "morphology";
	/** constant to address the xml-element 'analysis'. **/
	public static final String TAG_TC_ANALYSIS = "analysis";
	/** constant to address the xml-element 'fs'. **/
	public static final String TAG_TC_FS = "fs";
	/** constant to address the xml-element 'f'. **/
	public static final String TAG_TC_F = "f";
	/** constant to address the xml-element 'segmentation'. **/
	public static final String TAG_TC_SEGMENTATION = "segmentation";
	/** constant to address the xml-element 'segment'. **/
	public static final String TAG_TC_SEGMENT = "segment";			
	/** constant to address the xml-element 'references'. **/
	public static final String TAG_TC_REFERENCES = "references";			
	/** constant to address the xml-element 'reference'. **/
	public static final String TAG_TC_REFERENCE = "reference";			
	/** constant to address the xml-element 'entity'. **/
	public static final String TAG_TC_ENTITY = "entity";

	/** constant to address the xml-attribute 'cat'. **/
	public static final String ATT_CAT= "cat";
	/** constant to address the xml-attribute 'CMDVersion'. **/
	public static final String ATT_CMDVERSION= "CMDVersion";
	/** constant to address the xml-attribute 'tagset'. **/
	public static final String ATT_TAGSET= "tagset";
	/** constant to address the xml-attribute 'name'. **/
	public static final String ATT_NAME= "name";
	/** constant to address the xml-attribute 'ID'. **/
	public static final String ATT_ID= "ID";
	/** constant to address the xml-attribute 'value'. **/
	public static final String ATT_VALUE= "value";
	/** constant to address the xml-attribute 'xmlns'. **/
	public static final String ATT_XMLNS= "xmlns";
	/** constant to address the xml-attribute 'tokenIDs'. **/
	public static final String ATT_TOKENIDS= "tokenIDs";
	/** constant to address the xml-attribute 'lang'. **/
	public static final String ATT_LANG= "lang";
	/** constant to address the xml-attribute 'xsi:schemaLocation'. **/
	public static final String ATT_XSI_SCHEMALOCATION= "schemaLocation";
	/** constant to address the xml-attribute 'version'. **/
	public static final String ATT_VERSION= "version";
	/** constant to adress the xml-attribute 'emptytoks'. **/
	public static final String ATT_EMPTYTOKS = "emptytoks";
	/** constant to adress the xml-attribute 'multigovs'. **/
	public static final String ATT_MULTIGOVS = "multigovs";
	/** constant to adress the xml-attribute 'govIDs'. **/
	public static final String ATT_GOVIDS = "govIDs";
	/** constant to adress the xml-attribute 'depIDs'. **/
	public static final String ATT_DEPIDS = "depIDs";
	/** constant to adress the xml-attribute 'func'. **/
	public static final String ATT_FUNC = "func";
	/** constant to adress the xml-attribute 'type'. **/
	public static final String ATT_TYPE = "type";
	/** constant to adress the xml-attribute 'mintokIDs'. **/
	public static final String ATT_MINTOKIDS = "mintokIDs";
	/** constant to adress the xml-attribute 'rel'. **/
	public static final String ATT_REL = "rel";
	/** constant to adress the xml-attribute 'target'. **/
	public static final String ATT_TARGET = "target";
	/** constant to adress the xml-attribute 'typetagset'. **/
	public static final String ATT_TYPETAGSET = "typetagset";
	/** constant to adress the xml-attribute 'reltagset'. **/
	public static final String ATT_RELTAGSET = "reltagset";
}
