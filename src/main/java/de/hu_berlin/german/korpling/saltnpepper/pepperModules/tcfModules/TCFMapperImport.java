package de.hu_berlin.german.korpling.saltnpepper.pepperModules.tcfModules;

import java.util.Stack;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.BasicEMap;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.EMap;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DefaultHandler2;

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
	public static final String ANNO_NAME_CONSTITUENT = "const";
	
	@Override
	public MAPPING_RESULT mapSDocument() {
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
		return(MAPPING_RESULT.FINISHED);
	}
	
	private class TCFReader extends DefaultHandler2 implements TCFDictionary {
		private STextualDS currentSTDs;
		private int p;
		private Stack<String> path;	
		private Stack<String> idPath;
		private String currentTokenID;		
		private String currentAnnoID;
		private String currentAnnoKey;
		private SNode currentSNode;
		private boolean shrinkTokenAnnotation;
		
		public TCFReader(){
			super();
			path = new Stack<String>();
			idPath = new Stack<String>();
			currentTokenID = null;
			currentSNode = null;
			currentAnnoID = null;
			currentAnnoKey = null;
			p = 0;
			shrinkTokenAnnotation = ((TCFImporterProperties)getProperties()).isShrinkTokenAnnotation(); /* TODO */
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
				currentTokenID = null;
				SLayer syntaxLayer = SaltFactory.eINSTANCE.createSLayer();
				syntaxLayer.setSName(LAYER_CONSTITUENTS);
				syntaxLayer.createSAnnotation(null, ATT_TAGSET, attributes.getValue(ATT_TAGSET));
				sLayers.put(LAYER_CONSTITUENTS, syntaxLayer);
				getSDocument().getSDocumentGraph().addLayer(syntaxLayer);
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
				/* are we dealing with a potential SToken or SStructure? */
				/* TODO also here we deal with TokenIDs, so PROP_SHRINK_TOKEN_ANNOTATIONS might play a role*/
				String tokenIDs = attributes.getValue(ATT_TOKENIDS);
				if(tokenIDs==null){
					/* constituent */					
					SStructure sStruc = SaltFactory.eINSTANCE.createSStructure();
					sStruc.createSAnnotation(null, ATT_CAT, attributes.getValue(ATT_CAT));
					sNodes.put(constID, sStruc);
					sLayers.get(LAYER_CONSTITUENTS).getSNodes().add(sStruc);
					if(idPath.isEmpty()){						
						/* sStruc is root */
						getSDocument().getSDocumentGraph().addSNode(sStruc);
						/*TEST*/System.out.println("Building node: "+sStruc.getSAnnotation(ATT_CAT).getSValue()+" as root");
					}
					else{
						getSDocument().getSDocumentGraph().addSNode(sNodes.get(idPath.peek()), sStruc, STYPE_NAME.SDOMINANCE_RELATION);
						/*TEST*/System.out.println("Building node: "+sStruc.getSAnnotation(ATT_CAT).getSValue()+"-"+constID.replace("c", "")+" and appending it to node "+sNodes.get(idPath.peek()).getSAnnotation(ATT_CAT).getValueString()+"-"+idPath.peek().replace("c", ""));
					}					
					/*TEST*/System.out.println("Pushing to stack: "+attributes.getValue(ATT_CAT));
					idPath.push(constID);					
				}
				else{
					/* token */					
					if(!tokenIDs.contains(" ")){
						/* single token */						
						/*TODO ask yourself, if a single token can be the direct root not governed by a phrase*/
						SToken sToken = (SToken)sNodes.get(tokenIDs);
						sToken.createSAnnotation(null, ATT_CAT, attributes.getValue(ATT_CAT));
						getSDocument().getSDocumentGraph().addSNode(sNodes.get(idPath.peek()), sToken, STYPE_NAME.SDOMINANCE_RELATION);
						/*TEST*/System.out.println("Taking token:"+getSDocument().getSDocumentGraph().getSText(sToken)+" and appending it to node "+sNodes.get(idPath.peek()).getSAnnotation(ATT_CAT).getValueString()+"-"+idPath.peek().replace("c", ""));
						/*TODO SHRINK PROPERTY*/
						/*we HAVE TO push also tokens onto the stack to avoid that at the end of their xml-element the wrong constituent is popped off the stack*/
						idPath.push(tokenIDs);
					}
					else{
						String[] seq = tokenIDs.split(" ");
						EList<SToken> sTokensForSpan = new BasicEList<SToken>();
						for(int i=0; i<seq.length; i++){
							sTokensForSpan.add((SToken)sNodes.get(seq[i]));							
						}
						SSpan sSpan = SaltFactory.eINSTANCE.createSSpan();
						/*TODO*/
						sSpan.createSAnnotation(null, ATT_CAT, attributes.getValue(ATT_CAT));
						sNodes.put(constID, sSpan);
						sLayers.get(LAYER_CONSTITUENTS).getSNodes().add(sSpan);/* TODO add span to syntax layer? tokens don't belong there */
						/*we HAVE TO push also tokens/spans onto the stack to avoid that at the end of their xml-element the wrong constituent is popped off the stack*/
						idPath.push(constID);/*TODO – which ID? PROBLEMATIC*/
					}
				}
			}
			else if(TAG_TC_DEPPARSING.equals(localName)){
				currentTokenID = null;
				SLayer depLayer = SaltFactory.eINSTANCE.createSLayer();
				depLayer.setSName(LAYER_DEPENDENCIES);
				getSDocument().getSDocumentGraph().addLayer(depLayer);
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
			}
			else if (TAG_TC_LEMMA.equals(localName)){
				currentTokenID = attributes.getValue(TCFDictionary.ATT_TOKENIDS);
				currentAnnoID = attributes.getValue(TCFDictionary.ATT_ID);
			}
			else if (TAG_TOOLINCHAIN.equals(localName)){
			}
			else if (TAG_TC_TEXT.equals(localName)){
				STextualDS primaryText = SaltFactory.eINSTANCE.createSTextualDS();
				currentSTDs = primaryText;
				getSDocument().getSDocumentGraph().addSNode(primaryText);
				/* reset pointer */
				p = 0;
			}
			else if (TAG_RESOURCEPROXYLIST.equals(localName)){
			}
			else if (TAG_TC_TOKEN.equals(localName)){
				currentTokenID = attributes.getValue(TCFDictionary.ATT_ID);
			}
			else if (TAG_COMPONENTS.equals(localName)){
			}
			else if (TAG_WEBSERVICETOOLCHAIN.equals(localName)){
			}
			else if (TAG_TC_LEMMAS.equals(localName)){
			}
			else if (TAG_TC_SENTENCE.equals(localName)){
				String[] seq = attributes.getValue(TCFDictionary.ATT_TOKENIDS).split(" ");				
				EList<SToken> sentenceTokens = new BasicEList<SToken>();
				for(int i=0; i<seq.length; i++){
					sentenceTokens.add((SToken)sNodes.get(seq[i]));
					/*TEST*/System.out.println("Current token added to sentence span: "+getSDocument().getSDocumentGraph().getSText(sNodes.get(seq[i])));
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
				/* TODO CHECK this tag is also used in morphology (e.g.) -> does this cause errors? 
				 * I don't think so */
				currentTokenID = attributes.getValue(ATT_TOKENIDS);
				currentAnnoID = attributes.getValue(ATT_ID);									
			}
			else if (TAG_TC_POSTAGS.equals(localName)){
				SLayer posLayer = SaltFactory.eINSTANCE.createSLayer();
				posLayer.setSName(LAYER_POS);
				sLayers.put(LAYER_POS, posLayer);
			}
			else if (TAG_RESOURCES.equals(localName)){
			}
			else if (TAG_TC_ANALYSIS.equals(localName)){
				String[] currentTokenIDs = attributes.getValue(ATT_TOKENIDS).split(" ");
				EList<SToken> sTokens = new BasicEList<SToken>();
				for(int i=0; i<currentTokenIDs.length; i++){
					sTokens.add((SToken) sNodes.get(currentTokenIDs[i]));					
				}
				currentSNode = getSDocument().getSDocumentGraph().createSSpan(sTokens);
				getSDocument().getSDocumentGraph().getSLayerByName(LAYER_TCF_MORPHOLOGY).get(0).getNodes().add(currentSNode);
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
			}
		}
		
		@Override
		public void endElement(java.lang.String uri,
                java.lang.String localName,
                java.lang.String qName) throws SAXException{
			localName = qName.substring(qName.lastIndexOf(":")+1);
			if(TAG_TC_CONSTITUENT.equals(localName)){
				/*TEST*/System.out.println("Popping from stack: "+sNodes.get(idPath.peek()).getSAnnotation(ATT_CAT).getValueString());
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
				currentSTDs.setSText(txt.toString());
			}
			else if(TAG_TC_TOKEN.equals(path.peek())){
				/* build token */				
				String primaryData = currentSTDs.getSText();
				String tok = txt.toString();
				while(!primaryData.substring(p).startsWith(tok)){
					p++;
				}
				
				sNodes.put(currentTokenID, getSDocument().getSDocumentGraph().createSToken(currentSTDs, p, p+tok.length()));
			}
			else if(TAG_TC_TAG.equals(path.peek())){
				/* build annotation – only use in POS */
				path.pop();
				if(TAG_TC_POSTAGS.equals(path.peek())){
					if(!currentTokenID.contains(" ")){
						SAnnotation anno = SaltFactory.eINSTANCE.createSPOSAnnotation();
						anno.setValue(txt.toString());
						SNode annoNode = sNodes.get(currentTokenID);
						if(!shrinkTokenAnnotation){
							annoNode = getSDocument().getSDocumentGraph().createSSpan((SToken)annoNode);							
						}
						annoNode.addSAnnotation(anno);
						labels.put(currentAnnoID, anno);
						sLayers.get(LAYER_POS).getSNodes().add(annoNode);
					}
					else{
						String[] seq = currentTokenID.split(" ");
						EList<SToken> spanTokens = new BasicEList<SToken>();
						for(int i=0; i<seq.length; i++){
							spanTokens.add((SToken)sNodes.get(seq[i]));
						}					
						SAnnotation anno = SaltFactory.eINSTANCE.createSPOSAnnotation();						
						anno.setValue(txt.toString());
						SSpan sSpan = getSDocument().getSDocumentGraph().createSSpan(spanTokens);
						sSpan.addSAnnotation(anno);
						sNodes.put(currentTokenID, sSpan);
						sLayers.get(LAYER_POS).getSNodes().add(sSpan);
					}
				}				
			}
			else if(TAG_TC_LEMMA.equals(path.peek())){
				/* build annotation */								
				SAnnotation anno = SaltFactory.eINSTANCE.createSLemmaAnnotation();
				anno.setValue(txt.toString());
				sNodes.get(currentTokenID).addSAnnotation(anno);
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
