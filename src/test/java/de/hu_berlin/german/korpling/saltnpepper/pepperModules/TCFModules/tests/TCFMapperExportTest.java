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
package de.hu_berlin.german.korpling.saltnpepper.pepperModules.TCFModules.tests;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.eclipse.emf.common.util.URI;
import org.junit.Before;
import org.junit.Test;

import de.hu_berlin.german.korpling.saltnpepper.pepperModules.tcfModules.TCFDictionary;
import de.hu_berlin.german.korpling.saltnpepper.pepperModules.tcfModules.TCFExporterProperties;
import de.hu_berlin.german.korpling.saltnpepper.pepperModules.tcfModules.TCFMapperExport;
import de.hu_berlin.german.korpling.saltnpepper.salt.SaltFactory;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SDocument;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDocumentGraph;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SSpan;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.STYPE_NAME;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SToken;
import de.hu_berlin.german.korpling.saltnpepper.salt.samples.SampleGenerator;

public class TCFMapperExportTest {
	
	private TCFMapperExport fixture = null;
	private static final String FOLDER_PEPPER_TEST = "/pepper-test/";
	private static final String SNAME_TEST_PRIMARY_TEXT = "ExporterTestPrimaryData.tcf";
	private static final String SNAME_TEST_TOKENS = "ExporterTestPrimaryData.tcf";
	private static final String SNAME_TEST_TEXTSTRUCTURE = "ExporterTestTextstructure.tcf";
	
	public TCFMapperExport getFixture(){
		return fixture;
	}
	
	public void setFixture(TCFMapperExport fixture){
		this.fixture = fixture;
	}
	
	@Before
	public void setUp(){		
		setFixture(new TCFMapperExport());		
		getFixture().setProperties(new TCFExporterProperties());
	}
	
	private void writeStartAndPrimaryText(XMLStreamWriter xmlWriter) throws XMLStreamException{
		xmlWriter.writeStartDocument();
		xmlWriter.writeStartElement(TCFDictionary.NS_WL, TCFDictionary.TAG_WL_D_SPIN, TCFDictionary.NS_VALUE_WL);				
		xmlWriter.writeNamespace(TCFDictionary.NS_ED, TCFDictionary.NS_VALUE_ED);
		xmlWriter.writeNamespace(TCFDictionary.NS_LX, TCFDictionary.NS_VALUE_LX);
		xmlWriter.writeNamespace(TCFDictionary.NS_MD, TCFDictionary.NS_VALUE_MD);
		xmlWriter.writeNamespace(TCFDictionary.NS_TC, TCFDictionary.NS_VALUE_TC);
		xmlWriter.writeNamespace(TCFDictionary.NS_WL, TCFDictionary.NS_VALUE_WL);
		xmlWriter.writeAttribute(TCFDictionary.ATT_VERSION, "0.4");
		xmlWriter.writeStartElement(TCFDictionary.NS_MD, TCFDictionary.TAG_MD_METADATA, TCFDictionary.NS_VALUE_MD);
		xmlWriter.writeEndElement();
		xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TEXTCORPUS, TCFDictionary.NS_VALUE_TC);
		xmlWriter.writeAttribute(TCFDictionary.ATT_LANG, "en");
		xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TEXT, TCFDictionary.NS_VALUE_TC);
		xmlWriter.writeCharacters(SampleGenerator.PRIMARY_TEXT_EN);
		xmlWriter.writeEndElement();
	}
	
	/** writes tokens t_1 to t_11 (Is this example more complicated than it appears to be?) as valid tcf xml */
	private void writeTokens(XMLStreamWriter xmlWriter) throws XMLStreamException{
		xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKENS, TCFDictionary.NS_VALUE_TC);
		xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
		xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t_1");
		xmlWriter.writeCharacters("Is");
		xmlWriter.writeEndElement();
		xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
		xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t_2");
		xmlWriter.writeCharacters("this");
		xmlWriter.writeEndElement();
		xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
		xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t_3");
		xmlWriter.writeCharacters("example");
		xmlWriter.writeEndElement();
		xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
		xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t_4");
		xmlWriter.writeCharacters("more");
		xmlWriter.writeEndElement();
		xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
		xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t_5");
		xmlWriter.writeCharacters("complicated");
		xmlWriter.writeEndElement();
		xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
		xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t_6");
		xmlWriter.writeCharacters("than");
		xmlWriter.writeEndElement();
		xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
		xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t_7");
		xmlWriter.writeCharacters("it");
		xmlWriter.writeEndElement();
		xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
		xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t_8");
		xmlWriter.writeCharacters("appears");
		xmlWriter.writeEndElement();
		xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
		xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t_9");
		xmlWriter.writeCharacters("to");
		xmlWriter.writeEndElement();
		xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
		xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t_10");
		xmlWriter.writeCharacters("be");
		xmlWriter.writeEndElement();
		xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
		xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t_11");
		xmlWriter.writeCharacters("?");
		xmlWriter.writeEndElement();//end of token
		xmlWriter.writeEndElement();//end of tokens
	}
	
	/*
	 * TODO use XMLDiff von XUnit 
	 */
	
	
	@Test
	public void testPrimaryText() throws XMLStreamException, IOException{
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		XMLOutputFactory o= XMLOutputFactory.newFactory();
		XMLStreamWriter xmlWriter= o.createXMLStreamWriter(outStream);
		
		/* creating TCF */		
		writeStartAndPrimaryText(xmlWriter);
		xmlWriter.writeEndElement();//end of textcorpus
		xmlWriter.writeEndDocument();		
		
		/* creating SDocument */
		SDocument sDocument = SaltFactory.eINSTANCE.createSDocument();
		sDocument.setSName(SNAME_TEST_PRIMARY_TEXT);
		sDocument.createSMetaAnnotation(null, TCFDictionary.ATT_LANG, "en");
		SampleGenerator.createPrimaryData(sDocument, SampleGenerator.LANG_EN);		
		
		/* setting variables*/		
		this.getFixture().setResourceURI(URI.createFileURI(System.getProperty("java.io.tmpdir")+FOLDER_PEPPER_TEST+SNAME_TEST_PRIMARY_TEXT));
		
		/* start mapper */
		this.getFixture().setSDocument(sDocument);
		this.getFixture().mapSDocument();
		
		/* tests */
		File fixFile = new File(getFixture().getResourceURI().toFileString());
		BufferedReader reader = new BufferedReader(new FileReader(fixFile));
		assertEquals(outStream.toString(), reader.readLine());
		reader.close();
	}
	
	@Test
	public void testTokenization() throws XMLStreamException, IOException{
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		XMLOutputFactory o= XMLOutputFactory.newFactory();
		XMLStreamWriter xmlWriter= o.createXMLStreamWriter(outStream);
		
		/* creating TCF */
		writeStartAndPrimaryText(xmlWriter);
		writeTokens(xmlWriter);
		xmlWriter.writeEndElement();//end of textcorpus
		xmlWriter.writeEndDocument();
		
		/* creating SDocument */
		SDocument sDocument = SaltFactory.eINSTANCE.createSDocument();
		sDocument.setSName(SNAME_TEST_TOKENS);
		sDocument.createSMetaAnnotation(null, TCFDictionary.ATT_LANG, "en");
		SampleGenerator.createPrimaryData(sDocument, SampleGenerator.LANG_EN);
		SampleGenerator.createTokens(sDocument);
		
		/* setting variables*/		
		this.getFixture().setResourceURI(URI.createFileURI(System.getProperty("java.io.tmpdir")+FOLDER_PEPPER_TEST+SNAME_TEST_TOKENS));
		
		/* start mapper */
		this.getFixture().setSDocument(sDocument);
		this.getFixture().mapSDocument();
		
		/* tests */
		File fixFile = new File(getFixture().getResourceURI().toFileString());
		BufferedReader reader = new BufferedReader(new FileReader(fixFile));
		assertEquals(outStream.toString(), reader.readLine());
		reader.close();
	}
	
	@Test
	public void testLayoutAnnotations() throws XMLStreamException, IOException{
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		XMLOutputFactory o= XMLOutputFactory.newFactory();
		XMLStreamWriter xmlWriter= o.createXMLStreamWriter(outStream);
		
		/* creating TCF */		
		writeStartAndPrimaryText(xmlWriter);
		writeTokens(xmlWriter);		
		xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TEXTSTRUCTURE, TCFDictionary.NS_VALUE_TC);
		
		xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TEXTSPAN, TCFDictionary.NS_VALUE_TC);
		xmlWriter.writeAttribute(TCFDictionary.ATT_START, "t_1");
		xmlWriter.writeAttribute(TCFDictionary.ATT_END, "t_11");
		xmlWriter.writeAttribute(TCFDictionary.ATT_TYPE, "page");
		xmlWriter.writeEndElement();
		
		xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TEXTSPAN, TCFDictionary.NS_VALUE_TC);
		xmlWriter.writeAttribute(TCFDictionary.ATT_START, "t_1");
		xmlWriter.writeAttribute(TCFDictionary.ATT_END, "t_5");
		xmlWriter.writeAttribute(TCFDictionary.ATT_TYPE, "line");
		xmlWriter.writeEndElement();
		
		xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TEXTSPAN, TCFDictionary.NS_VALUE_TC);
		xmlWriter.writeAttribute(TCFDictionary.ATT_START, "t_6");
		xmlWriter.writeAttribute(TCFDictionary.ATT_END, "t_11");
		xmlWriter.writeAttribute(TCFDictionary.ATT_TYPE, "line");
		xmlWriter.writeEndElement();
		
		xmlWriter.writeEndElement();//end of textstructure
		xmlWriter.writeEndElement();//end of textcorpus
		xmlWriter.writeEndDocument();		
		
		/* creating SDocument */
		SDocument sDocument = SaltFactory.eINSTANCE.createSDocument();
		sDocument.setSName(SNAME_TEST_PRIMARY_TEXT);
		sDocument.createSMetaAnnotation(null, TCFDictionary.ATT_LANG, "en");
		SampleGenerator.createPrimaryData(sDocument, SampleGenerator.LANG_EN);
		SampleGenerator.createTokens(sDocument);
		SDocumentGraph sDocGraph = sDocument.getSDocumentGraph();
		TCFExporterProperties properties = new TCFExporterProperties();
		String qNamePage = properties.getTextstructurePageName();
		String valuePage = properties.getTextstructurePageValue();
		String qNameLine = properties.getTextstructureLineName();
		String valueLine = properties.getTextstructureLineValue();
		
		sDocGraph.createSSpan(sDocument.getSDocumentGraph().getSortedSTokenByText()).createSAnnotation(null, qNamePage, valuePage);
		List<SToken> sTokens = sDocGraph.getSortedSTokenByText();
		SSpan sSpan = sDocGraph.createSSpan(sTokens.get(0));
		sSpan.createSAnnotation(null, qNameLine, valueLine);
		for (int i=1; i<5; i++){
			sDocGraph.createSRelation(sSpan, sTokens.get(i), STYPE_NAME.SSPANNING_RELATION, null);
		}
		sSpan = sDocGraph.createSSpan(sTokens.get(5));
		sSpan.createSAnnotation(null, qNameLine, valueLine);
		for (int i=6; i<sTokens.size(); i++){
			sDocGraph.createSRelation(sSpan, sTokens.get(i), STYPE_NAME.SSPANNING_RELATION, null);
		}
		
		/* setting variables*/		
		this.getFixture().setResourceURI(URI.createFileURI(System.getProperty("java.io.tmpdir")+FOLDER_PEPPER_TEST+SNAME_TEST_TEXTSTRUCTURE));
		this.getFixture().setProperties(properties);
		
		/* start mapper */
		this.getFixture().setSDocument(sDocument);
		this.getFixture().mapSDocument();
		
		/* tests */
		File fixFile = new File(getFixture().getResourceURI().toFileString());
		BufferedReader reader = new BufferedReader(new FileReader(fixFile));
		assertEquals(outStream.toString(), reader.readLine());
		reader.close();
	}
}
