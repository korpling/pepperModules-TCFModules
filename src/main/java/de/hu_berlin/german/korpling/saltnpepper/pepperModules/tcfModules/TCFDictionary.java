/**
 * Copyright 2009 Humboldt University of Berlin, INRIA.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 */
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
	/** constant to address the xml-element 'ResourceTitle'. **/
	public static final String TAG_RESOURCETITLE= "ResourceTitle";
	/** constant to address the xml-element 'Version'. **/
	public static final String TAG_VERSION= "Version";
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
	/** constant to address the xml-element 'namedEntities' **/
	public static final String TAG_TC_NAMEDENTITIES = "namedEntities";
	/** constant to address the xml-element 'phonetics' **/
	public static final String TAG_TC_PHONETICS = "phonetics";
	/** constant to address the xml-element 'pron' **/
	public static final String TAG_TC_PRON = "pron";
	/** constant to address the xml-element 'orthography' **/
	public static final String TAG_TC_ORTHOGRAPHY = "orthography";
	/** constant to address the xml-element 'correction' **/
	public static final String TAG_TC_CORRECTION = "correction";
	/** constant to address the xml-element 'textstructure' **/
	public static final String TAG_TC_TEXTSTRUCTURE = "textstructure";
	/** constant to address the xml-element 'textspan' **/
	public static final String TAG_TC_TEXTSPAN = "textspan";
	/** constant to address the xml-element 'geo' **/
	public static final String TAG_TC_GEO = "geo";
	/** constant to address the xml-element 'src' **/
	public static final String TAG_TC_SRC = "src";
	/** constant to address the xml-element 'gpoint' **/
	public static final String TAG_TC_GPOINT = "gpoint";
	/** constant to address the xml-element 'synonymy' **/
	public static final String TAG_TC_SYNONYMY = "synonymy";
	/** constant to address the xml-element 'antonymy' **/
	public static final String TAG_TC_ANTONYMY = "antonymy";
	/** constant to address the xml-element 'hyponymy' **/
	public static final String TAG_TC_HYPONYMY = "hyponymy";
	/** constant to address the xml-element 'hyperonymy' **/
	public static final String TAG_TC_HYPERONYMY = "hyperonymy";
	/** constant to address the xml-element 'orthform' **/
	public static final String TAG_TC_ORTHFORM = "orthform";
	/** constant to address the xml-element 'wsd' **/
	public static final String TAG_TC_WSD = "wsd";
	/** constant to address the xml-element 'ws' **/
	public static final String TAG_TC_WS = "ws";
	/** constant to address the xml-element 'WordSplittings' **/
	public static final String TAG_TC_WORDSPLITTINGS = "WordSplittings";
	/** constant to address the xml-element 'split' **/
	public static final String TAG_TC_SPLIT = "split";
	/** constant to address the xml-element 'discourseconnectives' **/
	public static final String TAG_TC_DISCOURSECONNECTIVES = "discourseconnectives";
	/** constant to address the xml-element 'discourseconnectives' **/
	public static final String TAG_TC_CONNECTIVE = "connective";
	/** constant to address the xml-element 'MdCreator' **/
	public static final String TAG_MDCREATOR = "MdCreator";
	/** constant to address the xml-element 'MdCreationDate' **/
	public static final String TAG_MDCREATIONDATE = "MdCreationDate";
	/** constant to address the xml-element 'MdSelfLink' **/
	public static final String TAG_MDSELFLINK = "MdSelfLink";
	/** constant to address the xml-element 'MdProfile' **/
	public static final String TAG_MDPROFILE = "MdProfile";
	/** constant to address the xml-element 'MdCollectionDisplayName' **/
	public static final String TAG_MDCOLLECTIONDISPLAYNAME = "MdCollectionDisplayName";
	/** constant to address the xml-element 'ResourceProxy' **/
	public static final String TAG_RESOURCEPROXY = "ResourceProxy";
	/** constant to address the xml-element 'ResourceType' **/
	public static final String TAG_RESOURCETYPE = "ResourceType";
	/** constant to address the xml-element 'ResourceRef' **/
	public static final String TAG_RESOURCEREF = "ResourceRef";
	/** constant to address the xml-element 'JournalFileProxy' **/
	public static final String TAG_JOURNALFILEPROXY = "JournalFileProxy";
	/** constant to address the xml-element 'JournalFileRef' **/
	public static final String TAG_JOURNALFILEREF = "JournalFileRef";
	/** constant to address the xml-element 'ResourceRelation' **/
	public static final String TAG_RESOURCERELATION = "ResourceRelation";
	/** constant to address the xml-element 'RelationType' **/
	public static final String TAG_RELATIONTYPE = "RelationType";
	/** constant to address the xml-element 'Res1' **/
	public static final String TAG_RES1 = "Res1";
	/** constant to address the xml-element 'Res2' **/
	public static final String TAG_RES2 = "Res2";
	/** constant to address the xml-element 'IsPartOfList' **/
	public static final String TAG_ISPARTOFLIST = "IsPartOfList";
	/** constant to address the xml-element 'IsPartOf' **/
	public static final String TAG_ISPARTOF = "IsPartOf";
	/** constant to address the xml-element 'LifeCycleStatus'. **/
	public static final String TAG_LIFECYCLESTATUS= "LifeCycleStatus";
	/** constant to address the xml-element 'StartYear'. **/
	public static final String TAG_STARTYEAR= "StartYear";
	/** constant to address the xml-element 'CompletionYear'. **/
	public static final String TAG_COMPLETIONYEAR= "CompletionYear";
	/** constant to address the xml-element 'PublicationDate'. **/
	public static final String TAG_PUBLICATIONDATE= "PublicationDate";
	/** constant to address the xml-element 'LastUpdate'. **/
	public static final String TAG_LASTUPDATE= "LastUpdate";
	/** constant to address the xml-element 'TimeCoverage'. **/
	public static final String TAG_TIMECOVERAGE= "TimeCoverage";
	/** constant to address the xml-element 'LegalOwner'. **/
	public static final String TAG_LEGALOWNER= "LegalOwner";
	/** constant to address the xml-element 'Genre'. **/
	public static final String TAG_GENRE= "Genre";
	/** constant to address the xml-element 'Location'. **/
	public static final String TAG_LOCATION= "Location";
	/** constant to address the xml-element 'Adress'. **/
	public static final String TAG_ADDRESS= "Adress";
	/** constant to address the xml-element 'Region'. **/
	public static final String TAG_REGION= "Region";
	/** constant to address the xml-element 'ContinentName'. **/
	public static final String TAG_CONTINENTNAME= "ContinentName";
	/** constant to address the xml-element 'Country'. **/
	public static final String TAG_COUNTRY= "Country";
	/** constant to address the xml-element 'CountryName'. **/
	public static final String TAG_COUNTRYNAME= "CountryName";
	/** constant to address the xml-element 'CountryCoding'. **/
	public static final String TAG_COUNTRYCODING= "CountryCoding";
	/** constant to address the xml-element 'tags'. **/
	public static final String TAG_TAGS= "tags";
	/** constant to address the xml-element 'tag'. **/
	public static final String TAG_TAG= "tag";
	
	/** constant to address the xml-attribute 'ref'. **/
	public static final String ATT_REF= "ref";
	/** constant to address the xml-attribute 'ComponentId'. **/
	public static final String ATT_COMPONENTID= "ComponentId";
	/** constant to address the xml-attribute 'cat'. **/
	public static final String ATT_CAT= "cat";
	/** constant to address the xml-attribute 'mimetype'. **/
	public static final String ATT_MIMETYPE= "mimetype";
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
	/** constant to address the xml-attribute 'tokID'. **/
	public static final String ATT_TOKID= "tokID";
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
	/** constant to adress the xml-attribute 'class'. **/
	public static final String ATT_CLASS = "class";
	/** constant to adress the xml-attribute 'transcription'. **/
	public static final String ATT_TRANSCRIPTION = "transcription";
	/** constant to adress the xml-attribute 'operation'. **/
	public static final String ATT_OPERATION = "operation";
	/** constant to adress the xml-attribute 'start'. **/
	public static final String ATT_START = "start";
	/** constant to adress the xml-attribute 'end'. **/
	public static final String ATT_END = "end";
	/** constant to adress the xml-attribute 'alt'. **/
	public static final String ATT_ALT = "alt";
	/** constant to adress the xml-attribute 'lat'. **/
	public static final String ATT_LAT = "lat";
	/** constant to adress the xml-attribute 'lon'. **/
	public static final String ATT_LON = "lon";
	/** constant to adress the xml-attribute 'continent'. **/
	public static final String ATT_CONTINENT = "continent";
	/** constant to adress the xml-attribute 'country'. **/
	public static final String ATT_COUNTRY = "country";
	/** constant to adress the xml-attribute 'capital'. **/
	public static final String ATT_CAPITAL = "capital";
	/** constant to adress the xml-attribute 'coordFormat'. **/
	public static final String ATT_COORDFORMAT = "coordFormat";
	/** constant to adress the xml-attribute 'continentFormat'. **/
	public static final String ATT_CONTINENTFORMAT = "continentFormat";
	/** constant to adress the xml-attribute 'countryFormat'. **/
	public static final String ATT_COUNTRYFORMAT = "countryFormat";
	/** constant to adress the xml-attribute 'capitalFormat'. **/
	public static final String ATT_CAPITALFORMAT = "capitalFormat";
	/** constant to adress the xml-attribute 'lemmaRefs'. **/
	public static final String ATT_LEMMAREFS = "lemmaRefs";
	/** constant to adress the xml-attribute 'lexunits'. **/
	public static final String ATT_LEXUNITS = "lexunits";
	/** constant to adress the xml-attribute 'src'. **/
	public static final String ATT_SRC = "src";
	/** constant to adress the xml-attribute 'comment'. **/
	public static final String ATT_COMMENT = "comment";
}
