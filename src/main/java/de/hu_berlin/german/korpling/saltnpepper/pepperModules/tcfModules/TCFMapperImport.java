package de.hu_berlin.german.korpling.saltnpepper.pepperModules.tcfModules;

import java.util.Stack;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.BasicEMap;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.EMap;
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
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SNode;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SRelation;

public class TCFMapperImport extends PepperMapperImpl{
//CHECK-ASK: might it be better to implement and extension of PepperMapperImpl here?	
	
	private EMap<String, SNode> sNodes;
	/* TODO CHECK what do I need labels<> for? */
	private EMap<String, Label> labels;
	private EMap<String, SLayer> sLayers;
	
	/* CHECK rethink organisation of constant strings */
	public static final String LEVEL_SENTENCE = "sentence";
	public static final String LAYER_POS = "pos";
	public static final String LAYER_DEPENDENCIES = "dependencies";
	public static final String LEVEL_DEPENDENCY = "dependency";
	public static final String LAYER_TCF_MORPHOLOGY = "tcfMorphology";
	public static final String LAYER_CONSTITUENTS = "syntax";
	public static final String LAYER_LEMMA = "lemma";
	public static final String LAYER_REFERENCES = "references";
	
	public static final String ANNO_NAME_CONSTITUENT = "const";
	
	public static final String HEAD_MARKER = "refHead";//is annotation key and value for head annotation (online one instance for all necessary)
	public static final String SPAN = "span";//marking element for span ids over single tokens	
	
	private static final boolean DEGUG = true;
	
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
		return(DOCUMENT_STATUS.COMPLETED);
	}
	
	private class TCFReader extends DefaultHandler2 implements TCFDictionary {
		private STextualDS currentSTDS;
		private int p;
		private Stack<String> path;	
		private Stack<String> idPath;
		private String currentNodeID;		
		private String currentAnnoID;
		private String currentAnnoKey;
		private SNode currentSNode;
		private boolean shrinkTokenAnnotations;		
		
		public TCFReader(){
			super();
			path = new Stack<String>();
			idPath = new Stack<String>();
			currentNodeID = null;
			currentSNode = null;
			currentAnnoID = null;
			currentAnnoKey = null;
			p = 0;
			shrinkTokenAnnotations = ((TCFImporterProperties)getProperties()).isShrinkTokenAnnotation(); /* TODO */
		}
		
		@Override
		public void startElement(	String uri,
				String localName,
				String qName,
				Attributes attributes)throws SAXException
		{
			p = 0;
			localName = qName.substring(qName.lastIndexOf(":")+1);
			path.push(localName);
			if (TAG_TC_TOKENS.equals(localName)){				
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
			else if (TAG_TC_PARSE.equals(localName)){
			}
			else if (TAG_TC_PARSING.equals(localName)){
				currentNodeID = null;
				SLayer syntaxLayer = SaltFactory.eINSTANCE.createSLayer();
				syntaxLayer.setSName(LAYER_CONSTITUENTS);
				syntaxLayer.createSAnnotation(null, ATT_TAGSET, attributes.getValue(ATT_TAGSET));
				sLayers.put(LAYER_CONSTITUENTS, syntaxLayer);
				getSDocument().getSDocumentGraph().addSLayer(syntaxLayer);
			}
			else if (TAG_GENERALINFO.equals(localName)){
			}
			else if (TAG_CMD.equals(localName)){
			}
			else if (TAG_RESOURCECLASS.equals(localName)){
			}
			else if (TAG_HEADER.equals(localName)){
			}
			else if (TAG_TC_CONSTITUENT.equals(localName)){
				String constID = attributes.getValue(ATT_ID);
				/* are we dealing with a potential SToken (sequence) or a potential SStructure? */
				String tokenIDs = attributes.getValue(ATT_TOKENIDS);
				if(tokenIDs==null){
					/* SStructure */					
					SStructure sStruc = SaltFactory.eINSTANCE.createSStructure();
					sStruc.createSAnnotation(null, ATT_CAT, attributes.getValue(ATT_CAT));
					sNodes.put(constID, sStruc);
					sLayers.get(LAYER_CONSTITUENTS).getSNodes().add(sStruc);
					if(idPath.isEmpty()){						
						/* sStruc is root */
						getSDocument().getSDocumentGraph().addSNode(sStruc);
						if(DEGUG){System.out.println("Building node: "+sStruc.getSAnnotation(ATT_CAT).getSValue()+" as root");}
					}
					else{
						getSDocument().getSDocumentGraph().addSNode(sNodes.get(idPath.peek()), sStruc, STYPE_NAME.SDOMINANCE_RELATION);
						if(DEGUG){System.out.println("Building node: "+sStruc.getSAnnotation(ATT_CAT).getSValue()+"-"+constID.replace("c", "")+" and appending it to node "+sNodes.get(idPath.peek()).getSAnnotation(ATT_CAT).getValueString()+"-"+idPath.peek().replace("c", ""));}
					}					
					if(DEGUG){System.out.println("Pushing to stack: "+attributes.getValue(ATT_CAT));}
					idPath.push(constID);					
				}
				else{
					/* tokens/spans */
					SNode sNode = sNodes.get(tokenIDs);
					if(tokenIDs.contains(" ")){
						if(sNode==null){
							if(DEGUG){System.out.println("Span does not exist. I build it.");}
							String[] seq = tokenIDs.split(" ");
							EList<SToken> sTokensForSpan = new BasicEList<SToken>();
							for(int i=0; i<seq.length; i++){
								sTokensForSpan.add((SToken)sNodes.get(seq[i]));							
							}
							sNode = getSDocument().getSDocumentGraph().createSSpan(sTokensForSpan);
							sNodes.put(tokenIDs, sNode);
							if(DEGUG){System.out.println("Building span: \""+getSDocument().getSDocumentGraph().getSText(sNode)+"\" and appending it to node "+sNodes.get(idPath.peek()).getSAnnotation(ATT_CAT).getValueString()+"-"+idPath.peek().replace("c", ""));}
						}						
						sNode.createSAnnotation(null, ATT_CAT, attributes.getValue(ATT_CAT));
						getSDocument().getSDocumentGraph().addSNode(sNodes.get(idPath.peek()), sNode, STYPE_NAME.SDOMINANCE_RELATION);
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
								sNode = getSDocument().getSDocumentGraph().createSSpan((SToken)sNodes.get(tokenIDs));
								if(DEGUG){System.out.println("Building span over single token \""+getSDocument().getSDocumentGraph().getSText(sNode)+"\"");}
								sNodes.put(tokenIDs+SPAN, sNode);
							}							
						}
						if(sNode==null){sNode = getSDocument().getSDocumentGraph().createSSpan((SToken)sNodes.get(tokenIDs));}
						sNode.createSAnnotation(null, ATT_CAT, attributes.getValue(ATT_CAT));//TODO annotation might already exist, we definitely have to use namespaces!
						getSDocument().getSDocumentGraph().addSNode(sNodes.get(idPath.peek()), sNode, STYPE_NAME.SDOMINANCE_RELATION);
						if(DEGUG){System.out.println("Taking token:"+getSDocument().getSDocumentGraph().getSText(sNode)+" and appending it to node "+sNodes.get(idPath.peek()).getSAnnotation(ATT_CAT).getValueString()+"-"+idPath.peek().replace("c", ""));}						
						/*we HAVE TO push also tokens onto the stack to avoid that at the end of their xml-element the wrong constituent is popped off the stack*/
						/*so the pushed id is actually just a dummy -> we don't have to check, if the span should be pushed*/
						idPath.push(tokenIDs);
					}
				}
			}
			else if(TAG_TC_DEPPARSING.equals(localName)){
				currentNodeID = null;
				SLayer depLayer = SaltFactory.eINSTANCE.createSLayer();
				depLayer.setSName(LAYER_DEPENDENCIES);
				getSDocument().getSDocumentGraph().addSLayer(depLayer);
				/* TODO the same has to be done in SaltSample, still undone */
				/* TODO the same has to be done for POS both in SaltSample(CHECK) and here */
				/* TODO CHECK does this layer actually make any sense? */
				depLayer.createSMetaAnnotation(null, TCFDictionary.ATT_TAGSET, attributes.getValue(TCFDictionary.ATT_TAGSET));
				depLayer.createSMetaAnnotation(null, TCFDictionary.ATT_EMPTYTOKS, attributes.getValue(TCFDictionary.ATT_EMPTYTOKS));
				depLayer.createSMetaAnnotation(null, TCFDictionary.ATT_MULTIGOVS, attributes.getValue(TCFDictionary.ATT_MULTIGOVS));
				sLayers.put(LAYER_DEPENDENCIES, depLayer);
			}
			else if(TAG_TC_PARSE.equals(localName)){
				idPath.clear(); //relevant for constituent parsing				
			}
			else if(TAG_TC_DEPENDENCY.equals(localName)){
				/* is there no governing ID, we skip, because we don't use a root node */
				if(attributes.getValue(ATT_GOVIDS)!=null){
					SPointingRelation depRel = SaltFactory.eINSTANCE.createSPointingRelation();
					depRel.setSource(sNodes.get(attributes.getValue(ATT_GOVIDS))); //CAREFUL TODO multigovs
					depRel.setTarget(sNodes.get(attributes.getValue(ATT_DEPIDS))); //CAREFUL TODO multigovs
					depRel.createSAnnotation(null, LEVEL_DEPENDENCY, attributes.getValue(ATT_FUNC));
					getSDocument().getSDocumentGraph().addSRelation(depRel);				
					sLayers.get(LAYER_DEPENDENCIES).getEdges().add(depRel);
				}
			}
			else if (TAG_TC_SENTENCES.equals(localName)){
			}
			else if (TAG_PARAMETER.equals(localName)){
			}
			else if (TAG_MD_METADATA.equals(localName)){
			}
			else if (TAG_DESCRIPTION.equals(localName)){
			}
			else if (TAG_TC_TEXTCORPUS.equals(localName)){	
				getSDocument().getSDocumentGraph().createSMetaAnnotation(null, ATT_LANG, attributes.getValue(ATT_LANG));
			}
			else if (TAG_TC_LEMMA.equals(localName)){
				currentNodeID = attributes.getValue(TCFDictionary.ATT_TOKENIDS);
				currentAnnoID = attributes.getValue(TCFDictionary.ATT_ID);
				SNode sNode = sNodes.get(currentNodeID);
				if(currentNodeID.contains(" ")){
					if(sNode==null){
						/* build a span */
						String[] seq = currentNodeID.split(" ");
						EList<SToken> spanTokens = new BasicEList<SToken>();
						for(int i=0; i<seq.length; i++){
							spanTokens.add((SToken)sNodes.get(seq[i]));						
						}
						sNode = getSDocument().getSDocumentGraph().createSSpan(spanTokens);
						sNodes.put(currentNodeID, sNode);
					}
				}
				else{
					/* single token */
					sNode = shrinkTokenAnnotations ? (SToken)sNode : sNodes.get(currentNodeID+SPAN);
					if(sNode==null){
						sNode = sNodes.get(currentNodeID);
						sNode = getSDocument().getSDocumentGraph().createSSpan((SToken)sNode);
						sNodes.put(currentNodeID+SPAN, sNode);
					}					
				}				
				sLayers.get(LAYER_LEMMA).getSNodes().add(sNode);
				currentSNode = sNode;
			}
			else if (TAG_TOOLINCHAIN.equals(localName)){
			}
			else if (TAG_TC_TEXT.equals(localName)){
				STextualDS primaryText = SaltFactory.eINSTANCE.createSTextualDS();
				currentSTDS = primaryText;
				getSDocument().getSDocumentGraph().addSNode(primaryText);
				/* reset pointer */
				p = 0;
			}
			else if (TAG_RESOURCEPROXYLIST.equals(localName)){
			}
			else if (TAG_TC_TOKEN.equals(localName)){
				currentNodeID = attributes.getValue(TCFDictionary.ATT_ID);
			}
			else if (TAG_COMPONENTS.equals(localName)){
			}
			else if (TAG_WEBSERVICETOOLCHAIN.equals(localName)){
			}
			else if (TAG_TC_LEMMAS.equals(localName)){
				SLayer lemmaLayer = SaltFactory.eINSTANCE.createSLayer();
				lemmaLayer.setSName(LAYER_LEMMA);
				getSDocument().getSDocumentGraph().addSLayer(lemmaLayer);
				sLayers.put(LAYER_LEMMA, lemmaLayer);
				if(DEGUG){System.out.println("Lemma layer created and added.");}
			}
			else if (TAG_TC_SENTENCE.equals(localName)){
				String[] seq = attributes.getValue(TCFDictionary.ATT_TOKENIDS).split(" ");				
				EList<SToken> sentenceTokens = new BasicEList<SToken>();
				for(int i=0; i<seq.length; i++){
					sentenceTokens.add((SToken)sNodes.get(seq[i]));
					if(DEGUG){System.out.println("Current token added to sentence span: "+getSDocument().getSDocumentGraph().getSText(sNodes.get(seq[i])));}
				}
				SSpan sentenceSpan = getSDocument().getSDocumentGraph().createSSpan(sentenceTokens);
				String att = attributes.getValue(TCFDictionary.ATT_ID);
				sentenceSpan.createSAnnotation(null, LEVEL_SENTENCE, att);
				sNodes.put(att, sentenceSpan);
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
				if(attributes.getValue(ATT_TOKENIDS)!=null){
					/* build node for pos annotation */
					currentNodeID = attributes.getValue(ATT_TOKENIDS);
					currentAnnoID = attributes.getValue(ATT_ID);
					SNode sNode = sNodes.get(currentNodeID);
					if(currentNodeID.contains(" ")){
						if(sNode==null){
							/*build span*/
							String[] seq = currentNodeID.split(" ");
							EList<SToken> spanTokens = new BasicEList<SToken>();
							for(int i=0; i<seq.length; i++){
								spanTokens.add((SToken)sNodes.get(seq[i]));
							}
							sNode = getSDocument().getSDocumentGraph().createSSpan(spanTokens);
							sNodes.put(currentNodeID, sNode);
						}
					}
					else{
						/* single token */
						sNode = shrinkTokenAnnotations ? (SToken)sNode : sNodes.get(currentNodeID+SPAN);
						if(sNode==null){
							sNode = sNodes.get(currentNodeID);
							sNode = getSDocument().getSDocumentGraph().createSSpan((SToken)sNode);
							sNodes.put(currentNodeID+SPAN, sNode);
						}						
					}					
					sLayers.get(LAYER_POS).getSNodes().add(sNode);
					currentSNode = sNode;					
				}													
			}
			else if (TAG_TC_POSTAGS.equals(localName)){
				SLayer posLayer = SaltFactory.eINSTANCE.createSLayer();
				posLayer.setSName(LAYER_POS);
				sLayers.put(LAYER_POS, posLayer);
				getSDocument().getSDocumentGraph().addSLayer(posLayer);
			}
			else if (TAG_RESOURCES.equals(localName)){
			}
			else if (TAG_TC_ANALYSIS.equals(localName)){
				currentNodeID = attributes.getValue(ATT_TOKENIDS);
				SNode sNode = sNodes.get(currentNodeID);
				if(currentNodeID.contains(" ")){
					if(sNode==null){
						/* build SSpan */
						String[] seq = currentNodeID.split(" ");
						EList<SToken> spanTokens = new BasicEList<SToken>();
						for(int i=0; i<seq.length; i++){
							spanTokens.add((SToken)sNodes.get(seq[i]));
						}
						sNode = getSDocument().getSDocumentGraph().createSSpan(spanTokens);
						sNodes.put(currentNodeID, sNode);
					}					
				}
				else{
					/* single token */
					sNode = shrinkTokenAnnotations ? (SToken)sNode : sNodes.get(currentNodeID+SPAN);
					if(sNode==null){
						sNode = sNodes.get(currentNodeID);
						sNode = getSDocument().getSDocumentGraph().createSSpan((SToken)sNode);
						sNodes.put(currentNodeID+SPAN, sNode);
					}					 
				}
				sLayers.get(LAYER_TCF_MORPHOLOGY).getSNodes().add(sNode);
				currentSNode = sNode;
			}
			else if (TAG_TC_F.equals(localName)){				
				currentAnnoKey = attributes.getValue(ATT_NAME);								
			}
			else if (TAG_TC_SEGMENT.equals(localName)){
				currentSNode.createSAnnotation(TAG_TC_SEGMENT, ATT_TYPE, attributes.getValue(ATT_TYPE));				
			}
			else if (TAG_TC_MORPHOLOGY.equals(localName)){
				SLayer tcfMorphLayer = SaltFactory.eINSTANCE.createSLayer();
				tcfMorphLayer.setSName(LAYER_TCF_MORPHOLOGY);
				getSDocument().getSDocumentGraph().addSLayer(tcfMorphLayer);
				sLayers.put(LAYER_TCF_MORPHOLOGY, tcfMorphLayer);
			}
			else if (TAG_TC_REFERENCES.equals(localName)){
				SLayer refLayer = SaltFactory.eINSTANCE.createSLayer();
				refLayer.setSName(LAYER_REFERENCES);
				refLayer.createSAnnotation(null, ATT_TYPETAGSET, attributes.getValue(ATT_TYPETAGSET));
				refLayer.createSAnnotation(null, ATT_RELTAGSET, attributes.getValue(ATT_TYPETAGSET));
				getSDocument().getSDocumentGraph().addSLayer(refLayer);
				sLayers.put(LAYER_REFERENCES, refLayer);
			}
			else if (TAG_TC_ENTITY.equals(localName)){
				currentSNode = null;
			}
			else if (TAG_TC_REFERENCE.equals(localName)){	
				/* currentSNode stores the goal of the relations - right now it only works for anaphoric relations */
				currentNodeID = attributes.getValue(ATT_TOKENIDS);
				SNode sNode = sNodes.get(currentNodeID);
				if(currentSNode==null){
					/* first reference in line, just build span or annotate token or span ... */					
					if(currentNodeID.contains(" ")){
						/* span */
						if(sNode==null){
							/* build span */
							String[] seq = currentNodeID.split(" ");
							sNode = getSDocument().getSDocumentGraph().createSSpan((SToken)sNodes.get(seq[0]));
							for(int i=1; i<seq.length; i++){
								getSDocument().getSDocumentGraph().addSNode(sNode, (SToken)sNodes.get(seq[i]), STYPE_NAME.SSPANNING_RELATION);
							}
							sNodes.put(currentNodeID, sNode);
						}
					}
					else{
						sNode = shrinkTokenAnnotations ? (SToken)sNodes.get(currentNodeID) : sNodes.get(currentNodeID+SPAN);
						if(sNode==null){
							sNode = getSDocument().getSDocumentGraph().createSSpan((SToken)sNodes.get(currentNodeID));
							sNodes.put(currentNodeID+SPAN, sNode);
						};
					}
					sNode.createSAnnotation(TAG_TC_REFERENCE, ATT_TYPE, attributes.getValue(ATT_TYPE));
					sLayers.get(LAYER_REFERENCES).getSNodes().add(sNode);
					currentSNode = sNode;
				}
				else{
					/* build reference to last token and annotate new one */
					if(currentNodeID.contains(" ")){
						/* span */
						if(sNode==null){
							/* build span */
							String[] seq = currentNodeID.split(" ");
							sNode = getSDocument().getSDocumentGraph().createSSpan((SToken)sNodes.get(seq[0]));
							for(int i=1; i<seq.length; i++){
								getSDocument().getSDocumentGraph().addSNode(sNode, (SToken)sNodes.get(seq[i]), STYPE_NAME.SSPANNING_RELATION);
							}
							sNodes.put(currentNodeID, sNode);
						}
					}
					else{
						sNode = shrinkTokenAnnotations ? (SToken)sNodes.get(currentNodeID) : sNodes.get(currentNodeID+SPAN);
						if(sNode==null){
							sNode = getSDocument().getSDocumentGraph().createSSpan((SToken)sNodes.get(currentNodeID));
							sNodes.put(currentNodeID+SPAN, sNode);
						};
					}
					sNode.createSAnnotation(TAG_TC_REFERENCE, ATT_TYPE, attributes.getValue(ATT_TYPE));
					sLayers.get(LAYER_REFERENCES).getSNodes().add(sNode);
					
					SRelation reference = SaltFactory.eINSTANCE.createSRelation();
					reference.setSTarget(currentSNode);
					reference.setSSource(sNode);
					reference.createSAnnotation(TAG_TC_REFERENCE, ATT_REL, attributes.getValue(ATT_REL));
					sLayers.get(LAYER_REFERENCES).getSRelations().add(reference);
				}
			}
		}
		
		@Override
		public void endElement(java.lang.String uri,
                java.lang.String localName,
                java.lang.String qName) throws SAXException{
			localName = qName.substring(qName.lastIndexOf(":")+1);
			if(TAG_TC_CONSTITUENT.equals(localName)){
				if(DEGUG){System.out.println(shrinkTokenAnnotations ? "Popping from stack: "+sNodes.get(idPath.peek()).getSAnnotation(ATT_CAT).getValueString() : "POP()");}
				idPath.pop();
			}
		}
		
		@Override
		public void characters(char[] ch, int start, int length) throws SAXException{
			StringBuilder txt = new StringBuilder();
			for(int i=start; i<start+length; i++){
				txt.append(ch[i]);
			}
			if(TAG_TC_TEXT.equals(path.peek())){				
				currentSTDS.setSText(txt.toString());
			}
			else if(TAG_TC_TOKEN.equals(path.peek())){
				/* build token */				
				String primaryData = currentSTDS.getSText();
				String tok = txt.toString();
				while(!primaryData.substring(p).startsWith(tok)){
					p++;
				}
				
				sNodes.put(currentNodeID, getSDocument().getSDocumentGraph().createSToken(currentSTDS, p, p+tok.length()));
			}
			else if(TAG_TC_TAG.equals(path.peek())){
				/* build annotation – only use in POS */
				path.pop();
				if(TAG_TC_POSTAGS.equals(path.peek())){
					SAnnotation sAnno = SaltFactory.eINSTANCE.createSPOSAnnotation();
					sAnno.setValue(txt.toString());
					currentSNode.addSAnnotation(sAnno);
					labels.put(currentAnnoID, sAnno);
				}				
			}
			else if(TAG_TC_LEMMA.equals(path.peek())){
				/* build annotation */								
				SAnnotation anno = SaltFactory.eINSTANCE.createSLemmaAnnotation();
				anno.setValue(txt.toString());
				currentSNode.addSAnnotation(anno);
				labels.put(currentAnnoID, anno);				
			}
			else if(TAG_TC_F.equals(path.peek())){
				/* build annotation */
				currentSNode.createSAnnotation(null, currentAnnoKey, txt.toString());				
			}
			else if(TAG_TC_SEGMENT.equals(path.peek())){
				/* build annotation */
				currentSNode.createSAnnotation(TAG_TC_SEGMENT, TAG_TC_SEGMENT, txt.toString());				
			}
		}
}

}
