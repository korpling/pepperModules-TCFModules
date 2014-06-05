package de.hu_berlin.german.korpling.saltnpepper.pepperModules.tcfModules;

import java.util.Stack;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.BasicEMap;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.EMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DefaultHandler2;

import de.hu_berlin.german.korpling.saltnpepper.pepper.common.DOCUMENT_STATUS;
import de.hu_berlin.german.korpling.saltnpepper.pepper.modules.impl.PepperMapperImpl;
import de.hu_berlin.german.korpling.saltnpepper.salt.SaltFactory;
import de.hu_berlin.german.korpling.saltnpepper.salt.graph.Label;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDocumentGraph;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SPointingRelation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SSpan;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SStructure;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.STYPE_NAME;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.STextualDS;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SToken;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SAnnotation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SLayer;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SMetaAnnotation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SNode;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltSemantics.SLemmaAnnotation;

public class TCFMapperImport extends PepperMapperImpl{
//CHECK-ASK: might it be better to implement and extension of PepperMapperImpl here?	
	
	private EMap<String, SNode> sNodes;
	/* TODO CHECK what do I need labels<> for? */
	private EMap<String, Label> labels;
	private EMap<String, SLayer> sLayers;
	
	/* TODO CHECK rethink organisation of constant strings */
	public static final String LEVEL_SENTENCE = "sentence";
	public static final String LAYER_POS = "pos";
	public static final String LAYER_DEPENDENCIES = "dependencies";
	public static final String LEVEL_DEPENDENCY = "dependency";
	public static final String LAYER_TCF_MORPHOLOGY = "tcfMorphology";
	public static final String LAYER_CONSTITUENTS = "syntax";
	public static final String LAYER_LEMMA = "lemma";
	public static final String LAYER_REFERENCES = "references";
	public static final String LAYER_NE = "named entities";
	public static final String LAYER_PHONETICS = "phonetics";
	public static final String LAYER_ORTHOGRAPHY = "orthography";
	public static final String LAYER_GEO = "geography";
	public static final String LAYER_LS = "lexical-semantics";
	public static final String LAYER_WORDSENSE = "wordSenseDisambiguation";
	public static final String LAYER_SPLITTINGS = "wordSplittings";
	public static final String LAYER_DISCOURSE = "discourseConnectives";
	public static final String LAYER_TEXTSTRUCTURE = "textstructure";
	public static final String LAYER_SENTENCES = "sentences";
	public static final String STYPE_REFERENCE = "reference";
	public static final String STYPE_DEPENDENCY = "dependency";
	
	public static final String ANNO_NAME_CONSTITUENT = "const";
	
	public static final String HEAD_MARKER = "refHead";//is annotation key and value for head annotation (online one instance for all necessary)
	public static final String SPAN = "span";//marking element for span ids over single tokens	
	
	private static final boolean DEBUG = false;
	private static Logger logger = LoggerFactory.getLogger(TCFMapperImport.class);
	
	@Override
	public DOCUMENT_STATUS mapSDocument() {
		sNodes = new BasicEMap<String, SNode>();
		labels = new BasicEMap<String, Label>();
		sLayers = new BasicEMap<String, SLayer>();
		if(getSDocument()==null){
			setSDocument(SaltFactory.eINSTANCE.createSDocument());
		}
		SDocumentGraph docGraph = SaltFactory.eINSTANCE.createSDocumentGraph();		
		getSDocument().setSDocumentGraph(docGraph);
		TCFReader reader = new TCFReader();
		this.readXMLResource(reader, getResourceURI());
//		getSDocument().createSMetaAnnotation(null, "document", getSDocument().getSName());
		return(DOCUMENT_STATUS.COMPLETED);
	}
	
	private class TCFReader extends DefaultHandler2 implements TCFDictionary {
		private STextualDS currentSTDS;
		private int p;
		private Stack<String> path;	
		private Stack<String> idPath;
		private StringBuilder chars;
		private String currentNodeID;		
		private String currentAnnoID;
		private String currentAnnoKey;
		private SNode currentSNode;
		/* head annotation */
		private SAnnotation head;
		/* internal id */
		private int id = 0;
		private static final String REF_PREFIX = "reference-";
		/* properties */
		private boolean shrinkTokenAnnotations;
		private boolean useCommonAnnotatedElement;
		/* other booleans */
		private boolean ignoreIds;
		private EMap<String, String> decode;
		
		public TCFReader(){
			super();
			path = new Stack<String>();
			idPath = new Stack<String>();
			chars = new StringBuilder();
			currentNodeID = null;
			currentSNode = null;
			currentAnnoID = null;
			currentAnnoKey = null;
			head = SaltFactory.eINSTANCE.createSAnnotation();
			head.setSName(TCFMapperImport.HEAD_MARKER);
			head.setSValue(TCFMapperImport.HEAD_MARKER);
			p = 0;			
			shrinkTokenAnnotations = ((TCFImporterProperties)getProperties()).isShrinkTokenAnnotation();
			useCommonAnnotatedElement = ((TCFImporterProperties)getProperties()).isUseCommonAnnotatedElement();
			logger.info(shrinkTokenAnnotations ? "shrinkTokenAnnotations=true" : "shrinkTokenAnnotations=false - import might take a little longer");
			ignoreIds = false;
			id = 0;		
		}
		
		@Override
		public void startElement(	String uri,
				String localName,
				String qName,
				Attributes attributes)throws SAXException
		{
			localName = qName.substring(qName.lastIndexOf(":")+1);
			path.push(localName);
			if(DEBUG){System.out.println("<"+localName+">");}
			if (TAG_TC_TOKENS.equals(localName)){				
			}
			else if (TAG_MDCREATOR.equals(localName)){
				chars.delete(0, chars.length());
			}
			else if (TAG_MDCREATIONDATE.equals(localName)){
				chars.delete(0, chars.length());
			}
			else if (TAG_MDSELFLINK.equals(localName)){
				chars.delete(0, chars.length());
			}
			else if (TAG_MDPROFILE.equals(localName)){
				chars.delete(0, chars.length());
			}
			else if (TAG_MDCOLLECTIONDISPLAYNAME.equals(localName)){
				chars.delete(0, chars.length());
			}
			else if (TAG_PID.equals(localName)){
			}
			else if (TAG_RESOURCERELATIONLIST.equals(localName)){
			}
			else if (TAG_JOURNALFILEPROXYLIST.equals(localName)){
			}
			else if (TAG_WL_D_SPIN.equals(localName)){
			}
			else if (TAG_RESOURCENAME.equals(localName)){
			}
			else if (TAG_DESCRIPTIONS.equals(localName)){
			}
			else if (TAG_TC_PARSING.equals(localName)){
				currentNodeID = null;
				ignoreIds = false;
				SLayer syntaxLayer = buildLayer(LAYER_CONSTITUENTS);
				syntaxLayer.createSAnnotation(null, ATT_TAGSET, attributes.getValue(ATT_TAGSET));
			}
			else if (TAG_GENERALINFO.equals(localName)){
				chars.delete(0, chars.length());
			}
			else if (TAG_CMD.equals(localName)){
				String att = attributes.getValue(ATT_CMDVERSION); 
				if(att!=null){getSDocument().createSMetaAnnotation(null, ATT_CMDVERSION, att);}
			}
			else if (TAG_RESOURCECLASS.equals(localName)){
			}
			else if (TAG_HEADER.equals(localName)){
			}
			else if (TAG_TC_CONSTITUENT.equals(localName)){
				String constID = attributes.getValue(ATT_ID);
				if(!ignoreIds){ignoreIds = (constID==null);}
				if(ignoreIds){constID = Integer.toString(id++);}
				/* are we dealing with a potential SToken (sequence) or a potential SStructure? */
				if(DEBUG){System.out.println("startElement().localName=constituent idPath sais: "+idPath);}
				String tokenIDs = attributes.getValue(ATT_TOKENIDS);
				if(tokenIDs==null){
					/* SStructure */					
					SStructure sStruc = SaltFactory.eINSTANCE.createSStructure();
					sStruc.createSAnnotation(LAYER_CONSTITUENTS, ATT_CAT, attributes.getValue(ATT_CAT));
					sNodes.put(constID, sStruc);
					sLayers.get(LAYER_CONSTITUENTS).getSNodes().add(sStruc);
					if(idPath.empty()){						
						/* sStruc is root */
						if(DEBUG){System.out.println("Building node: "+sStruc.getSAnnotation(ATT_CAT).getSValue()+" as root");}
						getSDocGraph().addSNode(sStruc);						
					}
					else{
						if(DEBUG){
							System.out.println("idPath.peek()="+idPath.peek());
							System.out.println("Building node: "+sStruc.getSAnnotation(ATT_CAT).getSValue()+"-"+constID.replace("c", "")+" and appending it to node "+sNodes.get(idPath.peek()).getSAnnotation(ATT_CAT).getValueString()+"-"+idPath.peek().replace("c", ""));
						}
						getSDocGraph().addSNode(sNodes.get(idPath.peek()), sStruc, STYPE_NAME.SDOMINANCE_RELATION);						
					}					
					if(DEBUG){System.out.println("Pushing to stack: "+attributes.getValue(ATT_CAT));}
					idPath.push(constID);					
				}
				else{
					/* tokens/spans */
					SNode sNode = sNodes.get(tokenIDs);
					if(tokenIDs.contains(" ")){
						/* span */
						if(sNode==null){
							if(DEBUG){System.out.println("Span does not exist. I build it.");}
							String[] seq = tokenIDs.split(" ");
							EList<SToken> sTokensForSpan = new BasicEList<SToken>();
							for(int i=0; i<seq.length; i++){
								sTokensForSpan.add((SToken)sNodes.get(seq[i]));							
							}
							sNode = getSDocGraph().createSSpan(sTokensForSpan);
							if(useCommonAnnotatedElement){sNodes.put(tokenIDs, sNode);}// store node, if spans should be reused
							if(DEBUG){System.out.println("Building span: \""+getSDocGraph().getSText(sNode)+"\" and appending it to node "+sNodes.get(idPath.peek()).getSAnnotation(ATT_CAT).getValueString()+"-"+idPath.peek().replace("c", ""));}
						}						
						sNode.createSAnnotation(LAYER_CONSTITUENTS, ATT_CAT, attributes.getValue(ATT_CAT));
						getSDocGraph().addSNode(sNodes.get(idPath.peek()), sNode, STYPE_NAME.SDOMINANCE_RELATION);
						/*we HAVE TO push also tokens/spans onto the stack to avoid that at the end of their xml-element the wrong constituent is popped off the stack*/
						idPath.push(tokenIDs);
					}
					else{
						/* single token */						
						/*TODO ask yourself, if a single token can be the direct root not governed by a phrase*/						
						if(shrinkTokenAnnotations){
							sNode = (SToken)sNodes.get(tokenIDs);
						}
						else{
							sNode = sNodes.get(tokenIDs+SPAN);
							if(sNode==null){								
								sNode = getSDocGraph().createSSpan((SToken)sNodes.get(tokenIDs));
								if(DEBUG){System.out.println("Building span over single token \""+getSDocGraph().getSText(sNode)+"\"");}
								if(useCommonAnnotatedElement){sNodes.put(tokenIDs+SPAN, sNode);}// store node, if spans should be reused
							}							
						}
//						if(sNode==null){sNode = getSDocGraph().createSSpan((SToken)sNodes.get(tokenIDs));} //when should this ever get null?!
						sNode.createSAnnotation(LAYER_CONSTITUENTS, ATT_CAT, attributes.getValue(ATT_CAT));//TODO annotation might already exist, we definitely have to use namespaces!
						getSDocGraph().addSNode(sNodes.get(idPath.peek()), sNode, STYPE_NAME.SDOMINANCE_RELATION);
						if(DEBUG){System.out.println("Taking token:"+getSDocGraph().getSText(sNode)+" and appending it to node "+sNodes.get(idPath.peek()).getSAnnotation(ATT_CAT).getValueString()+"-"+idPath.peek().replace("c", ""));}						
						/*we HAVE TO push also tokens onto the stack to avoid that at the end of their xml-element the wrong constituent is popped off the stack*/
						/*so the pushed id is actually just a dummy -> we don't have to check, if the span should be pushed*/
						idPath.push(tokenIDs);
					}
				}
			}
			else if(TAG_TC_DEPPARSING.equals(localName)){
				currentNodeID = null;
				SLayer depLayer = buildLayer(LAYER_DEPENDENCIES);
				/* TODO the same has to be done in SaltSample, still undone */
				/* TODO the same has to be done for POS both in SaltSample(CHECK) and here */
				depLayer.createSMetaAnnotation(null, TCFDictionary.ATT_TAGSET, attributes.getValue(TCFDictionary.ATT_TAGSET));
				depLayer.createSMetaAnnotation(null, TCFDictionary.ATT_EMPTYTOKS, attributes.getValue(TCFDictionary.ATT_EMPTYTOKS));
				depLayer.createSMetaAnnotation(null, TCFDictionary.ATT_MULTIGOVS, attributes.getValue(TCFDictionary.ATT_MULTIGOVS));
			}
			else if(TAG_TC_PARSE.equals(localName)){
				idPath.clear(); //relevant for constituent parsing	
				if(DEBUG){System.out.println("Stack is empty? "+Boolean.toString(idPath.empty()));}
			}
			else if(TAG_TC_DEPENDENCY.equals(localName)){
				/* is there no governing ID, we skip, because we don't use a root node */
				SDocumentGraph graph = getSDocGraph();
				if(attributes.getValue(ATT_GOVIDS)!=null){					
					SPointingRelation depRel = (SPointingRelation)graph.addSNode(sNodes.get(attributes.getValue(ATT_GOVIDS)), sNodes.get(attributes.getValue(ATT_DEPIDS)), STYPE_NAME.SPOINTING_RELATION);
					depRel.createSAnnotation(LAYER_DEPENDENCIES, ATT_FUNC, attributes.getValue(ATT_FUNC)); //TODO write into documentation, how I use namespaces
					sLayers.get(LAYER_DEPENDENCIES).getSRelations().add(depRel);
					depRel.addSType(STYPE_DEPENDENCY);					
				}
			}
			else if (TAG_TC_SENTENCES.equals(localName)){
				buildLayer(LAYER_SENTENCES);
			}
			else if (TAG_PARAMETER.equals(localName)){
			}
			else if (TAG_MD_METADATA.equals(localName)){
			}
			else if (TAG_DESCRIPTION.equals(localName)){
			}
			else if (TAG_TC_TEXTCORPUS.equals(localName)){
				if(attributes.getValue(ATT_LANG)!=null){getSDocument().createSMetaAnnotation(null, ATT_LANG, attributes.getValue(ATT_LANG));}				
			}
			else if (TAG_TC_LEMMA.equals(localName)){
				if(chars.length()>0){chars.delete(0, chars.length());}
				currentNodeID = attributes.getValue(TCFDictionary.ATT_TOKENIDS);
				currentAnnoID = attributes.getValue(TCFDictionary.ATT_ID);				
				SNode sNode = getSNode(currentNodeID);			
				sLayers.get(LAYER_LEMMA).getSNodes().add(sNode);
				currentSNode = sNode;
			}
			else if (TAG_TOOLINCHAIN.equals(localName)){
			}
			else if (TAG_TC_TEXT.equals(localName)){
				STextualDS primaryText = SaltFactory.eINSTANCE.createSTextualDS();
				if(chars.length()>0){chars.delete(0, chars.length());}
				currentSTDS = primaryText;
				getSDocGraph().addSNode(primaryText);
				/* reset pointer */
				p = 0;
			}
			else if (TAG_RESOURCEPROXYLIST.equals(localName)){
				chars.delete(0, chars.length());
			}
			else if (TAG_TC_TOKEN.equals(localName)){
				currentNodeID = attributes.getValue(TCFDictionary.ATT_ID);
				if(chars.length()>0){chars.delete(0, chars.length());}
			}
			else if (TAG_COMPONENTS.equals(localName)){
			}
			else if (TAG_WEBSERVICETOOLCHAIN.equals(localName)){
			}
			else if (TAG_TC_LEMMAS.equals(localName)){
				buildLayer(LAYER_LEMMA);
			}
			else if (TAG_TC_SENTENCE.equals(localName)){
				String[] seq = attributes.getValue(ATT_TOKENIDS).split(" ");				
				EList<SToken> sentenceTokens = new BasicEList<SToken>();
				for(int i=0; i<seq.length; i++){
					sentenceTokens.add((SToken)sNodes.get(seq[i]));
					if(DEBUG){System.out.println("Current token added to sentence span: "+getSDocGraph().getSText(sNodes.get(seq[i])));}
				}
				SSpan sentenceSpan = getSDocGraph().createSSpan(sentenceTokens);
				String att = attributes.getValue(ATT_ID);
				sNodes.put(att, sentenceSpan);
				sLayers.get(LAYER_SENTENCES).getSNodes().add(sentenceSpan);
			}
			else if (TAG_MD_SERVICES.equals(localName)){
			}
			else if (TAG_TOOLCHAIN.equals(localName)){
			}
			else if (TAG_TC_TAG.equals(localName)){
				/* first check, if we are really in postags 
				 * and not in morphology (both use tag "tag").
				 * tag in morphology does not contain attributes.
				 */
				if(chars.length()>0){chars.delete(0, chars.length());}
				if(attributes.getValue(ATT_TOKENIDS)!=null){
					/* build node for pos annotation */
					currentNodeID = attributes.getValue(ATT_TOKENIDS);
					currentAnnoID = attributes.getValue(ATT_ID);
					SNode sNode = getSNode(currentNodeID);					
					sLayers.get(LAYER_POS).getSNodes().add(sNode);
					currentSNode = sNode;
					if(DEBUG){
						System.out.println("currentSNode is of type "+currentSNode.getClass().getSimpleName()+" : ["+ getSDocGraph().getSText(currentSNode) +"]");
					}
				}				
			}
			else if (TAG_TC_POSTAGS.equals(localName)){
				SLayer posLayer = buildLayer(LAYER_POS);
				if(attributes.getValue(ATT_TAGSET)!=null){posLayer.createSMetaAnnotation(null, ATT_TAGSET, attributes.getValue(ATT_TAGSET));}
			}
			else if (TAG_RESOURCES.equals(localName)){
			}
			else if (TAG_TC_ANALYSIS.equals(localName)){
				currentNodeID = attributes.getValue(ATT_TOKENIDS);
				SNode sNode = getSNode(currentNodeID);
				sLayers.get(LAYER_TCF_MORPHOLOGY).getSNodes().add(sNode);
				currentSNode = sNode;
			}
			else if (TAG_TC_F.equals(localName)){
				if(chars.length()>0){chars.delete(0, chars.length());}
				currentAnnoKey = attributes.getValue(ATT_NAME);								
			}
			else if (TAG_TC_SEGMENT.equals(localName)){
				if(chars.length()>0){chars.delete(0, chars.length());}
				currentSNode.createSAnnotation(TAG_TC_SEGMENT, ATT_TYPE, attributes.getValue(ATT_TYPE));				
			}
			else if (TAG_TC_MORPHOLOGY.equals(localName)){
				SLayer tcfMorphLayer = SaltFactory.eINSTANCE.createSLayer();
				tcfMorphLayer.setSName(LAYER_TCF_MORPHOLOGY);
				getSDocGraph().addSLayer(tcfMorphLayer);
				sLayers.put(LAYER_TCF_MORPHOLOGY, tcfMorphLayer);
			}
			else if (TAG_TC_REFERENCES.equals(localName)){
				ignoreIds = false;
				currentNodeID = null;
				SLayer refLayer = buildLayer(LAYER_REFERENCES);				
				if(attributes.getValue(ATT_TYPETAGSET)!=null){refLayer.createSMetaAnnotation(null, ATT_TYPETAGSET, attributes.getValue(ATT_TYPETAGSET));}
				if(attributes.getValue(ATT_RELTAGSET)!=null){refLayer.createSMetaAnnotation(null, ATT_RELTAGSET, attributes.getValue(ATT_TYPETAGSET));}				
			}
			else if (TAG_TC_ENTITY.equals(localName)){				
				path.pop();
				if(path.peek().equals(TAG_TC_NAMEDENTITIES)){
					currentNodeID = attributes.getValue(ATT_TOKENIDS);
					SNode sNode = getSNode(currentNodeID);
					/* annotate */
					String annoVal = attributes.getValue(ATT_CLASS);
					if(annoVal!=null){sNode.createSAnnotation(LAYER_NE, ATT_CLASS, annoVal);}
					/* add to layer */
					sLayers.get(LAYER_NE).getSNodes().add(sNode);
				}
				else if(path.peek().equals(TAG_TC_REFERENCES)){
					currentSNode = null;
					idPath.clear();					
				}
			}
			else if (TAG_TC_REFERENCE.equals(localName)){
				if(!ignoreIds){
					ignoreIds = (attributes.getValue(ATT_ID).equals(currentNodeID));
					//DEBUG
					if(ignoreIds){
						System.out.println("ignoreIds becomes true@"+attributes.getValue(ATT_ID)+", because currentNodeID="+currentNodeID);
					}
					//END OF DEBUG
				}				
				StringBuilder ref = new StringBuilder();
				/* id of reference: */				
				currentNodeID = ignoreIds ? REF_PREFIX+ id++ : attributes.getValue(ATT_ID);
				String tokenIDs = attributes.getValue(ATT_TOKENIDS);
				/* store node in currentSNode */
//				currentSNode = sNodes.get(tokenIDs);				
//				if((tokenIDs.contains(" "))&&((currentSNode==null)||(!useCommonAnnotatedElement))){//we deal with a span of multiple tokens
//					if(true){System.out.println("MULTIPLE TOKENS");}
//					String[] seq = tokenIDs.split(" ");
//					currentSNode = getSDocGraph().createSSpan((SToken)sNodes.get(seq[0]));
//					for(int i=1; i<seq.length; i++){
//						getSDocGraph().addSNode(currentSNode, (SToken)sNodes.get(seq[i]), STYPE_NAME.SSPANNING_RELATION);
//					}
//					if(useCommonAnnotatedElement){sNodes.put(tokenIDs, currentSNode);}					
//				}
//				else{
//					if(shrinkTokenAnnotations){
//						currentSNode = (SToken)currentSNode;
//					}
//					else{
//						currentSNode = sNodes.get(tokenIDs+SPAN);					
//						if((currentSNode==null)||(!useCommonAnnotatedElement)){
//							currentSNode = getSDocGraph().createSSpan((SToken)sNodes.get(tokenIDs));
//							if(useCommonAnnotatedElement){sNodes.put(tokenIDs+SPAN, currentSNode);}								
//						}
//					}
//				}
				currentSNode = getSNode(attributes.getValue(ATT_TOKENIDS));
				System.out.println("CHECK: "+currentSNode.getSName()+(getSDocGraph().getSNodes().contains(currentSNode)? " " : " NOT ") + "contained");
				/* annotate */
				if(currentSNode.getSAnnotation(LAYER_REFERENCES+"::"+ATT_TYPE)==null){//references can be used in several entities, e.g. "them" with "her" and "him"
					currentSNode.createSAnnotation(LAYER_REFERENCES, ATT_TYPE, attributes.getValue(ATT_TYPE));
				}
				sNodes.put(currentNodeID, currentSNode);//map with reference id -- only used with ignoreIds==false
				sLayers.get(LAYER_REFERENCES).getSNodes().add(currentSNode);
				
				/* put references on stack to build them later (if it is not the mentioning of the antecedent) */
				
				if(attributes.getValue(ATT_REL)!=null){//in webanno files this is false for the last reference					
					ref.append(currentSNode.getSId()).append("%%%").append(attributes.getValue(ATT_TARGET)).append("%%%").append(attributes.getValue(ATT_REL));
					idPath.push(ref.toString());
				}else if(ignoreIds){
					idPath.push(currentSNode.getSId().toString());//target of all the others
				}
			}
			else if (TAG_TC_NAMEDENTITIES.equals(localName)){
				SLayer namedEntities = buildLayer(LAYER_NE);		
				String annoVal = attributes.getValue(ATT_TYPE);
				if(annoVal!=null){namedEntities.createSMetaAnnotation(null, ATT_TYPE, annoVal);}			
			}
			else if (TAG_TC_PHONETICS.equals(localName)){
				SLayer phoLayer = buildLayer(LAYER_PHONETICS);
				String annoVal = attributes.getValue(ATT_TRANSCRIPTION);
				if(annoVal!=null){phoLayer.createSMetaAnnotation(null, ATT_TRANSCRIPTION, annoVal);}
			}
			else if (TAG_TC_PRON.equals(localName)){
				chars.delete(0, chars.length());
				currentNodeID = attributes.getValue(ATT_TOKID);
				currentSNode = shrinkTokenAnnotations ? (SToken)sNodes.get(currentNodeID) : 
						(useCommonAnnotatedElement ? sNodes.get(currentNodeID+SPAN) : getSDocGraph().createSSpan((SToken)sNodes.get(currentNodeID)));
				if(currentSNode==null){//only possible if useCommonAnnotatedElement==true
					currentSNode = getSDocGraph().createSSpan((SToken)sNodes.get(currentNodeID));
					sNodes.put(currentNodeID+SPAN, currentSNode);
				}
				sLayers.get(LAYER_PHONETICS).getSNodes().add(currentSNode);
			}
			else if (TAG_TC_ORTHOGRAPHY.equals(localName)){
				buildLayer(LAYER_ORTHOGRAPHY);
			}
			else if (TAG_TC_CORRECTION.equals(localName)){
				chars.delete(0, chars.length());
				currentNodeID = attributes.getValue(ATT_TOKENIDS);				
				SNode sNode = getSNode(currentNodeID);
				SAnnotation correction = sNode.createSAnnotation(LAYER_ORTHOGRAPHY, TAG_TC_CORRECTION, null); 
				String opVal = attributes.getValue(ATT_OPERATION);
				if(opVal!=null){
					SAnnotation operation = SaltFactory.eINSTANCE.createSAnnotation();
					operation.setName(ATT_OPERATION);
					operation.setNamespace(LAYER_ORTHOGRAPHY);
					operation.setValue(attributes.getValue(ATT_OPERATION));
					correction.addLabel(operation);
				}								
				sLayers.get(LAYER_ORTHOGRAPHY).getSNodes().add(sNode);
				currentSNode = sNode;
			}
			else if (TAG_TC_GEO.equals(localName)){//only once allowed
				SLayer geoLayer = buildLayer(LAYER_GEO);				
				for(int i=0; i<attributes.getLength(); i++){
					geoLayer.createSMetaAnnotation(null, attributes.getLocalName(i), attributes.getValue(i));
					System.out.println("Created annotation "+attributes.getLocalName(i)+" and assigned value "+attributes.getValue(i));
				}
			}
			else if (TAG_TC_SRC.equals(localName)){//only once in <geo> allowed (but obligatory!)
				chars.delete(0, chars.length());				
			}
			else if (TAG_TC_GPOINT.equals(localName)){//multiple in <geo> allowed (not obligatory)
				SNode sNode = getSNode(attributes.getValue(ATT_TOKENIDS));
				/* annotate */
				if(attributes.getValue(ATT_ALT)!=null){sNode.createSAnnotation(LAYER_GEO, ATT_ALT, attributes.getValue(ATT_ALT));}
				if(attributes.getValue(ATT_LAT)!=null){sNode.createSAnnotation(LAYER_GEO, ATT_LAT, attributes.getValue(ATT_LAT));}
				if(attributes.getValue(ATT_LON)!=null){sNode.createSAnnotation(LAYER_GEO, ATT_LON, attributes.getValue(ATT_LON));}
				if(attributes.getValue(ATT_CONTINENT)!=null){sNode.createSAnnotation(LAYER_GEO, ATT_CONTINENT, attributes.getValue(ATT_CONTINENT));}
				if(attributes.getValue(ATT_COUNTRY)!=null){sNode.createSAnnotation(LAYER_GEO, ATT_COUNTRY, attributes.getValue(ATT_COUNTRY));}
				if(attributes.getValue(ATT_CAPITAL)!=null){sNode.createSAnnotation(LAYER_GEO, ATT_CAPITAL, attributes.getValue(ATT_CAPITAL));}
				sLayers.get(LAYER_GEO).getSNodes().add(sNode);
			}
			else if (TAG_TC_SYNONYMY.equals(localName) || TAG_TC_ANTONYMY.equals(localName) || TAG_TC_HYPONYMY.equals(localName) || TAG_TC_HYPERONYMY.equals(localName)){
				if(!sLayers.containsKey(LAYER_LS)){
					buildLayer(LAYER_LS);				
				}				
			}
			else if (TAG_TC_ORTHFORM.equals(localName)){
				path.pop();
				chars.delete(0, chars.length());
				currentAnnoID = attributes.getValue(ATT_LEMMAREFS);
				SLemmaAnnotation lemma = (SLemmaAnnotation)labels.get(currentAnnoID);
				SAnnotation anno = SaltFactory.eINSTANCE.createSAnnotation();
				anno.setNamespace(LAYER_LS);
				anno.setName(path.peek());
				lemma.addLabel(anno);
				sLayers.get(LAYER_LS).getSNodes().add((SNode)lemma.getSAnnotatableElement());
			}
			else if (TAG_TC_WSD.equals(localName)){
				buildLayer(LAYER_WORDSENSE);
				String annoVal = attributes.getValue(ATT_SRC);
				if(annoVal!=null){sLayers.get(LAYER_WORDSENSE).createSAnnotation(null, ATT_SRC, annoVal);}
			}
			else if (TAG_TC_WS.equals(localName)){
				SNode sNode = getSNode(attributes.getValue(ATT_TOKENIDS));
				if(attributes.getValue(ATT_LEXUNITS)!=null){sNode.createSAnnotation(LAYER_WORDSENSE, ATT_LEXUNITS, attributes.getValue(ATT_LEXUNITS));}
				if(attributes.getValue(ATT_COMMENT)!=null){sNode.createSAnnotation(LAYER_WORDSENSE, ATT_COMMENT, attributes.getValue(ATT_COMMENT));}
				sLayers.get(LAYER_WORDSENSE).getSNodes().add(sNode);
			}
			else if (TAG_TC_WORDSPLITTINGS.equals(localName)){
				SLayer splitLayer = buildLayer(LAYER_SPLITTINGS);
				if(attributes.getValue(ATT_TYPE)!=null){splitLayer.createSAnnotation(null, ATT_TYPE, attributes.getValue(ATT_TYPE));}
			}
			else if (TAG_TC_SPLIT.equals(localName)){
				chars.delete(0, chars.length());
				currentSNode = getSNode(attributes.getValue(ATT_TOKID));
				sLayers.get(LAYER_SPLITTINGS).getSNodes().add(currentSNode);
			}
			else if (TAG_TC_DISCOURSECONNECTIVES.equals(localName)){
				SLayer discourseLayer = buildLayer(LAYER_DISCOURSE);
				String annoVal = attributes.getValue(ATT_TAGSET);
				if(annoVal!=null){discourseLayer.createSMetaAnnotation(null, ATT_TAGSET, annoVal);}
			}
			else if (TAG_TC_CONNECTIVE.equals(localName)){
				SNode sNode = getSNode(attributes.getValue(ATT_TOKENIDS));
				String annoVal = attributes.getValue(ATT_TYPE);
				if(annoVal!=null){sNode.createSAnnotation(LAYER_DISCOURSE, ATT_TYPE, annoVal);}
				sLayers.get(LAYER_DISCOURSE).getSNodes().add(sNode);
			}
			else if (TAG_TC_TEXTSTRUCTURE.equals(localName)){
				buildLayer(LAYER_TEXTSTRUCTURE);
			}
			else if (TAG_TC_TEXTSPAN.equals(localName)){				
				if(attributes.getValue(ATT_START)!=null && attributes.getValue(ATT_END)!=null){
					SDocumentGraph graph = getSDocGraph();
					SToken startToken = (SToken)sNodes.get(attributes.getValue(ATT_START));
					SToken endToken = (SToken)sNodes.get(attributes.getValue(ATT_END));
					SNode sNode = null;
					if(startToken.equals(endToken)){
						sNode = shrinkTokenAnnotations ? startToken : graph.createSSpan(startToken);
					}
					else{
						/* we ignore useCommonAnnotatedElement here */
						EList<SToken> allTokens = graph.getSortedSTokenByText();					
						int j=0;
						while(j<allTokens.size() && !allTokens.get(j).equals(startToken)){
							j++;
						}
						sNode = graph.createSSpan(startToken);
						do{
							graph.addSNode(sNode, allTokens.get(j), STYPE_NAME.SSPANNING_RELATION);
							j++;
						}while(j<allTokens.size() && !allTokens.get(j).equals(endToken));
						graph.addSNode(sNode, allTokens.get(j), STYPE_NAME.SSPANNING_RELATION);
					}
					/* annotate */
					sNode.createSAnnotation(LAYER_TEXTSTRUCTURE, ATT_TYPE, attributes.getValue(ATT_TYPE));
					
					sLayers.get(LAYER_TEXTSTRUCTURE).getSNodes().add(sNode);
				}
			}
		}
		
		@Override
		public void endElement(java.lang.String uri,
                String localName,
                String qName) throws SAXException{
			localName = qName.substring(qName.lastIndexOf(":")+1);
			if(TAG_TC_CONSTITUENT.equals(localName)){
				if(DEBUG){System.out.println((shrinkTokenAnnotations)&&(useCommonAnnotatedElement) ? "Popping from stack: "+sNodes.get(idPath.peek()).getSAnnotation(ATT_CAT).getValueString() : "POP()");}
				idPath.pop();			
			}
			else if(TAG_TC_ENTITY.equals(localName)){
				if(TAG_TC_REFERENCE.equals(path.peek())){
					System.out.println("========================================= STARTING TO EMPTY REFERENCE STACK ");
					String[] seq = null;
					SNode target = null;
					while(!idPath.empty()){						
						System.out.println("STACK TOP: "+idPath.peek());
						seq = idPath.pop().split("%%%");
						if(ignoreIds){
							if(seq.length==1){//target/antecedent
								System.out.println("Reseting target node");
								target = getSDocGraph().getSNode(seq[0]);
							}else{//ATTENTION target is supposed to be !=null (!!!)
								if(target==null){System.out.println("!--------------------------- WARNING: target of reference not set!");}
								assert(target!=null) : "target of reference not set! (null)";
								SPointingRelation ref = (SPointingRelation)getSDocGraph().addSNode(getSDocGraph().getSNode(seq[0]), target, STYPE_NAME.SPOINTING_RELATION);
								ref.addSType(STYPE_REFERENCE);
								ref.createSAnnotation(LAYER_REFERENCES, ATT_REL, seq[2]);
								sLayers.get(LAYER_REFERENCES).getSRelations().add(ref);
							}
						}else{						
							if(seq.length!=1){//CHECK isn't that always true?!
								System.out.println("==="+getSDocGraph().getSText(getSDocGraph().getSNode(seq[0]))+"-->"+getSDocGraph().getSText(sNodes.get(seq[1])));
								/* relation on antecedent */
								if(!(seq[0].equals(seq[1]))){
									System.out.println("*********************** "+seq[1]+"is "+ (getSDocGraph().getSNodes().contains(sNodes.get(seq[1])) ? "" : "NOT") +" contained in sDocGraph!");
									SNode debugNodeA = getSDocGraph().getSNode(seq[0]);
									SNode debugNodeB = sNodes.get(seq[1]);
									System.out.println("A="+debugNodeA+" by seq[0]="+seq[0]);
									System.out.println("B="+debugNodeB+" by seq[1]="+seq[1]);
									SPointingRelation ref = (SPointingRelation)getSDocGraph().addSNode(getSDocGraph().getSNode(seq[0]), sNodes.get(seq[1]), STYPE_NAME.SPOINTING_RELATION);							
									ref.createSAnnotation(LAYER_REFERENCES, ATT_REL, seq[2]);
									ref.addSType(STYPE_REFERENCE);
									sLayers.get(LAYER_REFERENCES).getSRelations().add(ref);	
								}							
							}
						}
					}
					
				}
			}
			else if(TAG_TC_TEXT.equals(localName)){
				String oldtext = currentSTDS.getSText();
				currentSTDS.setSText(oldtext==null ? chars.toString() : oldtext+chars.toString());
				if(DEBUG){System.out.println("add text to current STextualDS: "+chars.toString());}
			}
			else if(TAG_TC_TAG.equals(localName)){
				/* build annotation – only use in POS */
				path.pop();
				if(DEBUG){System.out.println("\tpop() executed. New stack peek: "+path.peek());}
				if(TAG_TC_POSTAGS.equals(path.peek())){
					SAnnotation sAnno = SaltFactory.eINSTANCE.createSPOSAnnotation();
					sAnno.setValue(chars.toString());
					currentSNode.addSAnnotation(sAnno);
					labels.put(currentAnnoID, sAnno);
					if(DEBUG){
						System.out.println("\tAdding SPOSAnnotation ["+sAnno.getQName()+"="+sAnno.getValueString()+"] to "+currentSNode.getClass().getSimpleName()+":["+getSDocGraph().getSText(currentSNode)+"]");						
					}
				}
			}
			else if(TAG_TC_F.equals(localName)){
				/* build annotation */
				currentSNode.createSAnnotation(null, currentAnnoKey, chars.toString());
			}
			else if(TAG_TC_LEMMA.equals(localName)){
				/* build annotation */
				if(DEBUG){
					System.out.println("building lemma annotation for SNode ["+getSDocGraph().getSText(currentSNode)+"]");
					System.out.println("value="+chars.toString());
				}
				SAnnotation anno = SaltFactory.eINSTANCE.createSLemmaAnnotation();
				anno.setValue(chars.toString());
				currentSNode.addSAnnotation(anno);
				labels.put(currentAnnoID, anno);
			}
			else if(TAG_TC_TOKEN.equals(localName)){
				/* build token */				
				String primaryData = currentSTDS.getSText();
				String tok = chars.toString();
//				System.out.println("building token: ["+tok+"] with p start value "+p);
				while(!primaryData.substring(p).startsWith(tok)){
					p++;
				}
				
				sNodes.put(currentNodeID, getSDocGraph().createSToken(currentSTDS, p, p+tok.length()));
			}
			else if(TAG_TC_SEGMENT.equals(localName)){
				/* build annotation TODO */
//				currentSNode.createSAnnotation(TAG_TC_SEGMENT, TAG_TC_SEGMENT, chars.toString());				
			}
			else if(TAG_TC_TOKENS.equals(localName)){
			}
			else if(TAG_TC_PRON.equals(localName)){
				currentSNode.createSAnnotation(LAYER_PHONETICS, TAG_TC_PRON, chars.toString());
			}
			else if (TAG_TC_CORRECTION.equals(localName)){
				System.out.println(currentSNode.getSAnnotation(LAYER_ORTHOGRAPHY+"::"+TAG_TC_CORRECTION));
				currentSNode.getSAnnotation(LAYER_ORTHOGRAPHY+"::"+TAG_TC_CORRECTION).setValue(chars.toString());
			}
			else if (TAG_TC_SRC.equals(localName)){
				sLayers.get(LAYER_GEO).createSMetaAnnotation(null, TAG_TC_SRC, chars.toString());
			}
			else if (TAG_TC_ORTHFORM.equals(localName)){
				labels.get(currentAnnoID).getLabel(LAYER_LS, path.peek()).setValue(chars.toString());
			}
			else if (TAG_TC_SPLIT.equals(localName)){
				currentSNode.createSAnnotation(LAYER_SPLITTINGS, TAG_TC_SPLIT, chars.toString());
			}
			else if (TAG_RESOURCEPROXYLIST.equals(localName)){				
			}
			else if (TAG_RESOURCEPROXY.equals(localName)){	
				
			}
			else if (TAG_MDCREATOR.equals(localName)){
				if(chars.length()>0){
					SMetaAnnotation meta = getSDocument().getSMetaAnnotation(TAG_MDCREATOR);
					if(meta!=null){
						meta.setValueString(meta.getValueString()+"; "+chars.toString());
					}else{
						getSDocument().createSMetaAnnotation(null, TAG_MDCREATOR, chars.toString());
					}
				}
			}
			else if (TAG_MDCREATIONDATE.equals(localName)){
				if(chars.length()>0){getSDocument().createSMetaAnnotation(null, TAG_MDCREATIONDATE, chars.toString());}
			}
			else if (TAG_MDSELFLINK.equals(localName)){
				if(chars.length()>0){getSDocument().createSMetaAnnotation(null, TAG_MDSELFLINK, chars.toString());}
			}
			else if (TAG_MDPROFILE.equals(localName)){
				if(chars.length()>0){getSDocument().createSMetaAnnotation(null, TAG_MDPROFILE, chars.toString());}
			}
			else if (TAG_MDCOLLECTIONDISPLAYNAME.equals(localName)){
				if(chars.length()>0){getSDocument().createSMetaAnnotation(null, TAG_MDCOLLECTIONDISPLAYNAME, chars.toString());}
			}
			else if (TAG_TC_TEXTCORPUS.equals(localName)){				
//				System.out.println("Bye bye");
//				System.exit(0);				
			}
		}
		
		private boolean isPrettyPrint(String s){
			return s.replace(" ", "").replace("\t", "").replace("\n", "").replace("\r", "").isEmpty();			
		}
		
		@Override
		public void characters(char[] ch, int start, int length) throws SAXException{							
			StringBuilder txt = new StringBuilder();
			for(int i=start; i<start+length; i++){
				txt.append(ch[i]);
			}
			boolean add = !isPrettyPrint(txt.toString());
			if(add){
				chars.append(txt.toString());
			}
		}
		
		private <T> Stack<T> revertStack(Stack<T> stack){
			if(stack==null){return null;}
			Stack<T> retStack = new Stack<T>();
			while(!stack.isEmpty()){				
				retStack.push(stack.pop());
			}
			return retStack;
		}
		
		private SNode getSNode(String id){
			if(id==null){return null;}			
			SNode sNode = sNodes.get(id);			
			SDocumentGraph graph = getSDocGraph();
			if(id.contains(" ")){
				if(sNode==null){					
					/*build span*/
					String[] seq = id.split(" ");
					sNode = graph.createSSpan((SToken)sNodes.get(seq[0]));
					for(int i=1; i<seq.length; i++){
						graph.addSNode(sNode, (SToken)sNodes.get(seq[i]), STYPE_NAME.SSPANNING_RELATION);
					}
					if(useCommonAnnotatedElement){sNodes.put(id, sNode);}
				}
			}
			else{//single token				
				sNode = shrinkTokenAnnotations ? (SToken)sNode : (useCommonAnnotatedElement ? sNodes.get(id+SPAN) : graph.createSSpan((SToken)sNode));
				if(sNode==null){//only if shrinkTokenAnnotations==false and useCommonAnnotatedElement==true
					/* build span over single token */
					sNode = graph.createSSpan((SToken)sNodes.get(id));
					sNodes.put(id+SPAN, sNode);
				}
			}
			return sNode;
		}
		
		private SDocumentGraph getSDocGraph(){
			return getSDocument().getSDocumentGraph();
		}
		
		private SLayer buildLayer(String name){
			SLayer newLayer = SaltFactory.eINSTANCE.createSLayer();
			newLayer.setSName(name);
			sLayers.put(name, newLayer);
			getSDocGraph().addSLayer(newLayer);
			return newLayer;
		}
		
		private Label createAnnotation(SNode sNode, String namespace, String name, String value){			
			if(sNode==null || name==null){return null;}
			String qName = namespace==null ? name : namespace+"::"+name;
			Label anno = sNode.getSAnnotation(qName);
			if(anno!=null){
				anno.setValue(value);
			}else{
				anno = sNode.createSAnnotation(namespace, name, value);
			}
			return anno;
		}
		
		private Label createMetaAnnotation(SNode sNode, String namespace, String name, String value){
			if(sNode==null || name==null){return null;}
			String qName = namespace==null ? name : namespace+"::"+name;
			Label anno = sNode.getSMetaAnnotation(qName);
			if(anno!=null){
				anno.setValue(value);
			}else{
				anno = sNode.createSMetaAnnotation(namespace, name, value);
			}
			return anno;
		}
}

}
