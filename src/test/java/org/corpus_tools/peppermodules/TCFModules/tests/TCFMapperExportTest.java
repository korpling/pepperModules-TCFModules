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
package org.corpus_tools.peppermodules.TCFModules.tests;

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

import org.corpus_tools.peppermodules.tcfModules.TCFDictionary;
import org.corpus_tools.peppermodules.tcfModules.TCFExporterProperties;
import org.corpus_tools.peppermodules.tcfModules.TCFMapperExport;
import org.corpus_tools.salt.SALT_TYPE;
import org.corpus_tools.salt.SaltFactory;
import org.corpus_tools.salt.common.SDocument;
import org.corpus_tools.salt.common.SDocumentGraph;
import org.corpus_tools.salt.common.SSpan;
import org.corpus_tools.salt.common.SToken;
import org.corpus_tools.salt.samples.SampleGenerator;
import org.corpus_tools.salt.util.SaltUtil;
import org.eclipse.emf.common.util.URI;
import org.junit.Before;
import org.junit.Test;

public class TCFMapperExportTest {

	private TCFMapperExport fixture = null;
	private static final String FOLDER_PEPPER_TEST = "/pepper-test/";
	private static final String SNAME_TEST_PRIMARY_TEXT = "ExporterTestPrimaryData.tcf";
	private static final String SNAME_TEST_TOKENS = "ExporterTestTokens.tcf";
	private static final String SNAME_TEST_TEXTSTRUCTURE = "ExporterTestTextstructure.tcf";
	private static final String SNAME_TEST_SENTENCES = "ExporterTestSentences.tcf";
	private static final String SNAME_TEST_POS = "ExporterTestPOS.tcf";
	private static final String SNAME_TEST_LEMMA = "ExporterTestLemma.tcf";

	public TCFMapperExport getFixture() {
		return fixture;
	}

	public void setFixture(TCFMapperExport fixture) {
		this.fixture = fixture;
	}

	@Before
	public void setUp() {
		setFixture(new TCFMapperExport());
		getFixture().setProperties(new TCFExporterProperties());
	}

	private void writeStartAndPrimaryText(XMLStreamWriter xmlWriter) throws XMLStreamException {
		xmlWriter.writeStartDocument();
		xmlWriter.writeProcessingInstruction(TCFDictionary.TCF_PI);
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
		xmlWriter.writeAttribute(TCFDictionary.ATT_LANG, "x-unspecified");
		xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TEXT, TCFDictionary.NS_VALUE_TC);
		xmlWriter.writeCharacters(SampleGenerator.PRIMARY_TEXT_EN);
		xmlWriter.writeEndElement();
	}

	/**
	 * writes tokens t_1 to t_11 (Is this example more complicated than it
	 * appears to be?) as valid tcf xml
	 */
	private void writeTokens(XMLStreamWriter xmlWriter) throws XMLStreamException {
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
		xmlWriter.writeEndElement();// end of token
		xmlWriter.writeEndElement();// end of tokens
	}

	/*
	 * TODO use XMLDiff von XUnit
	 */

	@Test
	public void testPrimaryText() throws XMLStreamException, IOException {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		XMLOutputFactory o = XMLOutputFactory.newFactory();
		XMLStreamWriter xmlWriter = o.createXMLStreamWriter(outStream);

		/* creating TCF */
		writeStartAndPrimaryText(xmlWriter);
		xmlWriter.writeEndElement();// end of textcorpus
		xmlWriter.writeEndDocument();

		/* creating SDocument */
		SDocument sDocument = SaltFactory.createSDocument();
		sDocument.setName(SNAME_TEST_PRIMARY_TEXT);
		sDocument.createMetaAnnotation(null, TCFDictionary.ATT_LANG, "en");
		SampleGenerator.createPrimaryData(sDocument, SampleGenerator.LANG_EN);

		/* setting variables */
		getFixture().setResourceURI(URI.createFileURI(System.getProperty("java.io.tmpdir") + FOLDER_PEPPER_TEST + SNAME_TEST_PRIMARY_TEXT));

		/* start mapper */
		getFixture().setDocument(sDocument);
		getFixture().mapSDocument();

		/* tests */
		File fixFile = new File(getFixture().getResourceURI().toFileString());
		BufferedReader reader = new BufferedReader(new FileReader(fixFile));
		assertEquals(outStream.toString(), reader.readLine());
		reader.close();
	}

	@Test
	public void testTokenization() throws XMLStreamException, IOException {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		XMLOutputFactory o = XMLOutputFactory.newFactory();
		XMLStreamWriter xmlWriter = o.createXMLStreamWriter(outStream);

		/* creating TCF */
		writeStartAndPrimaryText(xmlWriter);
		writeTokens(xmlWriter);
		xmlWriter.writeEndElement();// end of textcorpus
		xmlWriter.writeEndDocument();

		/* creating SDocument */
		SDocument sDocument = SaltFactory.createSDocument();
		sDocument.setName(SNAME_TEST_TOKENS);
		sDocument.createMetaAnnotation(null, TCFDictionary.ATT_LANG, "en");
		SampleGenerator.createPrimaryData(sDocument, SampleGenerator.LANG_EN);
		SampleGenerator.createTokens(sDocument);

		/* setting variables */
		getFixture().setResourceURI(URI.createFileURI(System.getProperty("java.io.tmpdir") + FOLDER_PEPPER_TEST + SNAME_TEST_TOKENS));

		/* start mapper */
		getFixture().setDocument(sDocument);
		getFixture().mapSDocument();

		/* tests */
		File fixFile = new File(getFixture().getResourceURI().toFileString());
		BufferedReader reader = new BufferedReader(new FileReader(fixFile));
		assertEquals(outStream.toString(), reader.readLine());
		reader.close();
	}

	@Test
	public void testLayoutAnnotations() throws XMLStreamException, IOException {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		XMLOutputFactory o = XMLOutputFactory.newFactory();
		XMLStreamWriter xmlWriter = o.createXMLStreamWriter(outStream);

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

		xmlWriter.writeEndElement();// end of textstructure
		xmlWriter.writeEndElement();// end of textcorpus
		xmlWriter.writeEndDocument();

		/* creating SDocument */
		SDocument sDocument = SaltFactory.createSDocument();
		sDocument.setName(SNAME_TEST_TEXTSTRUCTURE);
		sDocument.createMetaAnnotation(null, TCFDictionary.ATT_LANG, "en");
		SampleGenerator.createPrimaryData(sDocument, SampleGenerator.LANG_EN);
		SampleGenerator.createTokens(sDocument);
		SDocumentGraph sDocGraph = sDocument.getDocumentGraph();
		TCFExporterProperties properties = new TCFExporterProperties();
		String qNamePage = properties.getTextstructurePageName();
		String valuePage = properties.getTextstructurePageValue();
		String qNameLine = properties.getTextstructureLineName();
		String valueLine = properties.getTextstructureLineValue();

		sDocGraph.createSpan(sDocument.getDocumentGraph().getSortedTokenByText()).createAnnotation(null, qNamePage, valuePage);
		List<SToken> sTokens = sDocGraph.getSortedTokenByText();
		SSpan sSpan = sDocGraph.createSpan(sTokens.get(0));
		sSpan.createAnnotation(null, qNameLine, valueLine);
		for (int i = 1; i < 5; i++) {
			sDocGraph.createRelation(sSpan, sTokens.get(i), SALT_TYPE.SSPANNING_RELATION, null);
		}
		sSpan = sDocGraph.createSpan(sTokens.get(5));
		sSpan.createAnnotation(null, qNameLine, valueLine);
		for (int i = 6; i < sTokens.size(); i++) {
			sDocGraph.createRelation(sSpan, sTokens.get(i), SALT_TYPE.SSPANNING_RELATION, null);
		}

		/* setting variables */
		getFixture().setResourceURI(URI.createFileURI(System.getProperty("java.io.tmpdir") + FOLDER_PEPPER_TEST + SNAME_TEST_TEXTSTRUCTURE));
		getFixture().setProperties(properties);

		/* start mapper */
		getFixture().setDocument(sDocument);
		getFixture().mapSDocument();

		/* tests */
		File fixFile = new File(getFixture().getResourceURI().toFileString());
		BufferedReader reader = new BufferedReader(new FileReader(fixFile));
		assertEquals(outStream.toString(), reader.readLine());
		reader.close();
	}

	@Test
	public void testSentences() throws XMLStreamException, IOException {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		XMLOutputFactory o = XMLOutputFactory.newFactory();
		XMLStreamWriter xmlWriter = o.createXMLStreamWriter(outStream);

		/* creating TCF */
		writeStartAndPrimaryText(xmlWriter);
		writeTokens(xmlWriter);
		xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_SENTENCES, TCFDictionary.NS_VALUE_TC);
		xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_SENTENCE, TCFDictionary.NS_VALUE_TC);
		xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "s_1");
		xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t_1 t_2 t_3 t_4 t_5");
		xmlWriter.writeEndElement();
		xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_SENTENCE, TCFDictionary.NS_VALUE_TC);
		xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "s_2");
		xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t_6 t_7 t_8 t_9 t_10 t_11");
		xmlWriter.writeEndElement();

		xmlWriter.writeEndElement();// end of senctences
		xmlWriter.writeEndElement();// end of textcorpus
		xmlWriter.writeEndDocument();

		/* creating SDocument */
		SDocument sDocument = SaltFactory.createSDocument();
		sDocument.setName(SNAME_TEST_SENTENCES);
		sDocument.createMetaAnnotation(null, TCFDictionary.ATT_LANG, "en");
		SampleGenerator.createPrimaryData(sDocument, SampleGenerator.LANG_EN);
		SampleGenerator.createTokens(sDocument);
		SDocumentGraph sDocGraph = sDocument.getDocumentGraph();
		List<SToken> sTokens = sDocGraph.getSortedTokenByText();
		SSpan sSpan = sDocGraph.createSpan(sTokens.get(0));
		for (int i = 1; i < 5; i++) {
			sDocGraph.createRelation(sSpan, sTokens.get(i), SALT_TYPE.SSPANNING_RELATION, null);
		}
		sSpan.createAnnotation(null, "sentence", "sentence");
		sSpan = sDocGraph.createSpan(sTokens.get(5));
		for (int i = 6; i < sTokens.size(); i++) {
			sDocGraph.createRelation(sSpan, sTokens.get(i), SALT_TYPE.SSPANNING_RELATION, null);
		}
		sSpan.createAnnotation(null, "sentence", "sentence");

		/* setting variables */
		getFixture().setResourceURI(URI.createFileURI(System.getProperty("java.io.tmpdir") + FOLDER_PEPPER_TEST + SNAME_TEST_SENTENCES));
		getFixture().setProperties(new TCFExporterProperties());

		/* start mapper */
		getFixture().setDocument(sDocument);
		getFixture().mapSDocument();

		/* tests */
		File fixFile = new File(getFixture().getResourceURI().toFileString());
		BufferedReader reader = new BufferedReader(new FileReader(fixFile));
		assertEquals(outStream.toString(), reader.readLine());
		reader.close();
	}

	@Test
	public void testPOS() throws XMLStreamException, IOException {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		XMLOutputFactory o = XMLOutputFactory.newFactory();
		XMLStreamWriter xmlWriter = o.createXMLStreamWriter(outStream);

		/* creating TCF */
		writeStartAndPrimaryText(xmlWriter);
		writeTokens(xmlWriter);
		xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_POSTAGS, TCFDictionary.NS_VALUE_TC);
		xmlWriter.writeAttribute(TCFDictionary.ATT_TAGSET, "stts");

		xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TAG, TCFDictionary.NS_VALUE_TC);
		xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "pt_1");
		xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t_1");
		xmlWriter.writeCharacters("VBZ");
		xmlWriter.writeEndElement();

		xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TAG, TCFDictionary.NS_VALUE_TC);
		xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "pt_2");
		xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t_2");
		xmlWriter.writeCharacters("DT");
		xmlWriter.writeEndElement();

		xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TAG, TCFDictionary.NS_VALUE_TC);
		xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "pt_3");
		xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t_3");
		xmlWriter.writeCharacters("NN");
		xmlWriter.writeEndElement();

		xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TAG, TCFDictionary.NS_VALUE_TC);
		xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "pt_4");
		xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t_4");
		xmlWriter.writeCharacters("RBR");
		xmlWriter.writeEndElement();

		xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TAG, TCFDictionary.NS_VALUE_TC);
		xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "pt_5");
		xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t_5");
		xmlWriter.writeCharacters("JJ");
		xmlWriter.writeEndElement();

		xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TAG, TCFDictionary.NS_VALUE_TC);
		xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "pt_6");
		xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t_6");
		xmlWriter.writeCharacters("IN");
		xmlWriter.writeEndElement();

		xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TAG, TCFDictionary.NS_VALUE_TC);
		xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "pt_7");
		xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t_7");
		xmlWriter.writeCharacters("PRP");
		xmlWriter.writeEndElement();

		xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TAG, TCFDictionary.NS_VALUE_TC);
		xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "pt_8");
		xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t_8");
		xmlWriter.writeCharacters("VBZ");
		xmlWriter.writeEndElement();

		xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TAG, TCFDictionary.NS_VALUE_TC);
		xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "pt_9");
		xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t_9");
		xmlWriter.writeCharacters("TO");
		xmlWriter.writeEndElement();

		xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TAG, TCFDictionary.NS_VALUE_TC);
		xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "pt_10");
		xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t_10");
		xmlWriter.writeCharacters("VB");
		xmlWriter.writeEndElement();

		xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TAG, TCFDictionary.NS_VALUE_TC);
		xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "pt_11");
		xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t_11");
		xmlWriter.writeCharacters(".");
		xmlWriter.writeEndElement();

		xmlWriter.writeEndElement();// end of postags
		xmlWriter.writeEndElement();// end of textcorpus
		xmlWriter.writeEndDocument();

		/* creating SDocument */
		SDocument sDocument = SaltFactory.createSDocument();
		sDocument.setName(SNAME_TEST_POS);
		sDocument.createMetaAnnotation(null, TCFDictionary.ATT_LANG, "en");
		SampleGenerator.createPrimaryData(sDocument, SampleGenerator.LANG_EN);
		SampleGenerator.createTokens(sDocument);
		SampleGenerator.createMorphologyAnnotations(sDocument);
		SDocumentGraph sDocGraph = sDocument.getDocumentGraph();

		/* setting variables */
		getFixture().setResourceURI(URI.createFileURI(System.getProperty("java.io.tmpdir") + FOLDER_PEPPER_TEST + SNAME_TEST_POS));
		TCFExporterProperties properties = new TCFExporterProperties();
		properties.setPropertyValue(TCFExporterProperties.PROP_POS_QNAME, SaltUtil.createQName(SaltUtil.SALT_NAMESPACE, SaltUtil.SEMANTICS_POS));
		getFixture().setProperties(properties);

		/* start mapper */
		getFixture().setDocument(sDocument);
		getFixture().mapSDocument();

		/* tests */
		File fixFile = new File(getFixture().getResourceURI().toFileString());
		BufferedReader reader = new BufferedReader(new FileReader(fixFile));
		assertEquals(outStream.toString(), reader.readLine());
		reader.close();
	}
}
