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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hu_berlin.german.korpling.saltnpepper.pepper.common.DOCUMENT_STATUS;
import de.hu_berlin.german.korpling.saltnpepper.pepper.modules.exceptions.PepperModuleDataException;
import de.hu_berlin.german.korpling.saltnpepper.pepper.modules.impl.PepperMapperImpl;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDocumentGraph;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SSpan;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.STYPE_NAME;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.STextualDS;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SToken;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SAnnotation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SNode;

public class TCFMapperExport extends PepperMapperImpl implements TCFDictionary{
	
	private static final Logger logger = LoggerFactory.getLogger(TCFMapperExport.class);
	
	private Stack<XMLStreamWriter> TCFs = null;
	private HashMap<String, String> meta = null;
	private HashMap<SNode, String> sNodes = null;
	private String qNameLine = null;
	private String valueLine = null;
	private String qNamePage = null;
	private String valuePage = null;
		
	public TCFMapperExport(){
	}
	
	public void init(){
		TCFs = new Stack<XMLStreamWriter>();
		sNodes = new HashMap<SNode, String>();
		qNameLine = ((TCFExporterProperties)getProperties()).getTextstructureLineName();
		valueLine = ((TCFExporterProperties)getProperties()).getTextstructureLineValue();
		qNamePage = ((TCFExporterProperties)getProperties()).getTextstructurePageName();
		valuePage = ((TCFExporterProperties)getProperties()).getTextstructurePageValue();
		initMeta();
	}
	
	private void initMeta(){
		meta = new HashMap<String, String>();
//		if (getSDocument()!=null){//FIXME I think this is always false
//			meta.put(ATT_LANG, getSDocument().getSMetaAnnotation(ATT_LANG).getValue().toString()); //FIXME --> by properties we will find out where this meta attribute is
//		}
		//lang can be multiple value: parallel corpora		
	}
	
	/** this method maps an SDocument to TCF */
	@Override
	public DOCUMENT_STATUS mapSDocument(){
		init();
		if (getSDocument()==null){
			throw new PepperModuleDataException(this, "No document delivered to be converted.");
		}
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		XMLOutputFactory factory = XMLOutputFactory.newFactory();
		for (STextualDS sTextualDS : getSDocument().getSDocumentGraph().getSTextualDSs()){
			//for each tokenization do:			
			XMLStreamWriter w;
			try {
				w = TCFs.push(factory.createXMLStreamWriter(outStream));
				w.writeStartDocument();
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
				mapTokenization(getSDocument().getSDocumentGraph().getSortedSTokenByText());
				mapLayoutAnnotations();
				w.writeEndElement();
				w.writeEndDocument();
				w.writeEndDocument();
			} catch (XMLStreamException e) {}
			File file = null;			
			while(!TCFs.isEmpty()){
				w = TCFs.pop();
				file = new File(getResourceURI().toFileString());//FIXME we need a language stack, too, in case of parallel corpora
				file.getParentFile().mkdirs();
				PrintWriter p;
				try {
					p = new PrintWriter(file);
					p.println(outStream.toString());
					p.close();
				} catch (FileNotFoundException e) {	
					logger.error("Could not write TCF "+getResourceURI(), e);
				}		
			}
			w = null;
		}
		return DOCUMENT_STATUS.COMPLETED;
	}
	
	private String getLanguage(){
		//TODO
		return "en";
	}
	
	private void mapSTextualDS(STextualDS ds){
		XMLStreamWriter w = TCFs.peek();
		try {
			w.writeStartElement(NS_TC, TAG_TC_TEXT, NS_VALUE_TC);
			w.writeCharacters(ds.getSText());
			w.writeEndElement();
		} catch (XMLStreamException e) {}
		w = null;
	}
	
	private void mapTokenization(List<SToken> sTokens){		
		//TODO sTokens supposed to be ordered!
		if (!sTokens.isEmpty()){
			XMLStreamWriter w = TCFs.peek();
			try {
				w.writeStartElement(NS_TC, TAG_TC_TOKENS, NS_VALUE_TC);
				SDocumentGraph sDocGraph = getSDocument().getSDocumentGraph();
				int i = 0;
				String id = null;
				for (SToken sTok : sTokens){				
					i++;
					id = "t_"+i;
					sNodes.put(sTok, id);
					w.writeStartElement(NS_TC, TAG_TC_TOKEN, NS_VALUE_TC);
					w.writeAttribute(ATT_ID, id);
					w.writeCharacters(sDocGraph.getSText(sTok));
					w.writeEndElement();//end of token
				}
				w.writeEndElement();//end of tokens
			} catch (XMLStreamException e) {}
		}
	}
	
	private void mapLayoutAnnotations(){
		/* collect all relevant spans */
		List<SNode> layoutNodes = new ArrayList<SNode>(); 
		SAnnotation anno = null;
		for (SNode sNode : getSDocument().getSDocumentGraph().getSNodes()){
			anno = sNode.getSAnnotation(qNameLine);
			if (anno!=null && anno.getSValueSTEXT().equals(valueLine)){
				layoutNodes.add(sNode);				
			}
			anno = sNode.getSAnnotation(qNamePage);
			if (anno!=null && anno.getSValueSTEXT().equals(valuePage)){
				layoutNodes.add(sNode);
			}			
		}
		//TODO Spans are maybe not sorted by Text
		if (!layoutNodes.isEmpty()){
			XMLStreamWriter w = TCFs.peek();
			try {
				w.writeStartElement(NS_TC, TAG_TC_TEXTSTRUCTURE, NS_VALUE_TC);
				EList<STYPE_NAME> sTypes = new BasicEList<STYPE_NAME>();
				sTypes.add(STYPE_NAME.SSPANNING_RELATION);
				sTypes.add(STYPE_NAME.SDOMINANCE_RELATION);
				List<SToken> sTokens = null;
				String type = null;
				for (SNode sNode : layoutNodes){
					w.writeStartElement(NS_TC, TAG_TC_TEXTSPAN, NS_VALUE_TC);
					sTokens = getSDocument().getSDocumentGraph().getSortedSTokenByText(getSDocument().getSDocumentGraph().getOverlappedSTokens(sNode, sTypes));					
					w.writeAttribute(ATT_START, sNodes.get(sTokens.get(0)));
					w.writeAttribute(ATT_END, sNodes.get(sTokens.get(sTokens.size()-1)));
					type = sNode.getSAnnotation(qNamePage)!=null? sNode.getSAnnotation(qNamePage).getValue().toString() : 
						(sNode.getSAnnotation(qNameLine)!=null? sNode.getSAnnotation(qNameLine).getValue().toString() : "IMPOSSIBLE RIGHT NOW"/*to be continued*/); 
					w.writeAttribute(ATT_TYPE, type);
					w.writeEndElement();
				}
				w.writeEndElement();
			} catch (XMLStreamException e) {}
		}
	}
}
