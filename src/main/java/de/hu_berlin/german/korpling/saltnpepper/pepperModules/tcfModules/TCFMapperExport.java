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
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import de.hu_berlin.german.korpling.saltnpepper.pepper.common.DOCUMENT_STATUS;
import de.hu_berlin.german.korpling.saltnpepper.pepper.modules.impl.PepperMapperImpl;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SDocument;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDataSourceSequence;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDocumentGraph;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.STextualDS;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SToken;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SNode;

public class TCFMapperExport extends PepperMapperImpl implements TCFDictionary{
	/** the SDocument to be mapped */
	private SDocument document = null;
	private Stack<XMLStreamWriter> TCFs = null;
	private HashMap<String, String> meta = null;
	private HashMap<SNode, String> sNodes = null;
	
	public TCFMapperExport(SDocument sDocument){
		this.document = sDocument;//FIXME I guess I should better use getSDocument(), but I did not really get into the data flow yet
		TCFs = new Stack<XMLStreamWriter>();
		sNodes = new HashMap<SNode, String>();
		initMeta();		
	}
	
	private void initMeta(){
		meta = new HashMap<String, String>();
		meta.put(ATT_LANG, document.getSMetaAnnotation(ATT_LANG).getValue().toString()); //FIXME --> by properties we will find out where this meta attribute is
		//lang can be multiple value: parallel corpora		
	}
	
	/** this method maps an SDocument to TCF */
	@Override
	public DOCUMENT_STATUS mapSDocument(){
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		XMLOutputFactory factory = XMLOutputFactory.newFactory();
		for (STextualDS sTextualDS : document.getSDocumentGraph().getSTextualDSs()){
			//for each tokenization do:			
			XMLStreamWriter w;
			try {
				w = TCFs.push(factory.createXMLStreamWriter(outStream));
				w.writeStartDocument();
				w.writeNamespace(NS_ED, NS_VALUE_ED);
				w.writeNamespace(NS_LX, NS_VALUE_LX);
				w.writeNamespace(NS_MD, NS_VALUE_MD);
				w.writeNamespace(NS_TC, NS_VALUE_TC);
				w.writeNamespace(NS_WL, NS_VALUE_WL);
				w.writeNamespace(NS_XSI, NS_VALUE_XSI);
				w.writeStartElement(NS_TC, TAG_TC_TEXTCORPUS, NS_VALUE_TC);
				w.writeAttribute(ATT_LANG, meta.get(ATT_LANG));//TODO see above
				mapSTextualDS(sTextualDS);
				mapTokenization(document.getSDocumentGraph().getSTokensBySequence((SDataSourceSequence)sTextualDS));//TODO --> Tokens in order?
				w.writeEndElement();
				w.writeEndDocument();
			} catch (XMLStreamException e) {}
			File file = null;			
			while(TCFs.peek()!=null){
				w = TCFs.pop();
				file = new File(getResourceURI().toFileString()+"_"+meta.get(ATT_LANG));//FIXME we need a language stack, too, in case of parallel corpora
				PrintWriter p;
				try {
					p = new PrintWriter(file);
					p.println(outStream.toString());
					p.close();
				} catch (FileNotFoundException e) {
					//TODO
				}		
			}
			w = null;
		}
		return DOCUMENT_STATUS.COMPLETED;
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
		XMLStreamWriter w = TCFs.peek();
		try {
			w.writeStartElement(NS_TC, TAG_TC_TOKENS, NS_VALUE_TC);
			SDocumentGraph sDocGraph = document.getSDocumentGraph();
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
	
	private void mapLayoutAnnotations(){
		/*in TCF:Textstructure*/
	}
}
