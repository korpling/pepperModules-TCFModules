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
package org.corpus_tools.peppermodules.tcfModules;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.corpus_tools.pepper.common.DOCUMENT_STATUS;
import org.corpus_tools.pepper.impl.PepperMapperImpl;
import org.corpus_tools.pepper.modules.exceptions.PepperModuleDataException;
import org.corpus_tools.pepper.modules.exceptions.PepperModuleException;
import org.corpus_tools.salt.SALT_TYPE;
import org.corpus_tools.salt.common.SDocumentGraph;
import org.corpus_tools.salt.common.SSpan;
import org.corpus_tools.salt.common.STextualDS;
import org.corpus_tools.salt.common.SToken;
import org.corpus_tools.salt.core.SAnnotation;
import org.corpus_tools.salt.core.SNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TCFMapperExport extends PepperMapperImpl implements TCFDictionary{
	
	private static final Logger logger = LoggerFactory.getLogger(TCFMapperExport.class);
	
	private XMLStreamWriter currentTCF = null;
	private HashMap<String, String> meta = null;
	private HashMap<SNode, String> sNodes = null;
	private HashSet<SToken> emptyTokens = null;
	private String qNameLine = null;
	private String valueLine = null;
	private String qNamePage = null;
	private String valuePage = null;
	private boolean emptyTokensAllowed = false;
	private String qNameSentence = null;
	private String valueSentence = null;
	private String qNamePOS = null;
	private String qNameLemma = null;
		
	public TCFMapperExport(){
	}
	
	public void init(){
		currentTCF = null;
		sNodes = new HashMap<SNode, String>();
		emptyTokens = new HashSet<SToken>();
		qNameLine = ((TCFExporterProperties)getProperties()).getTextstructureLineName();
		valueLine = ((TCFExporterProperties)getProperties()).getTextstructureLineValue();
		qNamePage = ((TCFExporterProperties)getProperties()).getTextstructurePageName();
		valuePage = ((TCFExporterProperties)getProperties()).getTextstructurePageValue();
		emptyTokensAllowed = ((TCFExporterProperties)getProperties()).isEmptyTokensAllowed();
		qNameSentence = ((TCFExporterProperties)getProperties()).getSentenceQName();
		valueSentence = ((TCFExporterProperties)getProperties()).getSentenceValue();
		qNamePOS = ((TCFExporterProperties)getProperties()).getPOSQName();
		qNameLemma = ((TCFExporterProperties)getProperties()).getLemmaQName();
		initMeta();
	}
	
	private void initMeta(){
		meta = new HashMap<String, String>();
//		if (getDocument()!=null){//FIXME I think this is always false
//			meta.put(ATT_LANG, getDocument().getMetaAnnotation(ATT_LANG).getValue().toString()); //FIXME --> by properties we will find out where this meta attribute is
//		}
		//lang can be multiple value: parallel corpora		
	}
	
	/** this method maps an SDocument to TCF */
	@Override
	public DOCUMENT_STATUS mapSDocument(){
		init();
		if (getDocument()==null){
			throw new PepperModuleDataException(this, "No document delivered to be converted.");
		}
		ByteArrayOutputStream outStream = null;
		XMLOutputFactory factory = XMLOutputFactory.newFactory();
		XMLStreamWriter w;
		File file = null;
		PrintWriter p;
		boolean multipleFiles = getDocument().getDocumentGraph().getTextualDSs().size()>1;
		List<STextualDS> sTextualDSs = getDocument().getDocumentGraph().getTextualDSs();
		STextualDS sTextualDS = null;
		for (int i=0; i<sTextualDSs.size(); i++){			
			try {
				sTextualDS = sTextualDSs.get(i);
				outStream = new ByteArrayOutputStream();
				w = currentTCF = factory.createXMLStreamWriter(outStream);
				w.writeStartDocument();
				w.writeProcessingInstruction(TCF_PI);
				w.writeStartElement(TCFDictionary.NS_WL, TCFDictionary.TAG_WL_D_SPIN, TCFDictionary.NS_VALUE_WL);
				w.writeNamespace(NS_ED, NS_VALUE_ED);
				w.writeNamespace(NS_LX, NS_VALUE_LX);
				w.writeNamespace(NS_MD, NS_VALUE_MD);
				w.writeNamespace(NS_TC, NS_VALUE_TC);
				w.writeNamespace(NS_WL, NS_VALUE_WL);
				w.writeAttribute(ATT_VERSION, "0.4");
				w.writeStartElement(NS_MD, TAG_MD_METADATA, NS_VALUE_MD);
				w.writeEndElement();
				w.writeStartElement(NS_TC, TAG_TC_TEXTCORPUS, NS_VALUE_TC);
				w.writeAttribute(ATT_LANG, getLanguage());//TODO see also above (meta)
				mapSTextualDS(sTextualDS);
				mapTokenization(getDocument().getDocumentGraph().getSortedTokenByText());
				mapSentences();
				mapPOSAnnotations();
				mapLemmaAnnotations();
				mapLayoutAnnotations();
				w.writeEndElement();//end of textcorpus
				w.writeEndElement();//end of d-spin
				w.writeEndDocument();
				
				/*write File*/
				file = new File(getResourceURI().toFileString()+(multipleFiles? +i+".tcf" : ""));//FIXME A) File ending missing B)we need a language stack, too, in case of parallel corpora			
				file.getParentFile().mkdirs();
				try {
					p = new PrintWriter(file);
					p.println(outStream.toString());
					p.close();
				} catch (FileNotFoundException e) {	
					logger.error("Could not write TCF "+getResourceURI(), e);
				}
			} catch (XMLStreamException e) {throw new PepperModuleException();}
		}		
		return DOCUMENT_STATUS.COMPLETED;
	}
	
	private String getLanguage(){
		//TODO
		return "x-unspecified";
	}
	
	private void mapSTextualDS(STextualDS ds){
		XMLStreamWriter w = currentTCF;
		try {
			w.writeStartElement(NS_TC, TAG_TC_TEXT, NS_VALUE_TC);
			w.writeCharacters(ds.getText());
			w.writeEndElement();
		} catch (XMLStreamException e) {}
		w = null;
	}
	
	private void mapTokenization(List<SToken> sTokens){		
		//TODO sTokens supposed to be ordered!
		if (!sTokens.isEmpty()){
			XMLStreamWriter w = currentTCF;
			try {
				w.writeStartElement(NS_TC, TAG_TC_TOKENS, NS_VALUE_TC);
				SDocumentGraph sDocGraph = getDocument().getDocumentGraph();
				int i = 0;
				String id = null;
				String sText = null;				
				for (SToken sTok : sTokens){
					sText = sDocGraph.getText(sTok);
					if (emptyTokensAllowed || !sText.replace(" ","").replace(System.getProperty("line.separator"),"").replace("\t", "").isEmpty()){
						i++;
						id = "t_"+i;
						sNodes.put(sTok, id);
						w.writeStartElement(NS_TC, TAG_TC_TOKEN, NS_VALUE_TC);
						w.writeAttribute(ATT_ID, id);
						w.writeCharacters(sText);
						w.writeEndElement();//end of token
					}else{
						emptyTokens.add(sTok);
					}
				}
				w.writeEndElement();//end of tokens
			} catch (XMLStreamException e) {}
		}
	}
	
	private void mapSentences(){		
		/* write */
		XMLStreamWriter w = currentTCF;
		SDocumentGraph sDocGraph = getDocument().getDocumentGraph();
		List<SSpan> sSpans = new ArrayList<SSpan>();
		for (SSpan sSpan : getDocument().getDocumentGraph().getSpans()){
			if (sSpan.getAnnotation(qNameSentence)!=null && sSpan.getAnnotation(qNameSentence).getValue().toString().equals(valueSentence)){
				sSpans.add(sSpan);
			}
		}
		List<SToken> sTokens = null;
		String value = "";
		try {
			if (!sSpans.isEmpty()){			
				w.writeStartElement(NS_TC, TAG_TC_SENTENCES, NS_VALUE_TC);
				SSpan sSpan = null;
				for (int j=0; j<sSpans.size(); j++){
					sSpan = sSpans.get(j);			
					sTokens = sDocGraph.getOverlappedTokens(sSpan, SALT_TYPE.SSPANNING_RELATION);
					sTokens = sDocGraph.getSortedTokenByText(sTokens);					
					w.writeStartElement(NS_TC, TAG_TC_SENTENCE, NS_VALUE_TC);
					w.writeAttribute(ATT_ID, "s_"+(j+1));					
					for (SToken sTok : sTokens){
						value+= emptyTokens.contains(sTok)? "" : " "+sNodes.get(sTok);
					}					
					w.writeAttribute(ATT_TOKENIDS, value.trim());
					w.writeEndElement();
					value = "";			
				}
				w.writeEndElement();//end of sentences				
			}
		} catch (XMLStreamException e) {
		}
	}
	
	private void mapPOSAnnotations(){
		XMLStreamWriter w = currentTCF;
		List<SAnnotation> sAnnos = new ArrayList<SAnnotation>();
		SAnnotation anno = null;
		for (SToken sTok : getDocument().getDocumentGraph().getSortedTokenByText()){
			anno = sTok.getAnnotation(qNamePOS);
			if (anno!=null){
				sAnnos.add(anno);
			}
		}
		try{
			if (!sAnnos.isEmpty()){
				w.writeStartElement(NS_TC, TAG_TC_POSTAGS, NS_VALUE_TC);
				w.writeAttribute(ATT_TAGSET, "stts"/*TODO*/);
				int k=1;
				for (SAnnotation sAnno : sAnnos){
					w.writeStartElement(NS_TC, TAG_TC_TAG, NS_VALUE_TC);
					w.writeAttribute(ATT_ID, "pt_"+k++);
					w.writeAttribute(ATT_TOKENIDS, sNodes.get(sAnno.getContainer()));
					w.writeCharacters(sAnno.getValue().toString());
					w.writeEndElement();//end of tag
				}
				w.writeEndElement();//end of POSTags
			}
		}catch (XMLStreamException e){
			throw new PepperModuleDataException(this, "Failed to write POS-annotations.");
		}
	}
	
	private void mapLemmaAnnotations(){
		XMLStreamWriter w = currentTCF;
		List<SAnnotation> sAnnos = new ArrayList<SAnnotation>();
		SAnnotation anno = null;
		for (SToken sTok : getDocument().getDocumentGraph().getSortedTokenByText()){
			anno = sTok.getAnnotation(qNameLemma);
			if (anno!=null){
				sAnnos.add(anno);
			}
		}
		try{
			if (!sAnnos.isEmpty()){
				w.writeStartElement(NS_TC, TAG_TC_LEMMAS, NS_VALUE_TC);
				int k=1;
				for (SAnnotation sAnno : sAnnos){
					w.writeStartElement(NS_TC, TAG_TC_LEMMA, NS_VALUE_TC);
					w.writeAttribute(ATT_ID, "le_"+k++);
					w.writeAttribute(ATT_TOKENIDS, sNodes.get(sAnno.getContainer()));
					w.writeCharacters(sAnno.getValue().toString());
					w.writeEndElement();
				}
				w.writeEndElement();
			}
		}catch (XMLStreamException e){
			logger.warn("Failed to write lemma annotations.");
		}
	}
	
	private void mapLayoutAnnotations(){
		/* collect all relevant spans */
		List<SNode> layoutNodes = new ArrayList<SNode>(); 
		SAnnotation anno = null;
		for (SNode sNode : getDocument().getDocumentGraph().getNodes()){
			anno = sNode.getAnnotation(qNameLine);
			if (anno!=null && anno.getValue_STEXT().equals(valueLine)){
				layoutNodes.add(sNode);				
			}
			anno = sNode.getAnnotation(qNamePage);
			if (anno!=null && anno.getValue_STEXT().equals(valuePage)){
				layoutNodes.add(sNode);
			}			
		}
		//TODO Spans are maybe not sorted by Text
		if (!layoutNodes.isEmpty()){
			XMLStreamWriter w = currentTCF;
			try {
				w.writeStartElement(NS_TC, TAG_TC_TEXTSTRUCTURE, NS_VALUE_TC);
				List<SToken> sTokens = null;
				String type = null;
				for (SNode sNode : layoutNodes){					
					sTokens = getDocument().getDocumentGraph().getSortedTokenByText(getDocument().getDocumentGraph().getOverlappedTokens(sNode, SALT_TYPE.SSPANNING_RELATION, SALT_TYPE.SDOMINANCE_RELATION));
					List<SToken> realTokens = new ArrayList<SToken>();
					for (SToken sTok : sTokens){
						if (!emptyTokens.contains(sTok)){
							realTokens.add(sTok);
						}
					}
					if (!realTokens.isEmpty()){
						w.writeStartElement(NS_TC, TAG_TC_TEXTSPAN, NS_VALUE_TC);
						w.writeAttribute(ATT_START, sNodes.get(realTokens.get(0)));
						w.writeAttribute(ATT_END, sNodes.get(realTokens.get(realTokens.size()-1)));
						type = sNode.getAnnotation(qNamePage)!=null && sNode.getAnnotation(qNamePage).getValue().equals(valuePage)? "page" : 
							(sNode.getAnnotation(qNameLine)!=null && sNode.getAnnotation(qNameLine).getValue().equals(valueLine)? "line" : "IMPOSSIBLE RIGHT NOW"/*to be continued*/); 
						w.writeAttribute(ATT_TYPE, type);
						w.writeEndElement();
					}					
				}
				w.writeEndElement();
			} catch (XMLStreamException e) {}
		}
	}
}
